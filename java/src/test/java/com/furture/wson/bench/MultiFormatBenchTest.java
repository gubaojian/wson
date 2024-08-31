package com.furture.wson.bench;

import com.alibaba.fastjson.JSON;
import com.furture.wson.bench.custom.UserProtocol;
import com.github.gubaojian.wson.Wson;
import com.furture.wson.domain.User;
import junit.framework.TestCase;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class MultiFormatBenchTest extends TestCase {


    public void testFastJson() {
        String json = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "hello world");
            map.put("age", 1);
            json = JSON.toJSONString(map);
        }
        long end = System.currentTimeMillis();
        System.out.println("fastjson toString used " + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JSON.parseObject(json);
        }
        end = System.currentTimeMillis();
        System.out.println("fastjson parse used " + (end - start));
    }


    public void testWson() {
        byte[] data = null;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "hello world");
            map.put("age", 1);
            data = Wson.toWson(map);
        }
        long end = System.currentTimeMillis();

        System.out.println("wson towson used " + (end - start));

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            Wson.parse(data);
        }
        end = System.currentTimeMillis();

        System.out.println("wson parse used " + (end - start));
    }

    public void testSerializable() throws InterruptedException {
        User user = new User();
        user.name = "hello world";
        user.country = "中国";

        System.out.println("user object wson json 1");

        long start = System.currentTimeMillis();
        byte[] wbytes = null;
        for (int i = 0; i < 10000; i++) {
            wbytes = Wson.toWson(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("user object wson towson used " + (end - start) + "length " + wbytes.length);

        String json = null;
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            json = JSON.toJSONString(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object json tojson used " + (end - start) + "length " + json.length());

        ThreadSafeFury fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(true)
                .buildThreadSafeFury();
        fury.register(User.class);
        Thread.sleep(1000);
        byte[] bytes = fury.serialize(user);
        bytes = fury.serialize(user);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            fury.serialize(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object to fury  used " + (end - start) + " length " + bytes.length);
        bytes = fury.serialize(user);
        bytes = fury.serialize(user);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            fury.serialize(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object to  fury used " + (end - start) + " length " + bytes.length);

        UserProtocol.serialUser(user);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            bytes = UserProtocol.serialUser(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object to user protocol used " + (end - start) + " length " + bytes.length);


    }


    public void testSerializable2() throws InterruptedException {
        User user = new User();
        user.name = "hello world";
        user.country = "中国";

        System.out.println("user object wson json 2 ");
        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            JSON.toJSONString(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object json tojson2 used " + (end - start) + " length " + JSON.toJSONString(user).length());

        start = System.currentTimeMillis();
        byte[] bts = null;
        for (int i = 0; i < 10000; i++) {
            bts = Wson.toWson(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object wson towson2 used " + (end - start) + " length " + bts.length);

        //Java是最快的，XLANG或者CPP比Java慢，并且序列化后数据格式大
        ThreadSafeFury fury = Fury.builder().withLanguage(Language.JAVA)
                .requireClassRegistration(true)
                .buildThreadSafeFury();
        fury.register(User.class);
        byte[] bytes = fury.serialize(user);
        bytes = fury.serialize(user);
        Thread.sleep(1000);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            fury.serialize(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object to fury2  used " + (end - start) + " length " + bytes.length);


        UserProtocol.serialUser(user);
        start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            bytes = UserProtocol.serialUser(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object to user protocol2 used " + (end - start) + " length " + bytes.length);


    }

}
