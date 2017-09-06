package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/6.
 */
public class ParseBenchTest extends TestCase {


    public void testWeex5() throws IOException {
        benchFile("/weex5.json", "/weex5.tson", 10000);
    }


    private void benchFile(String jsonFile, String tsonFile, int count) throws IOException {
        String data = readFile(jsonFile);
        byte[] tson = readBytes(tsonFile);
        System.out.println("\nbench file " + jsonFile + tsonFile + ":\n");
        long start = 0;
        long end = 0;


        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Tson.parse(tson);
        }
        end = System.currentTimeMillis();
        System.out.println("TSON parse used " + (end - start));



        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
           JSON.parse(data);
        }
        end = System.currentTimeMillis();
        System.out.println("FastJSON parse used " + (end - start));

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
