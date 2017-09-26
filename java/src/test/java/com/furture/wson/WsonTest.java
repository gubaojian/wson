package com.furture.wson;

import com.alibaba.fastjson.JSON;
import com.efurture.wson.Wson;
import com.furture.wson.domain.Node;
import com.furture.wson.util.Bits;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 * 不要过多的处理null,空数组的情况, 业务有时需要做diff把数据返回。
 */
public class WsonTest extends TestCase {


    public void testParse(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "hello");
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Assert.assertEquals(bts.length, 14);
        Assert.assertEquals(Bits.getUInt(bts, 1), 1);
        Assert.assertEquals(Bits.getUInt(bts, 2), 4);
        Assert.assertEquals(bts[7], 's');
        Assert.assertEquals(bts.length, 14);
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);

    }


    public void testParseInt(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", 1);
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
    }


    public void testIntMax(){
        String key = "num";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Integer.MAX_VALUE);
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        Integer max = (Integer) parsed.get(key);
        Assert.assertEquals((int)max, (int)Integer.MAX_VALUE);
    }

    public void testIntMin(){
        String key = "num";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Integer.MIN_VALUE);
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        Integer max = (Integer) parsed.get(key);
        Assert.assertEquals((int)max, (int)Integer.MIN_VALUE);
    }

    public void testLongMax(){
        String key = "max";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Long.MAX_VALUE);
        byte[] bts = Wson.toWson(map);
        map.put(key, (double)Long.MAX_VALUE);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        long max =  ((Double)parsed.get(key)).longValue();
        Assert.assertEquals(max, Long.MAX_VALUE);
    }

    public void testLongMin(){
        String key = "max";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Long.MIN_VALUE);
        byte[] bts = Wson.toWson(map);
        map.put(key, (double)Long.MIN_VALUE);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        long max =  ((Double)parsed.get(key)).longValue();
        Assert.assertEquals(max, Long.MIN_VALUE);
    }

    public void testMapValueNull(){
        String key = "name";
        Map<String, Object> map = new HashMap<>();
        map.put(key, null);
        byte[] bts = Wson.toWson(map);
        Assert.assertEquals(8, bts.length);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        Assert.assertTrue(parsed.containsKey(key));
    }


    public void testList(){
        List<String> names = new ArrayList<String>();
        names.add("china");
        byte[] bts = Wson.toWson(names);
        System.out.println(new String(bts));
        List<String> parsed = (List<String>) Wson.parse(bts);
        Assert.assertEquals(names, parsed);
    }

    public void  testObject(){
        Map<Object, Object> map = new HashMap<>();
        map.put(100, "333");
        Wson.toWson(map);
    }


    public void  testRecursive(){
        Node node = new Node();
        node.name = "测试";
        node.next = node;
        Wson.toWson(node);
        System.out.println(new String(Wson.toWson(node)));

        System.out.println(JSON.toJSONString(node));
    }


}
