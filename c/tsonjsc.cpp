//
//  tsonjsc.cpp
//  JavaScriptCore
//
//  Created by furture on 2017/8/30.
//
//

#include "tsonjsc.h"
#include "ObjectConstructor.h"
#include <wtf/Vector.h>
#include <wtf/HashMap.h>

using namespace JSC;

/**
 * max deep, like JSONObject.cpp's maximumFilterRecursion default 40000
 */
#define TSON_MAX_DEEP  40000
#define TSON_SYSTEM_IDENTIFIER_CACHE_COUNT (1024*4)
#define TSON_LOCAL_IDENTIFIER_CACHE_COUNT 64

namespace tson {
    struct IdentifierCache{
        Identifier identifer = Identifier::EmptyIdentifier;
        size_t length = 0;
        uint32_t key = 0;
        const char* utf8 = NULL;
    };

    static  IdentifierCache* systemIdentifyCache = nullptr;
    static  VM* systemIdentifyCacheVM = nullptr;
    void tson_push_js_value(ExecState* exec, JSValue val, tson_buffer* buffer, Vector<JSObject*, 16>& objectStack);
    JSValue tson_to_js_value(ExecState* state, tson_buffer* buffer, IdentifierCache* localIdentifiers);
    inline void tson_push_js_string(ExecState* exec,  JSValue val, tson_buffer* buffer);
    inline void tson_push_js_identifier(Identifier val, tson_buffer* buffer);




    tson_buffer* toTson(ExecState* exec, JSValue val){
        tson_buffer* buffer = tson_buffer_new();
        LocalScope localScope(exec->vm());
        Vector<JSObject*, 16> objectStack;
        tson_push_js_value(exec, val, buffer, objectStack);
        return buffer;
    }

    /**
     * if has system cache, local cache size 64, if not, local cache size 128
     */
    JSValue toJSValue(ExecState* exec, tson_buffer* buffer){
        VM& vm =exec->vm();
        LocalScope scope(vm);
        if(systemIdentifyCacheVM && systemIdentifyCacheVM == &vm){
            IdentifierCache localIdentifiers[TSON_LOCAL_IDENTIFIER_CACHE_COUNT];
            return tson_to_js_value(exec, buffer, localIdentifiers);
        }
        IdentifierCache localIdentifiers[TSON_LOCAL_IDENTIFIER_CACHE_COUNT*2];
        return tson_to_js_value(exec, buffer, localIdentifiers);
    }


    void init(VM* vm){
        if(systemIdentifyCache){
            destory();
        }
        if(systemIdentifyCacheVM){
            systemIdentifyCacheVM = nullptr;
        }
        systemIdentifyCache = new IdentifierCache[TSON_SYSTEM_IDENTIFIER_CACHE_COUNT];
        for(int i=0; i<TSON_SYSTEM_IDENTIFIER_CACHE_COUNT; i++){
            systemIdentifyCache[i].identifer = vm->propertyNames->nullIdentifier;
            systemIdentifyCache[i].length = 0;
            systemIdentifyCache[i].utf8 = NULL;
            systemIdentifyCache[i].key = 0;
        }
        systemIdentifyCacheVM  = vm;
    }


    void destory(){
        if(systemIdentifyCache){
            for(int i=0; i<TSON_SYSTEM_IDENTIFIER_CACHE_COUNT; i++){
                if(systemIdentifyCache[i].length > 0){
                    if(systemIdentifyCache[i].utf8){
                        free((void*)(systemIdentifyCache[i].utf8));
                        systemIdentifyCache[i].utf8 = NULL;
                    }
                    systemIdentifyCache[i].key = 0;
                    systemIdentifyCache[i].length = 0;
                }
            }
            delete[] systemIdentifyCache; 
            systemIdentifyCache = nullptr;
        }
        if(systemIdentifyCacheVM){
            systemIdentifyCacheVM = nullptr;
        }
    }



    /**
     * check is circle reference and  max deep
     */
    inline bool check_js_deep_and_circle_reference(JSObject* object, Vector<JSObject*, 16>& objectStack){
        for (unsigned i = 0; i < objectStack.size(); i++) {
            if (objectStack.at(i) == object) {
                return true;
            }
        }
        if(objectStack.size() > TSON_MAX_DEEP){
            return true;
        }
        return false;
    }

    /**
      * djb2-hash-function
      */
    inline uint32_t hash(const char* utf8, const int length){
        uint32_t  hash = 5381;
        for(int i=0;i<length; i++){
            hash = ((hash << 5) + hash) + utf8[i];
        }
        return hash;
    }

