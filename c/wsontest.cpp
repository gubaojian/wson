//
//  WsonTest.c
//  JavaScriptCore
//
//  Created by furture on 2017/12/27.
//
#include "config.h"

#include "ArrayBuffer.h"
#include "ArrayPrototype.h"
#include "BuiltinNames.h"
#include "ButterflyInlines.h"
#include "CatchScope.h"
#include "CodeBlock.h"
#include "Completion.h"
#include "ConfigFile.h"
#include "Disassembler.h"
#include "Exception.h"
#include "ExceptionHelpers.h"
#include "HeapProfiler.h"
#include "HeapSnapshotBuilder.h"
#include "InitializeThreading.h"
#include "Interpreter.h"
#include "JIT.h"
#include "JSArray.h"
#include "JSArrayBuffer.h"
#include "JSCInlines.h"
#include "JSFunction.h"
#include "JSInternalPromise.h"
#include "JSInternalPromiseDeferred.h"
#include "JSLock.h"
#include "JSModuleLoader.h"
#include "JSNativeStdFunction.h"
#include "JSONObject.h"
#include "JSSourceCode.h"
#include "JSString.h"
#include "JSTypedArrays.h"
#include "JSWebAssemblyInstance.h"
#include "JSWebAssemblyMemory.h"
#include "LLIntData.h"
#include "LLIntThunks.h"
#include "ObjectConstructor.h"
#include "ParserError.h"
#include "ProfilerDatabase.h"
#include "PromiseDeferredTimer.h"
#include "ProtoCallFrame.h"
#include "ReleaseHeapAccessScope.h"
#include "SamplingProfiler.h"
#include "StackVisitor.h"
#include "StructureInlines.h"
#include "StructureRareDataInlines.h"
#include "SuperSampler.h"
#include "TestRunnerUtils.h"
#include "TypedArrayInlines.h"
#include "WasmContext.h"
#include "WasmFaultSignalHandler.h"
#include "WasmMemory.h"
#include <locale.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <thread>
#include <type_traits>
#include <wtf/CommaPrinter.h>
#include <wtf/CurrentTime.h>
#include <wtf/MainThread.h>
#include <wtf/NeverDestroyed.h>
#include <wtf/StringPrintStream.h>
#include <wtf/text/StringBuilder.h>

#if OS(WINDOWS)
#include <direct.h>
#include <wtf/text/win/WCharStringExtras.h>
#else
#include <unistd.h>
#endif

#if PLATFORM(COCOA)
#include <crt_externs.h>
#endif

#if HAVE(READLINE)
// readline/history.h has a Function typedef which conflicts with the WTF::Function template from WTF/Forward.h
// We #define it to something else to avoid this conflict.
#define Function ReadlineFunction
#include <readline/history.h>
#include <readline/readline.h>
#undef Function
#endif

#if HAVE(SYS_TIME_H)
#include <sys/time.h>
#endif

#if HAVE(SIGNAL_H)
#include <signal.h>
#endif

#if COMPILER(MSVC)
#include <crtdbg.h>
#include <mmsystem.h>
#include <windows.h>
#endif

#if PLATFORM(IOS) && CPU(ARM_THUMB2)
#include <fenv.h>
#include <arm/arch.h>
#endif

#define BENCH_WSON_VS_JSON true

#include "wsonjsc.h"
#include "wsonjsonbenchmark.h"


using namespace JSC;
using namespace WTF;

static char* createStringWithContentsOfFile(const char* fileName);
EncodedJSValue JSC_HOST_CALL functionReadFile(ExecState* exec);
EncodedJSValue JSC_HOST_CALL functionQuit(ExecState* exec);
EncodedJSValue JSC_HOST_CALL functionLog(ExecState* exec);
static EncodedJSValue JSC_HOST_CALL functionToWson(ExecState* exec);
static EncodedJSValue JSC_HOST_CALL functionParseWson(ExecState* exec);
static EncodedJSValue JSC_HOST_CALL functionWsonInit(ExecState* exec);
static EncodedJSValue JSC_HOST_CALL functionWsonDestroy(ExecState* exec);
static EncodedJSValue JSC_HOST_CALL functionBenchmark(ExecState* exec);




class GlobalObject;

class GlobalObject : public JSGlobalObject {
private:
    GlobalObject(VM&, Structure*);
    
public:
    typedef JSGlobalObject Base;
    
    static GlobalObject* create(VM& vm, Structure* structure)
    {
        GlobalObject* object = new (NotNull, allocateCell<GlobalObject>(vm.heap)) GlobalObject(vm, structure);
        object->finishCreation(vm);
        return object;
    }
    
    
    DECLARE_INFO;
    
