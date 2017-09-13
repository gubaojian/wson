package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class TsonBenchTest extends TestCase {


    public void testWeex5() throws IOException {
        benchBuild("/data.json", 1000);
    }

    private void benchBuild(String file, int count) throws IOException {

        String data = readFile(file);
        Object map = JSON.parse(data);
        long start = 0;
        long end = 0;
        int length = 0;

        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            JSON.toJSONString(map);
        }
        end = System.currentTimeMillis();
        System.out.println("FASTJSON toJSON used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Tson.toTson(map);
        }
        end = System.currentTimeMillis();
        System.out.println(length + "TSON toTSON used " + (end - start));

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
}
