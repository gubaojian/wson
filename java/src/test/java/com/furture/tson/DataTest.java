package com.furture.tson;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class DataTest extends TestCase {


    public void  testData() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/data.dat");
        byte[] bts = new byte[1024];
        int length = inputStream.read(bts);
        inputStream.close();
        if(Bits.getDouble(bts, 0) != 210.33576){
            throw new  RuntimeException("format not right match double " + Bits.getDouble(bts, 0));
        }
        if(Bits.getInt(bts, 8) != 12345){
            throw new  RuntimeException("format not right match int");
        }
        if(Bits.getVarInt(bts, 12) != -1){
            throw new  RuntimeException("format not right match varint "
                    + Bits.getVarInt(bts, 12)  + "  " + Bits.getUInt(bts, 13));
        }

        if(Bits.getVarInt(bts, 13) != Integer.MAX_VALUE){
            throw new  RuntimeException("format not right match varint MAX");
        }

        if(Bits.getVarInt(bts, 18) != Integer.MIN_VALUE){
            throw new  RuntimeException("format not right match varint MIN ");
        }

        if(Bits.getUInt(bts, 23) != 1){
            throw new  RuntimeException("format not right match uint " + Bits.getUInt(bts, 13));
        }

        if(Bits.getUInt(bts, 24) != Integer.MAX_VALUE){
            throw new  RuntimeException("format not right match uint " + Bits.getUInt(bts, 13));
        }
        System.out.println("first number  " +  Bits.getDouble(bts, 0));
        System.out.println("first int " +  Bits.getInt(bts, 8));
        System.out.println("first long " +  Bits.getLong(bts, 12));
    }
}
