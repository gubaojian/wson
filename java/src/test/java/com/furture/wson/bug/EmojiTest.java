package com.furture.wson.bug;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by 剑白(jianbai.gbj) on 2017/12/22.
 */
public class EmojiTest extends TestCase {

    @Test
    public void  testEmoji() throws UnsupportedEncodingException {
        String json = "[{\"args\":[\"67\",\"input\",{\"timeStamp\":1542864306658,\"value\":\"\uD83D\uDE33\uD83D\uDE33\"},{\"attrs\":{\"value\":\"\uD83D\uDE33\uD83D\uDE33\"}}],\"method\":\"fireEvent\"}]";
        JSONArray object =  JSON.parseArray(json);
        Object back = Wson.parse(Wson.toWson(object));
        System.out.println(object.toString());
        Assert.assertEquals(back.toString(), object.toString());
        System.out.println(back.toString());


        String emojo ="😳😳";

        Wson.toWson(emojo);

        System.out.println(emojo.getBytes("UTF-8").length);


        byte[] bts = emojo.getBytes("UTF-8");
        for(int i=0; i<bts.length; i++){
            System.out.println(bts[i]);
        }

        for(int i=0; i<emojo.length(); i++){
            System.out.println("code point " + emojo.codePointAt(i));
        }

       // System.out.println(new BigInteger( (byte[])(Wson.toWson(emojo))).toString(16));



        saveData(emojo, "emoji");

        //过大整数处理

        //long a = 99999999999999999999999;
        //System.out.println(99999999999999999999999);


       // System.out.println(Long.MAX_VALUE);
    }


    public void saveData(Object data, String name){
        byte[] wson = Wson.toWson(data);
        String wsonFile = "src/test/resources/" + name + ".wson";
        try {
            saveFile(wsonFile, wson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();
    }
}
