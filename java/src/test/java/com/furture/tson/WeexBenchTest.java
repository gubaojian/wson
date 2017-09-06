package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class WeexBenchTest extends TestCase {


    public void testWeex() throws IOException {
        benchFile("/weex.json");
    }

    public void testWeex2() throws IOException {
        benchFile("/weex2.json", 10000);
    }

    public void testWeex3() throws IOException {
        benchFile("/weex3.json");
    }

    public void testWeex4() throws IOException {
        benchFile("/weex4.json");
    }

    public void testWeex5() throws IOException {
        benchFile("/weex5.json");

        long start = System.currentTimeMillis();
        int x = 1;
        for(int i=0; i<10000; i++){
            x = x*i;
            x +=x;
            x -=10;
            x *=10;
        }
        System.out.println(x + "used " + (System.currentTimeMillis() - start));
    }

    public void testMiddle() throws IOException {
        benchFile("/middle.json", 1000);
    }

    public void testHome() throws IOException {
        benchFile("/home.json", 100);
    }
    public void testData() throws IOException {
        benchFile("/data.json", 10);
    }


    private void benchFile(String file) throws IOException {
        int count = 10000;
        benchFile(file, count);
    }

    private void benchFile(String file, int count) throws IOException {
        //System.gc();
        //try {
         //   Thread.sleep(1000);
        //} catch (InterruptedException e) {
          //  e.printStackTrace();
        //}
        String data = readFile(file);
        Object map = JSON.parse(data);
        byte[] tson = Tson.toTson(map);
        System.out.println("\nbench file " + file + ":\n");
        long start = 0;
        long end = 0;


        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            map = Tson.parse(tson);
        }
        end = System.currentTimeMillis();
        System.out.println("TSON parse used " + (end - start));


        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Tson.toTson(map);
        }
        end = System.currentTimeMillis();
        System.out.println("TSON toTSON used " + (end - start));


        /**
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/



        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            map = JSON.parse(data);
        }
        end = System.currentTimeMillis();
        System.out.println("FastJSON parse used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            JSON.toJSONString(map);
        }
        end = System.currentTimeMillis();
        System.out.println("FastJSON toJSON used " + (end - start));


        /**
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/



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


    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();

    }

    public void  testConvert() throws IOException {
        String data = readFile("/data.json");
        Object map = JSON.parse(data);
        byte[] tson = Tson.toTson(map);
        saveFile("data.tson", tson);
    }
}
