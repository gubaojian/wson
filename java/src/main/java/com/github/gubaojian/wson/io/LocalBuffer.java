package com.github.gubaojian.wson.io;

import java.lang.ref.WeakReference;

/**
 * 复用buffer. 线程本地私有存储
 * */
public class LocalBuffer {
    private final static ThreadLocal<WeakReference<byte[]>> bufLocal = new ThreadLocal<WeakReference<byte[]>>();
    /**
     * 获取缓存的buffer
     * */
    public static final byte[] requireBuffer(int bufferSize) {
        WeakReference<byte[]> reference = bufLocal.get();
        byte[] buffer = null;
        if (reference != null) {
            buffer =  reference.get();
            if (buffer != null) {
                bufLocal.set(null);
            }
        }
        if (buffer == null) {
            buffer = new byte[bufferSize];
        }
        return  buffer;
    }

    /**
     * 归还buffer
     * */
    public static final void returnBuffer(byte[] buffer, int maxCacheSize) {
        if(buffer.length <= maxCacheSize){
            bufLocal.set(new WeakReference<>(buffer));
        }
    }

}
