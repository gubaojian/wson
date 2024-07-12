package com.github.gubaojian.wson.io;

import java.nio.ByteOrder;

public class Platform {

    /**
     * cache json property key, most of them all same
     * */
   public static final int GLOBAL_STRING_CACHE_SIZE = 2*1024;
   public static final ThreadLocal<char[]> localCharsBufferCache = new ThreadLocal<>();
   public static final String[] globalStringBytesCache = new String[GLOBAL_STRING_CACHE_SIZE];
    /**
     * StringUTF-16, byte order with native byte order
     * */
   public static final boolean IS_NATIVE_LITTLE_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

}
