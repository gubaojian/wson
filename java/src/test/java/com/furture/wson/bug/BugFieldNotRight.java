package com.furture.wson.bug;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by furture on 2018/6/15.
 */
public class BugFieldNotRight extends TestCase {


    @Test
    public void testNotRightData() throws IOException {
        String json = readFile("/bug/fieldNotRight.json");


        JSONObject object = JSON.parseObject(json);

        JSONObject  back = (JSONObject) Wson.parse(Wson.toWson(object));


       // System.out.println(object.toJSONString());
        System.out.println(back.toJSONString());


    }


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

}
