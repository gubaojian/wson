package com.furture.wson.bug;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by 剑白(jianbai.gbj) on 2017/12/22.
 */
public class BigNumberTest extends TestCase {

    @Test
    public void  testBigNumber(){
        String json = "{\n" +
                "  \"class\": \"MtopWVPlugin\",\n" +
                "  \"method\": 999999999999999999999999999999,\n" +
                "  \"method3\": 999999999999999999999999999999.3333\n" +
                "}";
        JSONObject object =  JSON.parseObject(json);
        Object back = Wson.parse(Wson.toWson(object));
        System.out.println(object.toString());
        Assert.assertEquals(back.toString(), object.toString());
        System.out.println(back.toString());




        //过大整数处理

        //long a = 99999999999999999999999;
        //System.out.println(99999999999999999999999);


        System.out.println(Long.MAX_VALUE);
    }
}
