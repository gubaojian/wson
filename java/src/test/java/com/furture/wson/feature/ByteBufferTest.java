package com.furture.wson.feature;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Created by 剑白(jianbai.gbj) on 2018/3/16.
 */
public class ByteBufferTest {


    public static void main(String[] args) throws UnsupportedEncodingException {

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        buffer.put("中国".getBytes("UTF-8"));

        //System.out.println(buffer.as.get());

    }
}
