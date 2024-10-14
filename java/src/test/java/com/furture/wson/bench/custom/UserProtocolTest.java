package com.furture.wson.bench.custom;

import com.alibaba.fastjson2.JSON;
import com.furture.wson.domain.User;
import com.github.gubaojian.wson.io.Pool;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class UserProtocolTest  extends TestCase {

    @Test
    public void testUserProtocol() {
        User user = new User();
        user.name = "hello world";
        user.country = "中国";
        byte[] bts = UserProtocol.serialUser(user);
        User back = UserProtocol.deSerialUser(bts);
        System.out.println(back  + " " + user);
        Assert.assertEquals(user, back);
    }


    @Test
    public void testLockFreePool() {
        LockFreePool<byte[]> pool = new LockFreePool<byte[]>(128, () -> new byte[1024]);
        byte[] bts = null;
        Random random = new Random();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            PoolObject<byte[]> poolObject = pool.getPoolObject();
            bts = poolObject.getObject();
            bts[random.nextInt(1024)] = (byte) random.nextInt(128);
            pool.returnPoolObject(poolObject);
        }
        System.out.println("pool used " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            bts = Pool.requireBuffer(1024);
            bts[random.nextInt(1024)] = (byte) random.nextInt(128);
            Pool.returnBuffer(bts);
        }
        System.out.println("thread pool used " + (System.currentTimeMillis() - start));

        System.out.println("first bye value used " + bts[0]);
    }
}
