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
#include "BooleanObject.h"
#include "tson.h"

using namespace JSC;


namespace tson {
    tson_buffer* toTson(ExecState* state, JSValue val);
    JSValue toJSValue(ExecState* state, tson_buffer* buffer);

    /**
     * performance improve tson toJSValue. very big import improve
     */
    void init(VM* vm);
    void destory();
}



#endif /* tsonjsc_hpp */
