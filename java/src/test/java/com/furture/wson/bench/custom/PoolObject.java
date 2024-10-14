package com.furture.wson.bench.custom;


import java.util.concurrent.atomic.AtomicBoolean;



public class PoolObject<T> {
    public final AtomicBoolean checkOwn = new AtomicBoolean(false);
    public final T object;
    public final int index;

    public PoolObject(T object, int index) {
        this.object = object;
        this.index = index;
    }

    public  T getObject() {
        return  object;
    }
}