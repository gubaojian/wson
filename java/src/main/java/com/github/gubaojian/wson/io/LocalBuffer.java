package com.github.gubaojian.wson.io;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 复用buffer. 线程本地私有存储，线程安全。
 * 首先，Java 虚拟机可能会对每个线程的ThreadLocal 数组进行限制，即每个线程最多能够持有多少个ThreadLocal 变量。 这个限制可以通过JVM 的参数进行调整，例如 -XX:ThreadLocalVariables 。 在常见的JVM 实现中，这个数量一般在1024-65536 之间
 */
public class LocalBuffer {
    private final static ThreadLocal<LocalRef> local = new ThreadLocal<LocalRef>();

    /**
     * 获取缓存的buffer
     */
    public static final byte[] requireBuffer(int bufferSize) {
        LocalRef localRef = local.get();
        byte[] buffer = null;
        if (localRef != null) {
            WeakReference<byte[]> reference = localRef.bufferRef;
            if (reference != null) {
                buffer = reference.get();
                if (buffer != null) {
                    localRef.bufferRef = null;
                }
            }
        }
        if (buffer == null) {
            buffer = new byte[bufferSize];
        }
        return buffer;
    }

    public static final void returnBuffer(byte[] buffer) {
        returnBuffer(buffer, 128 * 1024);
    }
    /**
     * 归还buffer
     */
    public static final void returnBuffer(byte[] buffer, int maxCacheSize) {
        if (buffer.length <= maxCacheSize) {
            LocalRef cacheRef = local.get();
            if (cacheRef == null) {
                cacheRef = new LocalRef();
                local.set(cacheRef);
            }
            cacheRef.bufferRef = new WeakReference<>(buffer);
        }
    }


    /**
     * 复用度很高，做个缓存，复用一下。
     * 数组不大， 直接遍历效率更高
     * 可能比set计算hash好点。获取缓存的set, 复用对象，
     */
    public static final ArrayList requireArrayList() {
        LocalRef localRef = local.get();
        ArrayList list = null;
        if (localRef != null) {
            WeakReference<ArrayList> reference = localRef.arrayListRef;
            if (reference != null) {
                list = reference.get();
                if (list != null) {
                    localRef.bufferRef = null;
                }
            }
        }
        if (list == null) {
            list = new ArrayList();
        }
        return list;
    }

    /**
     * 归还list
     */
    public static final void returnList(ArrayList list) {
        if (list != null) {
            if (!list.isEmpty()) {
                list.clear();
            }
            LocalRef cacheRef = local.get();
            if (cacheRef == null) {
                cacheRef = new LocalRef();
                local.set(cacheRef);
            }
            cacheRef.arrayListRef = new WeakReference<>(list);
        }
    }

    /**
     * 保持引用不变，避免频繁创建WeakReference，进而频繁计算hash更新map，又可以把多个thread local合并到一起。
     * 数据发生变化时仅更新ref，保持map不变，仅进行查找查找thread local的 map
     * 当然也可以直接weakreference差别其实不大,
     * 可以把多个缓存合并到一起减少thread local使用。
     * 首先，Java 虚拟机可能会对每个线程的ThreadLocal 数组进行限制，
     * 即每个线程最多能够持有多少个ThreadLocal 变量。 这个限制可以通过JVM 的参数进行调整，例如 -XX:ThreadLocalVariables 。 在常见的JVM 实现中，这个数量一般在1024-65536 之间
     * https://github.com/search?q=repo%3Aalibaba%2Ffastjson%20ThreadLocal&type=code
     * 多个合并成一个。
     */
    public static class LocalRef {
        public WeakReference<byte[]> bufferRef;
        public WeakReference<ArrayList> arrayListRef;
    }

}
