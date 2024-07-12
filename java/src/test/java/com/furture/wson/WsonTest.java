package com.furture.wson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.gubaojian.wson.Wson;
import com.furture.wson.domain.Node;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 * 不要过多的处理null,空数组的情况, 业务有时需要做diff把数据返回。
 */
public class WsonTest extends TestCase {


    @Test
    public void testParse(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "hello");
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);

        System.out.println(ByteOrder.nativeOrder());

    }

    @Test
    public void testParseInt(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", 1);
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
    }

    @Test
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

    @Test
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

    @Test
    public void testLongMax(){
        String key = "max";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Long.MAX_VALUE);
        byte[] bts = Wson.toWson(map);
        map.put(key, Long.MAX_VALUE);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
    }

    @Test
    public void testLongMin(){
        String key = "max";
        Map<String, Object> map = new HashMap<>();
        map.put(key, Long.MIN_VALUE);
        byte[] bts = Wson.toWson(map);
        map.put(key, Long.MIN_VALUE);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertEquals(map, parsed);
        long max =  ((Number)parsed.get(key)).longValue();
        Assert.assertEquals(max, Long.MIN_VALUE);
    }

    @Test
    public void testMapValueNull(){
        String key = "name";
        Map<String, Object> map = new HashMap<>();
        map.put(key, null);
        byte[] bts = Wson.toWson(map);
        System.out.println(new String(bts));
        Map<String, Object> parsed = (Map<String, Object>) Wson.parse(bts);
        Assert.assertNotEquals(map, parsed);
        Assert.assertTrue(parsed.size()  == 0);
    }


    @Test
    public void testList(){
        List<String> names = new ArrayList<String>();
        names.add("china");
        byte[] bts = Wson.toWson(names);
        System.out.println(new String(bts));
        List<String> parsed = (List<String>) Wson.parse(bts);
        Assert.assertEquals(names, parsed);
    }

    /**
     * map key will be conver to string
     * */
    @Test
    public void  testObject(){
        Map<Object, Object> map = new HashMap<>();
        map.put("100", "333");
        byte[] bts = Wson.toWson(map);
        Map parseMap = (Map)Wson.parse(bts);
        Assert.assertEquals(JSON.toJSONString(parseMap), JSON.toJSONString(map));
    }

    @Test
    public void  testRecursive(){
        Node node = new Node();
        node.name = "测试";
        node.next = node;
        Wson.toWson(node);
        String wson = new String(Wson.toWson(node));
        Assert.assertTrue(wson.indexOf("id") < 0);
        System.out.println(wson);

        System.out.println(JSON.toJSONString(node));
    }

    @Test
    public void  testRecommend() throws IOException {
        String json = readFile("/recommend2.json");
        byte[] bts = Wson.toWson(JSON.parse(json));
        Object src = Wson.parse(bts);

        Assert.assertEquals(json, JSON.toJSONString(src));
    }

    @Test
    private String readFile(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.toByteArray());
    }


    @Test
    public void trimNull(){
        String json = "[{\"args\":[\"4\",{\"type\":\"change\",\"module\":\"connection\",\"data\":null},null],\"method\":\"callback\"}]";
        JSONArray object = JSON.parseArray(json);
        System.out.println(JSON.toJSONString(object));
        Assert.assertNotEquals(object, Wson.parse(Wson.toWson(object)));
        Assert.assertEquals(JSON.parse(JSON.toJSONString(object)), Wson.parse(Wson.toWson(object)));
        System.out.println(JSON.toJSONString(Wson.toWson(object)));
    }

    @Test
    public void  testFloat(){
        Map<String,Object> map = new HashMap<>();
        map.put("number", 1.0f);
        JSONObject object = (JSONObject) Wson.parse(Wson.toWson(map));
        System.out.println(object.get("number").getClass());

        Assert.assertEquals(object.get("number").getClass(), Float.class);
    }

    @Test
    public void testNull(){
        String json = "[{\"args\":[\"4\",{\"type\":\"change\",\"module\":\"connection\",\"data\":null},null],\"method\":\"callback\"}]";
        JSONArray object = JSON.parseArray(json);
        System.out.println(JSON.toJSONString(object));
        Assert.assertNotEquals(object, Wson.parse(Wson.toWson(object)));
        Assert.assertEquals(JSON.parse(JSON.toJSONString(object)), Wson.parse(Wson.toWson(object)));

        System.out.println(JSON.toJSONString(Wson.toWson(object)));

    }
}
