package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.wson.Wson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/6.
 */
public class ToWsonTest extends TestCase {




    public void testWeex5() throws IOException, InterruptedException {
        benchFile("/weex5.json", "/weex5.tson", 100000);
    }

    public void testWeex4() throws IOException, InterruptedException {
       // Thread.sleep(15000);
        benchFile("/weex4.json", "/weex4.tson", 100000);
    }

    private void benchFile(String jsonFile, String tsonFile, int count) throws IOException {
        String data = readFile(jsonFile);
        byte[] tson = readBytes(tsonFile);
        JSON.parse(data);
        Object map = Wson.parse(tson);
        System.out.println("\nbench file " + jsonFile + tsonFile + ":\n");
        long start = 0;
        long end = 0;



        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Wson.toWson(map);
        }
        end = System.currentTimeMillis();
        System.out.println("TSON toTSON used " + (end - start));



        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            JSON.toJSONString(map);
        }
        end = System.currentTimeMillis();
        System.out.println("FastJSON toJSON used " + (end - start));





    }


    private String readFile(String file) throws IOException {
        ByteOutputStream outputStream = new ByteOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.getBytes());
    }


    private byte[] readBytes(String file) throws IOException {
        ByteOutputStream outputStream = new ByteOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  outputStream.getBytes();
    }
}
