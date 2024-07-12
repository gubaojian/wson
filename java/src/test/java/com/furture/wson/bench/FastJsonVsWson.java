package com.furture.wson.bench;

import com.alibaba.fastjson.JSON;
import com.github.gubaojian.wson.Wson;
import com.furture.wson.domain.User;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class FastJsonVsWson extends TestCase {



    public void testFastJson(){
        String json = null;
        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "hello world");
            map.put("age", 1);
            json = JSON.toJSONString(map);
        }
        long end = System.currentTimeMillis();
        System.out.println("fastjson toString used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            JSON.parseObject(json);
        }
        end = System.currentTimeMillis();
        System.out.println("fastjson parse used " + (end - start));
    }



    public void testWson(){
        byte[] data = null;
        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "hello world");
            map.put("age", 1);
            data = Wson.toWson(map);
        }
        long end = System.currentTimeMillis();

        System.out.println("wson towson used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            Wson.parse(data);
        }
        end = System.currentTimeMillis();

        System.out.println("wson parse used " + (end - start));
    }

    public void testSerializable(){
        User user = new User();
        user.name = "中国";
        user.country = "中国";

        System.out.println("user object wson towson used start ");

        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
           Wson.toWson(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("user object wson towson used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            JSON.toJSONString(user);
        }
        end = System.currentTimeMillis();
        System.out.println("user object json tojson used " + (end - start));
    }



}
