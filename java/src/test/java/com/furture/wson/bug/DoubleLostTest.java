package com.furture.wson.bug;

import com.alibaba.fastjson.JSONObject;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;

/**
 * Created by 剑白(jianbai.gbj) on 2018/1/17.
 */
public class DoubleLostTest extends TestCase {


    /**
     * FASTJSON会把小数字转换成BigDecimal,
     * Wson会判断把在double范围内的转换成double
     * */
    public void  testDoubleLost(){
        String data = "{\n" +
                "    \"address\": {\n" +
                "        \"addressLine\": \"浙江省杭州市余杭区溪望路靠近阿里巴巴淘宝城2期\", \n" +
                "        \"city\": \"杭州市\", \n" +
                "        \"cityCode\": \"0571\", \n" +
                "        \"district\": \"余杭区\", \n" +
                "        \"province\": \"浙江省\", \n" +
                "        \"road\": \"溪望路\"\n" +
                "    }, \n" +
                "    \"coords\": {\n" +
                "        \"accuracy\": 29, \n" +
                "        \"altitude\": 0, \n" +
                "        \"bearing\": 0, \n" +
                "        \"latitude\": 30.278379, \n" +
                "        \"longitude\": 120.029034, \n" +
                "        \"speed\": 0\n" +
                "    }, \n" +
                "    \"errorCode\": 0\n" +
                "}";
        JSONObject object = JSONObject.parseObject(data);

        byte[] bts = Wson.toWson(object);
        JSONObject back = (JSONObject) Wson.parse(bts);

        Assert.assertNotEquals(back, object);
        Assert.assertNotEquals(object.getJSONObject("coords").get("longitude"), back.getJSONObject("coords").get("longitude"));
        Assert.assertEquals(((Number)object.getJSONObject("coords").get("longitude")).doubleValue(), back.getJSONObject("coords").get("longitude"));


        data = "{\n" +
                "\"latitude\": 308848884884884848484848848484848.27837944999999999999994444, \n" +
                "\"longitude\": 123088488848848848484848488484848480.029034308848884884884848484848848484848, \n" +
                "}";
        object = JSONObject.parseObject(data);

        bts = Wson.toWson(object);
        back = (JSONObject) Wson.parse(bts);

        Assert.assertEquals(back, object);
        Assert.assertEquals(object.get("longitude"), back.get("longitude"));


        System.out.println("ddd" + object.get("longitude"));

    }
}
