package com.furture.wson.compatible;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.gubaojian.pson.wson.Wson;
import junit.framework.TestCase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/30.
 */
public class CompatibleTest extends TestCase {

    public void  testCompatible(){
        String json = "[{\"args\":[\"4\",{\"type\":\"change\",\"module\":\"connection\",\"data\":null},null],\"method\":\"callback\"}]";
        JSONArray object = JSON.parseArray(json);
        CompatibleUtils.checkDiff(object);
    }

    public void  testCompatibleDate(){
        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        System.out.println(JSON.toJSONString(map));

        System.out.println(JSON.toJSONString(Wson.parse(Wson.toWson(map))));
    }

    public void  testMediaBench(){
        CompatibleUtils.testMediaBench();
    }
}
