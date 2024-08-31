package com.furture.wson.bug;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;

/**
 * Created by 剑白(jianbai.gbj) on 2018/1/17.
 */
public class LongLostTest  extends TestCase{

    public void  testLong(){
        String a = "{\"p\":\"1\",\"refundId\":6419458776149741,\"t\":\"t\"}";

        JSONObject object = JSON.parseObject(a);

        System.out.println(object.get("refundId").getClass());

        JSONObject back = (JSONObject) Wson.parse(Wson.toWson(object));


        System.out.println(back.get("refundId").getClass());

        double ad = 6419458776149741.000001;

        double per = ad  - (double)((long)ad);
       System.out.println(per + "" + (per == 0.0)
        + "  " + ad);

        System.out.println("Double.MIN_NORMAL " + (Double.MIN_NORMAL > per )

        +   Double.MIN_NORMAL  + "  " + Double.MAX_VALUE);

    }
}
