package com.furture.wson.feature;

import com.alibaba.fastjson2.JSON;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/28.
 */
public class CopyTest {
    public static void main(String[] args){

        char[] ch = new char[20];
        byte[] bts = new byte[40];


        //System.arraycopy(ch, 0, bts, 0, 10);


        String a = new String("a").intern();

        String b = new String("a").intern();

        System.out.println(a == b);

    }
}
