package com.furture.wson;

import com.furture.wson.util.Bits;
import com.furture.wson.util.LruCache;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class DataFormatTest extends TestCase {


    public void  testData() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/data.dat");
        byte[] bts = new byte[1024];
        int length = inputStream.read(bts);
        inputStream.close();
        if(Bits.getDouble(bts, 0) != 210.33576){
            throw new  RuntimeException("format not right match double " + Bits.getDouble(bts, 0));
        }
        if(Bits.getVarInt(bts, 8) != 12345){
            throw new  RuntimeException("format not right match int");
        }
        if(Bits.getVarInt(bts, 10) != -1){
            throw new  RuntimeException("format not right match varint "
                    + Bits.getVarInt(bts, 10)  + "  " + Bits.getUInt(bts, 11));
        }

        if(Bits.getVarInt(bts, 12) != Integer.MAX_VALUE){
            throw new  RuntimeException("format not right match varint MAX"
             + Bits.getVarInt(bts, 12)
            + "  " + Integer.MAX_VALUE);
        }

        if(Bits.getVarInt(bts, 17) != Integer.MIN_VALUE){
            throw new  RuntimeException("format not right match varint MIN ");
        }

        if(Bits.getUInt(bts, 22) != 1){
            throw new  RuntimeException("1 format not right match uint " + Bits.getUInt(bts, 22));
        }

        if(Bits.getUInt(bts, 23) != Integer.MAX_VALUE){
            throw new  RuntimeException("max format not right match uint " + Bits.getUInt(bts, 23));
        }

        System.out.println(bts[28] == 'l');

        if(Bits.getLong(bts, 29) != Long.MAX_VALUE){
            throw new  RuntimeException("max format not right match uint " + Bits.getUInt(bts, 25)
             +  " " + Long.MAX_VALUE);
        }

        if(Bits.getLong(bts, 38) != Long.MAX_VALUE - 1){
            throw new  RuntimeException("max format not right match uint " + Bits.getUInt(bts, 25)
                    +  " " + Long.MAX_VALUE);
        }


        if(Bits.getLong(bts, 47) != -(Long.MAX_VALUE - 1)){
            throw new  RuntimeException("max format not right match uint " + Bits.getUInt(bts, 25)
                    +  " " + Long.MAX_VALUE);
        }
        System.out.println("first number  " +  Bits.getDouble(bts, 0));
        System.out.println("first int " +   Bits.getInt(bts, 8));
        System.out.println("first long " +  Bits.getLong(bts, 12));

    }


    public void testLinkedHashMapTest() {

        //LRU
        LruCache<String, String> map = new LruCache<>(12);
        for (int i = 0; i < 16; i++) {
            map.put(String.valueOf(i), String.valueOf(i + 1));
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("key=" + entry.getKey() + "---------value=" + entry.getValue());
        }

        System.out.println("--------------------------");

        map.get("3");
        map.get("5");
        map.put("6", "100");
        map.put("18", "100");

        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("key=" + entry.getKey() + "---------value=" + entry.getValue());
        }

        double num = 20.0;
        long numl  = 20;

        System.out.println(Double.doubleToLongBits(num));
    }
}
