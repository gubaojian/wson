package com.furture.wson.bench;

import com.alibaba.fastjson.JSONObject;
import com.efurture.wson.Wson;
import com.google.flatbuffers.FlatBufferBuilder;
import junit.framework.TestCase;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 剑白(jianbai.gbj) on 2018/2/26.
 */
public class FlatBufferTest extends TestCase {

    public void testFlatBuffer(){
        //testBench(200);
        //testBench(100);
        //testBench("中国人民好");


        testBench2(200);
    }

    private void testBench(Object object){
        long start = 0;
        start = System.currentTimeMillis();
        List<Object> lists = new ArrayList<Object>();
        for(int j=0; j<1000; j++) {
            lists.add(object);
        }
        for(int i=0; i<1000; i++) {
            Wson.toWson(lists);
        }
        System.out.println("wson used " + (System.currentTimeMillis() - start)
        + " ms length " + Wson.toWson(lists).length);


        start = System.currentTimeMillis();
        int length = 0;
        for(int i=0; i<1000; i++) {
            FlatBufferBuilder builder = new FlatBufferBuilder();
            for(int j=0; j<1000; j++) {
                if (object instanceof Integer) {
                    builder.addInt((Integer) object);
                }
                if(object instanceof  String){
                    builder.createString(object.toString());
                }
            }
            builder.finish(0);
            byte[] bts = builder.sizedByteArray();
            length = bts.length;
        }


        System.out.println("flatbuffer used " + (System.currentTimeMillis() - start)
                + " ms length " + length);


    }

    private void testBench2(Number object){
        long start = 0;
        long sum = 0;

        start = System.currentTimeMillis();
        for(int i=0; i<100000; i++){
            if(object instanceof  Integer){
                sum += ((int)object);
            }
        }
        System.out.println(sum + " int used " + (System.currentTimeMillis() - start));



        start = System.currentTimeMillis();
        for(int i=0; i<100000; i++){
            if(object instanceof  Integer){
                sum += ((object).intValue());
            }
        }
        System.out.println(sum + "intvalue used " + (System.currentTimeMillis() - start));


    }
}
