package com.github.gubaojian.wson.io;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * 复用buffer. 线程本地私有存储，线程安全。
 * 首先，Java 虚拟机可能会对每个线程的ThreadLocal 数组进行限制，即每个线程最多能够持有多少个ThreadLocal 变量。 这个限制可以通过JVM 的参数进行调整，例如 -XX:ThreadLocalVariables 。 在常见的JVM 实现中，这个数量一般在1024-65536 之间
 */
public class Pool {
    private final static ThreadLocal<PoolRef> local = new ThreadLocal<PoolRef>();

    /**
     * 获取缓存的buffer
     */
    public static final byte[] requireBuffer(int bufferSize) {
        PoolRef poolRef = local.get();
        byte[] buffer = null;
        if (poolRef != null) {
            WeakReference<byte[]> reference = poolRef.bufferRef;
            if (reference != null) {
                buffer = reference.get();
                if (buffer != null) {
                    poolRef.bufferRef = null;
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
            PoolRef cacheRef = local.get();
            if (cacheRef == null) {
                cacheRef = new PoolRef();
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
        PoolRef poolRef = local.get();
        ArrayList list = null;
        if (poolRef != null) {
            WeakReference<ArrayList> reference = poolRef.arrayListRef;
            if (reference != null) {
                list = reference.get();
                if (list != null) {
                    poolRef.bufferRef = null;
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
            PoolRef cacheRef = local.get();
            if (cacheRef == null) {
                cacheRef = new PoolRef();
                local.set(cacheRef);
            }
            cacheRef.arrayListRef = new WeakReference<>(list);
        }
    }

    /**
     * 保持引用不变，避免频繁创建WeakReference，进而频繁计算hash更新map，
     * 又可以把多个thread local合并到一起，减少theadlocal对象创建。
     * 数据发生变化时仅更新ref，保持map不变，仅进行查找查找thread local的 map
     * 当然也可以直接weakreference差别其实不大,
     * 可以把多个缓存合并到一起减少thread local使用。
     * 首先，Java 虚拟机可能会对每个线程的ThreadLocal 数组进行限制，
     * 即每个线程最多能够持有多少个ThreadLocal 变量。 这个限制可以通过JVM 的参数进行调整，例如 -XX:ThreadLocalVariables 。 在常见的JVM 实现中，这个数量一般在1024-65536 之间
     * https://github.com/search?q=repo%3Aalibaba%2Ffastjson%20ThreadLocal&type=code
     * 向上面fastjson库中ThreadLocal有10个左右，可以合并成一个，减低内存，提升性能。
     */
    private static class PoolRef {
        //序列化时临时共享缓冲器，共用缓存，减少byte[]创建的开销
        public WeakReference<byte[]> bufferRef;
        //循环对象引用array可以复用。
        public WeakReference<ArrayList> arrayListRef;
    }

}
