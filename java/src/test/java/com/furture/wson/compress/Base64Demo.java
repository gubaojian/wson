package com.furture.wson.compress;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.commons.lang3.RandomUtils;

public class Base64Demo {
    public static  void main(String[] args) {
        byte[] data = RandomUtils.nextBytes(1024);
        String str = JSON.toJSONString(data, JSONWriter.Feature.WriteByteArrayAsBase64);
        System.out.println(str);
    }
}
