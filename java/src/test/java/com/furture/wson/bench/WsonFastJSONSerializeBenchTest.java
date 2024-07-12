package com.furture.wson.bench;

import com.alibaba.fastjson.JSON;
import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class WsonFastJSONSerializeBenchTest extends TestCase {



    public void testRunBenckMark() throws IOException {
        benckMark("/media.json", 10000);
        benckMark("/ele.json", 10000);
        benckMark("/data.json", 1000);
        benckMark("/home.json", 1000);
        benckMark("/recommend.json", 10000);
        benckMark("/tiny.json", 10000);
        benckMark("/weex.json", 10000);
        benckMark("/weex2.json", 10000);
        benckMark("/weex3.json", 10000);
        benckMark("/weex4.json", 10000);
        benckMark("/weex5.json", 10000);
        benckMark("/data/2.json", 10000);
        benckMark("/data/booleans.json", 10000);
        benckMark("/data/cart.json", 10000);
        benckMark("/data/epub.json", 10000);
        benckMark("/data/group.json", 10000);
        benckMark("/data/trade.json", 10000);


    }
    public void testMediaWson() throws IOException {
        benchBuild("/media.json", 10000, true);
    }

    public void testMediaJSON() throws IOException {
        benchBuild("/media.json", 10000, false);
    }

    public void testMedia2Wson() throws IOException {
        benchBuild("/media2.json", 1000, true);
    }

    public void testMedia2JSON() throws IOException {
        benchBuild("/media2.json", 1000, false);
    }

    public void testWeexWson() throws IOException {
        benchBuild("/weex.json", 1000, true);
    }

    public void testWeexJSON() throws IOException {
        benchBuild("/weex.json", 1000, false);
    }


    public void testMiddleWson() throws IOException {
        benchBuild("/middle.json", 1000, true);
    }

    public void testMiddleJSON() throws IOException {
        benchBuild("/middle.json", 1000, false);
    }





    /** 下面两个数据太大,单次存性能对比, 多次就是对比GC了 */
    public void testHomeWson() throws IOException {
        benchBuild("/home.json", 100, true);
    }

    public void testHomeJSON() throws IOException {
        benchBuild("/home.json", 100, false);
    }


    public void testDataWson() throws IOException {
        benchBuild("/data.json", 100, true);
    }

    public void testDataJSON() throws IOException {
        benchBuild("/data.json", 100, false);
    }


    private void benckMark(String jsonFile, int count) throws IOException {
        System.out.println("bench " + jsonFile);
        benchBuild(jsonFile, count, false);
        benchBuild(jsonFile, count, true);
    }

    private void benchBuild(String file, int count, boolean wson) throws IOException {
        String data = readFile(file);
        Object map = JSON.parse(data);
        long start = 0;
        long end = 0;

        if(wson) {
            start = System.currentTimeMillis();
            for (int i = 0; i < count; i++) {
               Wson.toWson(map);
            }
            end = System.currentTimeMillis();
            System.out.println("WSON toWSON used " + (end - start));
            System.out.println("wson size " + Wson.toWson(map).length);
        }else{
            start = System.currentTimeMillis();
            for(int i=0; i<count; i++) {
                JSON.toJSONString(map);
            }
            end = System.currentTimeMillis();
            System.out.println("FASTJSON toJSON used " + (end - start));
            System.out.println("json size " + JSON.toJSONString(map).getBytes("UTF-8").length);
        }
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