    inline uint32_t hash2(const char* utf8, const int length){
        uint32_t  hash = 5381;
        hash = ((hash << 5) + hash) + utf8[0];
        if(length > 1){
            hash = ((hash << 5) + hash) + utf8[length-1];
        }
        return hash;
    }

    /**
     * most of json identifer is repeat, cache can improve performance
     */
    inline Identifier makeIdentifer(VM* vm, IdentifierCache* localIdentifiers, const char* utf8, size_t length){
        if(length <= 0){
           return vm->propertyNames->emptyIdentifier;
        }
        if (utf8[0] <= 0 || utf8[0] >= 127 || length > 32){//only cache short identifier
            String string =  String::fromUTF8(utf8, length);
            return  Identifier::fromString(vm, string);
        }
        uint32_t key = 0;
        uint32_t systemIdentifyCacheIndex = 0;
        IdentifierCache cache;
        bool saveGlobal = false;
        uint32_t localIndex = 0;
        if(systemIdentifyCacheVM && systemIdentifyCacheVM == vm){
            key = hash(utf8, length);
            systemIdentifyCacheIndex = (TSON_SYSTEM_IDENTIFIER_CACHE_COUNT - 1)&key;
            localIndex = (TSON_LOCAL_IDENTIFIER_CACHE_COUNT - 1) & key;
            if(systemIdentifyCache != nullptr){
                cache = systemIdentifyCache[systemIdentifyCacheIndex];
                if(cache.length == length
                   && cache.key == key
                   && strncmp(cache.utf8, utf8, length) == 0
                   && !cache.identifer.isNull()){
                    return cache.identifer;
                }
                if(!cache.utf8 && cache.length == 0){
                    saveGlobal = true;
                }
            }
        }else{
            key = hash2(utf8, length);
            localIndex = (TSON_LOCAL_IDENTIFIER_CACHE_COUNT*2 - 1) & key;
        }

        cache = localIdentifiers[localIndex];
        if(cache.length == length
           && cache.key == key
           && strncmp(cache.utf8, utf8, length) == 0
           && !cache.identifer.isNull()){
            return cache.identifer;
        }

        String string =  String::fromUTF8(utf8, length);
        Identifier identifier = Identifier::fromString(vm, string);
        cache.identifer = identifier;
        cache.length = length;
        cache.key = key;
        if(saveGlobal && length <= 32){
            const char* copy = (const char*)malloc(sizeof(char)*length);
            memcpy((void*)copy, (void*)utf8, length);
            cache.utf8 = copy;
            systemIdentifyCache[systemIdentifyCacheIndex] = cache;
        }else{
            cache.utf8 = utf8;
            localIdentifiers[localIndex] = cache;
        }
        return identifier;
    }

    JSValue tson_to_js_value(ExecState* exec, tson_buffer* buffer,  IdentifierCache* localIdentifiers){
        uint8_t  type = tson_next_type(buffer);
        switch (type) {
            case TSON_STRING_TYPE:{
                    uint32_t length = tson_next_uint(buffer);
                    const char* utf8 = (const char*)tson_next_bts(buffer, length);
                    return jsString(exec, String::fromUTF8(utf8, length));
                }
                break;
            case TSON_ARRAY_TYPE:{
                    uint32_t length = tson_next_uint(buffer);
                    JSArray* array = constructEmptyArray(exec, 0);
                    for(uint32_t i=0; i<length; i++){
                        if(tson_has_next(buffer)){
                            array->putDirectIndex(exec, i, tson_to_js_value(exec, buffer, localIdentifiers));
                        }else{
                            break;
                        }
                    }
                   return array;
                }
                break;
            case TSON_MAP_TYPE:{
                  uint32_t length = tson_next_uint(buffer);
                  JSObject* object = constructEmptyObject(exec);
                  VM& vm = exec->vm();
                  for(uint32_t i=0; i<length; i++){
                      if(tson_has_next(buffer)){
                          int propertyLength = tson_next_uint(buffer);
                          const char* utf8 = (const char*)tson_next_bts(buffer, propertyLength);
                          PropertyName name = makeIdentifer(&vm, localIdentifiers, utf8, propertyLength);
                          if (std::optional<uint32_t> index = parseIndex(name)){
                              object->putDirectIndex(exec, index.value(), tson_to_js_value(exec, buffer, localIdentifiers));
                          }else{
                              object->putDirect(vm, name, tson_to_js_value(exec, buffer, localIdentifiers));
                          }
                      }else{
                          break;
                      }
                   }
                   return object;
                }
                break;
            case TSON_NUMBER_DOUBLE_TYPE:{
                 double  num = tson_next_double(buffer);
                 return  jsNumber(num);
                }
                break;
            case TSON_BOOLEAN_TYPE:{
                bool value = (tson_next_byte(buffer) != 0);
                return jsBoolean(value);
            }
            break;
            case TSON_NUMBER_INT_TYPE:{
                int32_t  num =  tson_next_int(buffer);
                return  jsNumber(num);
            }
            break;
            default:
                break;
        }
        return jsNull();
    }

