package com.github.gubaojian.wson.io;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 复用buffer. 线程本地私有存储
 * */
public class LocalBuffer {
    private final static ThreadLocal<WeakReference<byte[]>> bufLocal = new ThreadLocal<WeakReference<byte[]>>();
    private final static ThreadLocal<WeakReference<ArrayList>> listLocal = new ThreadLocal<WeakReference<ArrayList>>();

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


    /**
     * 获取缓存的set, 复用对象，
     * */
    public static final ArrayList requireArrayList() {
        WeakReference<ArrayList> reference = listLocal.get();
        ArrayList list = null;
        if (reference != null) {
            list =  reference.get();
            if (list != null) {
                bufLocal.set(null);
            }
        }
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }

    /**
     * 归还list
     * */
    public static final void returnList(ArrayList list) {
        if(list != null){
            if (!list.isEmpty()) {
                list.clear();
            }
            listLocal.set(new WeakReference<>(list));
        }
    }

}
