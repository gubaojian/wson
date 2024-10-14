package com.furture.wson.bench.custom;

import java.util.concurrent.atomic.AtomicInteger;

public final class LockFreePool<T> {
    private final AtomicInteger curPos = new AtomicInteger(Integer.MAX_VALUE - 2);
    private final PoolObject<T>[] pools;
    private final int poolSize;
    private ObjectCreator<T> creator;

    public LockFreePool(int suggestSize, ObjectCreator<T> creator) {
        int minSize = Runtime.getRuntime().availableProcessors();
        suggestSize = Math.max(minSize, suggestSize);
        this.pools = new PoolObject[suggestSize];
        this.poolSize = suggestSize;
        this.creator = creator;
        for(int i=0; i<poolSize; i++) {
            pools[i] = new PoolObject<>(creator.createObject(), i);
        }
    }

    public final PoolObject<T> getPoolObject() {
        int pos = curPos.incrementAndGet();
        int index = pos%poolSize; //get index in pool
        if (index < 0) {
             index = (poolSize + index);
        }
        PoolObject<T> poolObject = pools[index]; //most only one thread can visit this index.
        if (poolObject != null) { //add double check for this.
             boolean hasOneOwn = poolObject.checkOwn.compareAndExchange(false, true);
             if (!hasOneOwn) {
                 pools[index] = null;
                 return  poolObject;
             }
        }
        return new PoolObject<>(creator.createObject(), -1);
    }

    public final void returnPoolObject(PoolObject<T> object) {
        if (object.index >= 0) {
             object.checkOwn.set(false); //only one thread own, so none need compare and exchange, just set value.
             pools[object.index] = object;
        }
    }

}

