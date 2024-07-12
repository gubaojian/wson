package com.furture.wson.util;

import java.util.Random;

public class CHStringUtil {


    private static Random random = new Random();
    private static final int BASE_RANDOM = 0x9fa5 - 0x4e00 + 1;


    // use apache common library
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for(int i=0; i<len; i++){
            sb.append(getRandomChar());
        }
        return sb.toString();
    }

    public static String randomString(int start, int end) {
        int len = start + random.nextInt(end - start);
        return randomString(len);
    }

    public static char getRandomChar() {
        return (char) (0x4e00 + random.nextInt(BASE_RANDOM));
    }




    public static  void main(String[] args){
        System.out.println("love " + randomString(10) + " " + getRandomChar()
        + getRandomChar());
    }
}