    static Structure* createStructure(VM& vm, JSValue prototype)
    {
        return Structure::create(vm, 0, prototype, TypeInfo(GlobalObjectType, StructureFlags), info());
    }
    
    static RuntimeFlags javaScriptRuntimeFlags(const JSGlobalObject*) { return RuntimeFlags::createAllEnabled(); }
    
protected:
    void finishCreation(VM& vm){
        Base::finishCreation(vm);
        addFunction(vm, "readFile", functionReadFile, 2);
        addFunction(vm, "quit", functionQuit, 1);
        addFunction(vm, "log", functionLog, 1);
        addFunction(vm, "wsonInit", functionWsonInit, 0);
        addFunction(vm, "wsonDestroy", functionWsonDestroy, 0);
        addFunction(vm, "toWson", functionToWson, 1);
        addFunction(vm, "parseWson", functionParseWson, 1);
        addFunction(vm, "wsonJsonBenchmark", functionBenchmark, 1);
    }
    
    void addFunction(VM& vm, JSObject* object, const char* name, NativeFunction function, unsigned arguments)
    {
        Identifier identifier = Identifier::fromString(&vm, name);
        object->putDirect(vm, identifier, JSFunction::create(vm, this, arguments, identifier.string(), function));
    }
    
    void addFunction(VM& vm, const char* name, NativeFunction function, unsigned arguments)
    {
        addFunction(vm, this, name, function, arguments);
    }
};

const ClassInfo GlobalObject::s_info = { "global", &JSGlobalObject::s_info, nullptr, nullptr, CREATE_METHOD_TABLE(GlobalObject) };

GlobalObject::GlobalObject(VM& vm, Structure* structure)
: JSGlobalObject(vm, structure){
}





static RefPtr<Uint8Array> fillBufferWithContentsOfFile(FILE* file)
{
    fseek(file, 0, SEEK_END);
    size_t bufferCapacity = ftell(file);
    fseek(file, 0, SEEK_SET);
    RefPtr<Uint8Array> result = Uint8Array::create(bufferCapacity);
    size_t readSize = fread(result->data(), 1, bufferCapacity, file);
    if (readSize != bufferCapacity)
        return nullptr;
    return result;
}


static char* createStringWithContentsOfFile(const char* fileName)
{
    char* buffer;
    
    size_t buffer_size = 0;
    size_t buffer_capacity = 1024;
    buffer = (char*)malloc(buffer_capacity);
    
    FILE* f = fopen(fileName, "r");
    if (!f) {
        fprintf(stderr, "Could not open file: %s\n", fileName);
        free(buffer);
        return 0;
    }
    
    while (!feof(f) && !ferror(f)) {
        buffer_size += fread(buffer + buffer_size, 1, buffer_capacity - buffer_size, f);
        if (buffer_size == buffer_capacity) { /* guarantees space for trailing '\0' */
            buffer_capacity *= 2;
            buffer = (char*)realloc(buffer, buffer_capacity);
            ASSERT(buffer);
        }
        
        ASSERT(buffer_size < buffer_capacity);
    }
    fclose(f);
    buffer[buffer_size] = '\0';
    
    return buffer;
}

static RefPtr<Uint8Array> fillBufferWithContentsOfFile(const String& fileName)
{
    FILE* f = fopen(fileName.utf8().data(), "rb");
    if (!f) {
        fprintf(stderr, "Could not open file: %s\n", fileName.utf8().data());
        return nullptr;
    }
    
    RefPtr<Uint8Array> result = fillBufferWithContentsOfFile(f);
    fclose(f);
    
    return result;
}

EncodedJSValue JSC_HOST_CALL functionQuit(ExecState* exec)
{
    
    String desc = exec->argument(0).toWTFString(exec);
    
    printf("failed test %s", desc.utf8().data());
    
    exit(EXIT_FAILURE);
#if COMPILER(MSVC)
    // Without this, Visual Studio will complain that this method does not return a value.
    return JSValue::encode(jsUndefined());
#endif
}

EncodedJSValue JSC_HOST_CALL functionLog(ExecState* exec){
     String desc = exec->argument(0).toWTFString(exec);
     printf("%s\n", desc.utf8().data());
     return JSValue::encode(jsUndefined());
}




