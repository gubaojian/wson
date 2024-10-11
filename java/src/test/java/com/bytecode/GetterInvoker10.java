package com.bytecode;

public class GetterInvoker10 implements Invoker {
    public Getter getter;

    @Override
    public Object invokeGetFast() {
        return getter.getName();
    }
}
