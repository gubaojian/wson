package com.efurture.pack;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        InputStream inputStream = Main.class.getResourceAsStream("/data.dat");
        byte[] bts = new byte[1024];
        int length = inputStream.read(bts);
        inputStream.close();
        if(LittleEndianBits.getDouble(bts, 0) != 20.3356){
            throw new  RuntimeException("format not right match double");
        }
        if(LittleEndianBits.getInt(bts, 8) != 12345){
            throw new  RuntimeException("format not right match int");
        }
        System.out.println("first number  " +  LittleEndianBits.getDouble(bts, 0));
        System.out.println("first int " +  LittleEndianBits.getInt(bts, 8));
        System.out.println("first long " +  LittleEndianBits.getLong(bts, 12));
    }
}