EncodedJSValue JSC_HOST_CALL functionReadFile(ExecState* exec)
{
    VM& vm = exec->vm();
    auto scope = DECLARE_THROW_SCOPE(vm);
    
    String fileName = exec->argument(0).toWTFString(exec);
    RETURN_IF_EXCEPTION(scope, encodedJSValue());
    
    bool isBinary = false;
    if (exec->argumentCount() > 1) {
        String type = exec->argument(1).toWTFString(exec);
        RETURN_IF_EXCEPTION(scope, encodedJSValue());
        if (type != "binary")
            return throwVMError(exec, scope, "Expected 'binary' as second argument.");
        isBinary = true;
    }
    
    RefPtr<Uint8Array> content = fillBufferWithContentsOfFile(fileName);
    if (!content)
        return throwVMError(exec, scope, "Could not open file.");
    
    if (!isBinary)
        return JSValue::encode(jsString(exec, String::fromUTF8WithLatin1Fallback(content->data(), content->length())));
    
    Structure* structure = exec->lexicalGlobalObject()->typedArrayStructure(TypeUint8);
    JSObject* result = JSUint8Array::create(vm, structure, WTFMove(content));
    RETURN_IF_EXCEPTION(scope, encodedJSValue());
    
    return JSValue::encode(result);
}


int main(int argc, char* argv[]){
    const char *scriptPath = "wsontest.js";
    if (argc > 1) {
        scriptPath = argv[1];
    }
    
    WTF::initializeMainThread();
    JSC::initializeThreading();
#if ENABLE(WEBASSEMBLY)
    JSC::Wasm::enableFastMemory();
#endif
    
    Options::useJIT();
    VM& vm = VM::create(LargeHeap).leakRef();
    
    {
        JSLockHolder locker(vm);
        GlobalObject* globalObject = GlobalObject::create(vm, GlobalObject::createStructure(vm, jsNull()));
        globalObject->setRemoteDebuggingEnabled(true);
        
        String code = String::fromUTF8(createStringWithContentsOfFile(scriptPath));
        String sourceURLString = String::fromUTF8(scriptPath);
        SourceCode source = makeSource(code, SourceOrigin { sourceURLString }, sourceURLString, TextPosition(OrdinalNumber::fromOneBasedInt(0), OrdinalNumber()));
        
        NakedPtr<Exception> evaluationException;
        JSValue returnValue = profiledEvaluate(globalObject->globalExec(), ProfilingReason::API, source, JSValue(), evaluationException);
        
        if (evaluationException) {
            printf("run error %s\n", evaluationException->value().toString(globalObject->globalExec())->value(globalObject->globalExec()).utf8().data());
            exit(EXIT_FAILURE);
        }
        
        JSString* string = returnValue.toString(globalObject->globalExec());
        String result = string->value(globalObject->globalExec());
        
        printf("run success %s \n", result.utf8().data());
        
        
        vm.drainMicrotasks();
    }
    
    vm.promiseDeferredTimer->runRunLoop();
};


EncodedJSValue JSC_HOST_CALL functionWsonInit(ExecState* exec){
    wson::init(&exec->vm());
    return JSValue::encode(jsBoolean(true));
}

EncodedJSValue JSC_HOST_CALL functionWsonDestroy(ExecState* exec){
    if(exec){
       wson::destory();
    }
    return JSValue::encode(jsBoolean(true));
}

EncodedJSValue JSC_HOST_CALL functionParseWson(ExecState* exec)
{
    VM& vm = exec->vm();
    auto scope = DECLARE_THROW_SCOPE(vm);
    JSValue value = exec->argument(0);
    RETURN_IF_EXCEPTION(scope, encodedJSValue());
    if(!value.isObject()){
        printf("error type wson in parse wson \n");
        return JSValue::encode(jsNull());
    }
    JSUint8Array* array = (JSUint8Array*)asObject(value);
    uint8_t* ptr = array->typedVector();
    wson_buffer* buffer = wson_buffer_from(ptr, array->byteLength());
    JSValue result = wson::toJSValue(exec, buffer);
    return JSValue::encode(result);
}


EncodedJSValue JSC_HOST_CALL functionToWson(ExecState* exec)
{
    VM& vm = exec->vm();
    auto scope = DECLARE_THROW_SCOPE(vm);
    JSValue value = exec->argument(0);
    RETURN_IF_EXCEPTION(scope, encodedJSValue());
    wson_buffer* buffer = wson::toWson(exec, value);;
    Structure* structure = exec->lexicalGlobalObject()->typedArrayStructure(TypeUint8);
    auto length = buffer->position;
    const void* data = buffer->data;
    JSObject* result = createUint8TypedArray(exec, structure, ArrayBuffer::createFromBytes(data, length, [] (void* p) {
        free(p); }), 0, length);
    RETURN_IF_EXCEPTION(scope, encodedJSValue());
    buffer->data = NULL;
    free(buffer);
    return JSValue::encode(result);
}

EncodedJSValue JSC_HOST_CALL functionBenchmark(ExecState* exec){
    JSValue value  = exec->argument(0);
    if(value.isString()){
        value = JSONParse(exec, value.toWTFString(exec));
    }
    benchWsonVsJson(exec, value);
    return JSValue::encode(jsUndefined());
}



