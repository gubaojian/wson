package com.furture.wson.feature;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.Random;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class StringCodingTest extends TestCase {


    public void  testEncoding() throws IOException {
        Charset charset = Charset.forName("UTF-8");
        String content = readFile("/weex.json");
        Random random = new Random();
        long start = 0;
        long end = 0;



        byte[] bts = content.getBytes("UTF-8");
        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            content.getBytes("UTF-8");
        }
        end = System.currentTimeMillis();
        System.out.println("get utf-8 used "+ (end - start));




        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            content.getBytes(StandardCharsets.UTF_8);
        }
        end = System.currentTimeMillis();
        System.out.println("std used "+ (end - start));

        start = System.currentTimeMillis();
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bts);
        for(int i=0; i<10000; i++) {
            CoderResult result = decoder.decode(byteBuffer, charBuffer, true);
            if (!result.isUnderflow())
                result.throwException();
            result = decoder.flush(charBuffer);
            if (!result.isUnderflow())
                result.throwException();
            charBuffer.flip();
            byteBuffer.flip();
            decoder.reset();
        }
        end = System.currentTimeMillis();
        System.out.println("decode array buffer end used "+ (end - start));



        char[] buffer = new char[2048];
        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            testUtf8(bts, 0, bts.length, buffer);
        }
        end = System.currentTimeMillis();
        System.out.println("new string custom end used "+ (end - start));


        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
           new String(bts,0, bts.length, "UTF-8");
        }
        end = System.currentTimeMillis();
        System.out.println("new string end used "+ (end - start));

    }


    private static void testUtf8(byte[] bts, int offset, int length, char[] buffer){
        for(int i=offset; i<length; i++){
            buffer[length-offset] = (char) bts[offset];
        }
        new String(buffer,0, length-offset);
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
