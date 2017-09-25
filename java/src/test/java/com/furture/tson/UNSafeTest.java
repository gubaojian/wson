package com.furture.tson;

import junit.framework.TestCase;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/25.
 */
public class UNSafeTest extends TestCase {



    public void  testUnsafeTest(){
        Unsafe unsafe = getUnsafe();
        long start = 0;
        long end = 0;


        int[] bts = new int[1024];


        start = System.currentTimeMillis();
        for(int i=0; i<1000000; i++){
            unsafe.putInt(bts, (long)1000, i^3);
        }
        end = System.currentTimeMillis();

        System.out.println((end - start));


        start = System.currentTimeMillis();
        for(int i=0; i<1000000; i++){
            bts[1000] = i^3;
        }
        end = System.currentTimeMillis();
        System.out.println((end - start));


    }


    /**
     * Gets the {@code sun.misc.Unsafe} instance, or {@code null} if not available on this platform.
     */
    private static sun.misc.Unsafe getUnsafe() {
        sun.misc.Unsafe unsafe = null;
        try {
            unsafe =
                    AccessController.doPrivileged(
                            new PrivilegedExceptionAction<Unsafe>() {
                                @Override
                                public sun.misc.Unsafe run() throws Exception {
                                    Class<sun.misc.Unsafe> k = sun.misc.Unsafe.class;

                                    for (Field f : k.getDeclaredFields()) {
                                        f.setAccessible(true);
                                        Object x = f.get(null);
                                        if (k.isInstance(x)) {
                                            return k.cast(x);
                                        }
                                    }
                                    // The sun.misc.Unsafe field does not exist.
                                    return null;
                                }
                            });
        } catch (Throwable e) {
            // Catching Throwable here due to the fact that Google AppEngine raises NoClassDefFoundError
            // for Unsafe.
        }
        return unsafe;
    }
}
