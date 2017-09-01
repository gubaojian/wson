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

using namespace JSC;

/**
 * max deep, like JSONObject.cpp's maximumFilterRecursion default 40000
 */
#define TSON_MAX_DEEP  40000
#define TSON_IDENTIFIER_CACHE_COUNT 128

static int miss = 0;
static int hit = 0;
namespace tson {
    struct IdentifierCache{
        Identifier identifer;
        int length;
        const char* utf8;
    };
    
    static unsigned const MaximumCachableCharacter = 128;
    void tson_push_js_value(ExecState* exec, JSValue val, tson_buffer* buffer, Vector<JSObject*, 16>& objectStack);
    JSValue tson_to_js_value(ExecState* state, tson_buffer* buffer, IdentifierCache* recentIdentifiers);
    inline void tson_push_js_string(ExecState* exec,  JSValue val, tson_buffer* buffer);
    inline void tson_push_js_identifier(Identifier val, tson_buffer* buffer);
    inline Identifier  makeIdentifer(ExecState* exec, String& string);
    
    /**
     * check is circle reference or over max deep
     */
    inline bool check_js_circle_reference(JSObject* object, Vector<JSObject*, 16>& objectStack){
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
    inline uint32_t hash1(unsigned char first);
    inline uint32_t hash2(unsigned char first, unsigned char last);
    inline uint32_t hash3(unsigned char first, unsigned char last, unsigned char three);
    
   
    tson_buffer* toTson(ExecState* state, JSValue val){
        tson_buffer* buffer = tson_buffer_new();
        LocalScope localScope(state->vm());
        Vector<JSObject*, 16> objectStack;
        tson_push_js_value(state, val, buffer, objectStack);
        return buffer;
    }
    
    //FIXME 逻辑, deep thread
    JSValue toJSValue(ExecState* exec, tson_buffer* buffer){
        LocalScope scope(exec->vm());
        IdentifierCache recentIdentifiers[TSON_IDENTIFIER_CACHE_COUNT];
        miss = 0;
        hit = 0;
        JSValue  value =  tson_to_js_value(exec, buffer, recentIdentifiers);
        printf("jsvalue hit %d  mis %d  percent %f\n", hit, miss, hit*100.0/(hit + miss + 0.0));
        int count = 0;
        for(int i=0; i<TSON_IDENTIFIER_CACHE_COUNT; i++){
            if(recentIdentifiers[i].length > 0){
                count++;
            }
        }
        printf("jsvalue space used %d \n", count);
        return value;
    }
    
    inline uint32_t hash1(unsigned char first){
        uint32_t  hash = 5381;
        hash = ((hash << 5) + hash) + first;
        return hash;
    }
    

    inline uint32_t hash2(unsigned char first, unsigned char last){
        uint32_t  hash = 5381;
        hash = ((hash << 5) + hash) + first;
        hash = ((hash << 5) + hash) + last;
        return hash;
    }
    inline uint32_t hash3(unsigned char first, unsigned char last, unsigned char three){
        uint32_t  hash = 5381;
        hash = ((hash << 5) + hash) + first;
        hash = ((hash << 5) + hash) + last;
        hash = ((hash << 5) + hash) + three;
        return hash;
    }
    
    inline Identifier makeIdentifer(VM* vm, IdentifierCache* recentIdentifiers, const char* utf8, const int length){
        if(length <= 0){
           return vm->propertyNames->emptyIdentifier;
        }
        unsigned char first = utf8[0];
        if (first >= MaximumCachableCharacter){
            String string =  String::fromUTF8(utf8, length);
            return  Identifier::fromString(vm, string);
        }
        
        uint32_t index;
        if(length == 1){
            index = (TSON_IDENTIFIER_CACHE_COUNT - 1) & hash1(first);
        }else if (length == 2){
           index = (TSON_IDENTIFIER_CACHE_COUNT - 1) &  hash2(first, utf8[length-1]);
        }else{
            index = (TSON_IDENTIFIER_CACHE_COUNT - 1) & hash3(first, utf8[length-1], utf8[length/2]);
        }
        IdentifierCache cache = recentIdentifiers[index];
        if(cache.length == length
           && strncmp(cache.utf8, utf8, length) == 0
           && !cache.identifer.isNull()){
            hit++;
            return cache.identifer;
        }
        String string =  String::fromUTF8(utf8, length);
        Identifier identifier = Identifier::fromString(vm, string);
        cache.identifer = identifier;
        cache.utf8 = utf8;
        cache.length = length;
        recentIdentifiers[index] = cache;
        miss++;
        return identifier;
    }
    
    JSValue tson_to_js_value(ExecState* exec, tson_buffer* buffer,  IdentifierCache* recentIdentifiers){
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
                            array->putDirectIndex(exec, i, tson_to_js_value(exec, buffer, recentIdentifiers));
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
                          PropertyName name = makeIdentifer(&vm, recentIdentifiers, utf8, propertyLength);
                          if (std::optional<uint32_t> index = parseIndex(name)){
                              object->putDirectIndex(exec, index.value(), tson_to_js_value(exec, buffer, recentIdentifiers));
                          }else{
                              object->putDirect(vm, name, tson_to_js_value(exec, buffer, recentIdentifiers));
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
            if(check_js_circle_reference(array, objectStack)){
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
            if(check_js_circle_reference(object, objectStack)){
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
