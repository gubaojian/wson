package com.bytecode;

public class GetterInvoker  implements Invoker  {
    public Getter getter;

    @Override
    public Object invokeGetFast() {
        return getter.getName();
    }
}
