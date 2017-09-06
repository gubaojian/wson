package com.furture.tson;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class StringCodingTest extends TestCase {


    public void  testEncoding() throws IOException {
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
        System.out.println("utf-8 used "+ (end - start));





        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++){
            content.getBytes(StandardCharsets.UTF_8);
        }
        end = System.currentTimeMillis();
        System.out.println("used "+ (end - start));

        start = System.currentTimeMillis();
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        for(int i=0; i<10000; i++) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bts);
            CoderResult result = decoder.decode(byteBuffer, charBuffer, true);
            if (!result.isUnderflow())
                result.throwException();
            result = decoder.flush(charBuffer);
            if (!result.isUnderflow())
                result.throwException();
            charBuffer.flip();
            decoder.reset();
        }
        end = System.currentTimeMillis();
        System.out.println("end used "+ (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
           new String(bts, "UTF-8");
        }
        end = System.currentTimeMillis();
        System.out.println("new string end used "+ (end - start));
    }


    private String readFile(String file) throws IOException {
        ByteOutputStream outputStream = new ByteOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.getBytes());
    }
}
