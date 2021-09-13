package com.furture.wson.bug;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.gubaojian.pson.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by 剑白(jianbai.gbj) on 2017/12/22.
 */
public class IntDoubleTest extends TestCase  {


    @Test
    public void  testIntDouble(){
        String json = "{\n" +
                "    \"class\": \"MtopWVPlugin\",\n" +
                "    \"method\": \"send\",\n" +
                "    \"data\": {\n" +
                "      \"api\": \"mtop.shop.render.getasyncmoduledata\",\n" +
                "      \"v\": \"1.0\",\n" +
                "      \"needLogin\": false,\n" +
                "      \"ecode\": 0,\n" +
                "      \"data\": {\n" +
                "        \"compId\": 4963,\n" +
                "        \"userId\": 2841314996,\n" +
                "        \"extra\": \"count=3&sellerId=2841314996&pageType=3787&itemId=531836203415\"\n" +
                "      },\n" +
                "      \"AntiCreep\": true,\n" +
                "      \"param\": {\n" +
                "        \"compId\": 4963,\n" +
                "        \"userId\": 2841314996,\n" +
                "        \"extra\": \"count=3&sellerId=2841314996&pageType=3787&itemId=531836203415\"\n" +
                "      }\n" +
                "    }\n" +
                "}";
      JSONObject object =  JSON.parseObject(json);
      Object back = Wson.parse(Wson.toWson(object));
      System.out.println(object.toString());
      Assert.assertEquals(back.toString(), object.toString());
      System.out.println(back.toString());




        //过大整数处理

        //long a = 99999999999999999999999;
        //System.out.println(99999999999999999999999);
    }
}
