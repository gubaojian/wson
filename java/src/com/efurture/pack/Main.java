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

        if(Bits.getUInt(bts, 13) != 1){
            throw new  RuntimeException("format not right match uint " + Bits.getUInt(bts, 13));
        }
        System.out.println("first number  " +  Bits.getDouble(bts, 0));
        System.out.println("first int " +  Bits.getInt(bts, 8));
        System.out.println("first long " +  Bits.getLong(bts, 12));
    }
}
