package com.furture.tson;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/3.
 */
public class Node {

    public String name;

    public Node next;

    public static void main(String[] args) throws UnsupportedEncodingException {
        String ch = "adddd";
        byte[] bts = ch.getBytes("UTF-8");
        System.out.println("ddd"
        +  ch.hashCode()  + "  " + hash(bts, 0, bts.length));


        System.out.println(bytesEquals("adddds".getBytes(), 0, bts.length, bts));
    }

    private static final int hash(byte[] bts, int offset, int len){
        int h = 0;
        int end = offset + len;
        for (int i=offset; i<end; i++) {
            h = 31 * h + bts[i];
        }
        return h;
    }

    private static final boolean bytesEquals(byte[] buffer, int offset, int len,
                                             byte[] bts){
        if(len != bts.length){
            return  false;
        }
        for(byte bt : bts){
            if(bt != buffer[offset]){
                return  false;
            }
            offset ++;
        }
        return  true;
    }


}
