package com.furture.wson.compatible;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import junit.framework.TestCase;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/30.
 */
public class CompatibleTest extends TestCase {

    public void  testCompatible(){
        String json = "[{\"args\":[\"4\",{\"type\":\"change\",\"module\":\"connection\",\"data\":null},null],\"method\":\"callback\"}]";
        JSONArray object = JSON.parseArray(json);
        CompatibleUtils.checkDiff(object);

    }
}
