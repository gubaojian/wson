package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class FastJsonVsTson extends TestCase {


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
        System.out.println("fastjson used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            JSON.parseObject(json);
        }
        end = System.currentTimeMillis();
        System.out.println("fastjson parse used " + (end - start));
    }



    public void testTson(){
        byte[] data = null;
        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "hello world");
            map.put("age", 1);
            data = Tson.toTson(map);
        }
        long end = System.currentTimeMillis();

        System.out.println("tson totson used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            Tson.parse(data);
        }
        end = System.currentTimeMillis();

        System.out.println("tson parse used " + (end - start));



    }
}
