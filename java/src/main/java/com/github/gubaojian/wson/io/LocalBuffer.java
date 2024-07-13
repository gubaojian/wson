package com.github.gubaojian.wson.io;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 复用buffer. 线程本地私有存储
 * */
public class LocalBuffer {
    private final static ThreadLocal<LocalCacheRef<byte[]>> bufLocal = new ThreadLocal<LocalCacheRef<byte[]>>();
    private final static ThreadLocal<LocalCacheRef<ArrayList>> listLocal = new ThreadLocal<LocalCacheRef<ArrayList>>();

    /**
     * 获取缓存的buffer
     * */
    public static final byte[] requireBuffer(int bufferSize) {
        LocalCacheRef<byte[]> cacheRef = bufLocal.get();
        byte[] buffer = null;
        if (cacheRef != null) {
            WeakReference<byte[]> reference = cacheRef.ref;
            if (reference != null) {
                buffer =  reference.get();
                if (buffer != null) {
                    cacheRef.ref = null;
                }
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
            LocalCacheRef cacheRef = bufLocal.get();
            if (cacheRef == null) {
                cacheRef = new LocalCacheRef();
                bufLocal.set(cacheRef);
            }
            cacheRef.ref = new WeakReference<>(buffer);
        }
    }


    /**
     * 复用度很高，做个缓存，复用一下。
     * 数组不大， 直接遍历效率更高
     * 可能比set计算hash好点。获取缓存的set, 复用对象，
     * */
    public static final ArrayList requireArrayList() {
        LocalCacheRef<ArrayList> cacheRef = listLocal.get();
        ArrayList list = null;
        if (cacheRef != null) {
            WeakReference<ArrayList> reference = cacheRef.ref;
            if (reference != null) {
                list =  reference.get();
                if (list != null) {
                    cacheRef.ref = null;
                }
            }
        }
        if (list == null) {
            list = new ArrayList();;
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
            LocalCacheRef<ArrayList> cacheRef = listLocal.get();
            if (cacheRef == null) {
                cacheRef = new LocalCacheRef<>();
                listLocal.set(cacheRef);
            }
            cacheRef.ref = new WeakReference<>(list);
        }
    }

    /**
     * 保持引用不变，避免频繁创建WeakReference，进而频繁计算hash更新map，
     * 数据发生变化时仅更新ref，保持map不变，仅进行查找查找thread local的 map
     * 当然也可以直接weakreference差别其实不大
     * */
    public static class LocalCacheRef<T> {
        public WeakReference<T> ref;

    }

}
