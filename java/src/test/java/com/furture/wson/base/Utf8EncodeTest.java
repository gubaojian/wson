package com.furture.wson.base;

import com.furture.wson.benckmark.Benckmark;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Utf8EncodeTest extends TestCase {

    @Test
    public void testUtf8Encode(){
        String label = "Benckmark";
        Benckmark.run("Utf8Encode", new Runnable() {
            @Override
            public void run() {
                label.getBytes(StandardCharsets.UTF_8);
            }
        }, new Runnable() {
            @Override
            public void run() {
                label.getBytes(StandardCharsets.UTF_8);
            }
        });
        System.out.println("done");
    }


}
