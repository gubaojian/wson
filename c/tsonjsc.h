//
//  tsonjsc.hpp
//  JavaScriptCore
//
//  Created by furture on 2017/8/30.
//
//

#ifndef tsonjsc_h
#define tsonjsc_h
#include "config.h"
#include "JSCInlines.h"
#include "PropertyNameArray.h"
#include "IdentifierInlines.h"
#include "LocalScope.h"
#include "tson.h"

using namespace JSC;


namespace tson {
    tson_buffer* toTson(ExecState* state, JSValue val);
    JSValue toJSValue(ExecState* state, tson_buffer* buffer);
}





#endif /* tsonjsc_hpp */
