package com.furture.wson.bench;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.gubaojian.pson.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/30.
 */
public class FastJSONTest extends TestCase {




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