    void tson_push_js_value(ExecState* exec, JSValue val, tson_buffer* buffer, Vector<JSObject*, 16>& objectStack){
        if(val.isNull() || val.isUndefined()){
            tson_push_type_null(buffer);
            return;
        }

        if(val.isString()){
            tson_push_js_string(exec, val, buffer);
            return;
        }

        if(val.isNumber()){
            if(val.isInt32()){
                tson_push_type_int(buffer, val.asInt32());
            }else{
                double number = val.asNumber();
                tson_push_type_double(buffer, number);
            }
            return;
        }

        if(isJSArray(val)){
            JSArray* array = asArray(val);
            if(check_js_deep_and_circle_reference(array, objectStack)){
                tson_push_type_null(buffer);
                return;
            }
            uint32_t length = array->length();
            tson_push_type_array(buffer, length);
            objectStack.append(array);
            for(uint32_t index=0; index<length; index++){
                JSValue ele = array->getIndex(exec, index);
                tson_push_js_value(exec, ele, buffer, objectStack);
            }
            objectStack.removeLast();
            return;
        }

        if(val.isObject()){
            JSObject* object = asObject(val);
            VM& vm = exec->vm();

            if (object->inherits(vm, StringObject::info())){
                tson_push_js_string(exec, object->toString(exec), buffer);
                return;
            }

            if (object->inherits(vm, NumberObject::info())){
                double number = object->toNumber(exec);
                tson_push_type_double(buffer, number);
                return;
            }

            if (object->inherits(vm, BooleanObject::info())){
                JSValue boolVal =  object->toPrimitive(exec);
                if(boolVal.isTrue()){
                    tson_push_type_boolean(buffer, 1);
                }else{
                    tson_push_type_boolean(buffer, 0);
                }
                return;
            }

            if(check_js_deep_and_circle_reference(object, objectStack)){
                tson_push_type_null(buffer);
                return;
            }
            PropertyNameArray objectPropertyNames(exec, PropertyNameMode::Strings);
            const MethodTable* methodTable = object->methodTable();
            methodTable->getOwnPropertyNames(object, exec, objectPropertyNames, EnumerationMode());
            PropertySlot slot(object, PropertySlot::InternalMethodType::Get);
            uint32_t size = objectPropertyNames.size();
            tson_push_type_map(buffer, size);
            objectStack.append(object);
            for(uint32_t i=0; i<size; i++){
                 Identifier& propertyName = objectPropertyNames[i];
                 tson_push_js_identifier(propertyName , buffer);
                 if(methodTable->getOwnPropertySlot(object, exec, propertyName, slot)){
                     JSValue propertyValue = slot.getValue(exec, propertyName);
                     tson_push_js_value(exec, propertyValue, buffer,objectStack);
                 }else{
                    tson_push_type_null(buffer);
                 }
            }
            objectStack.removeLast();
            return;
        }

        if(val.isBoolean()){
            if(val.isTrue()){
                 tson_push_type_boolean(buffer, 1);
            }else{
                tson_push_type_boolean(buffer, 0);
            }
            return;
        }

#ifdef LOGE
        LOGE("value type is not handled, treat as null");
#endif
        tson_push_type_null(buffer);
    }

    inline void tson_push_js_string(ExecState* exec,  JSValue val, tson_buffer* buffer){
        String s = val.toWTFString(exec);
        CString utf8 = s.utf8();
        int length = utf8.length();
        tson_push_type_string(buffer, utf8.data(), length);
    }

    inline void tson_push_js_identifier(Identifier val, tson_buffer* buffer){
        CString utf8 = val.utf8();
        int length = utf8.length();
        tson_push_property(buffer, utf8.data(), length);
    }
}
