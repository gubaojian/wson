package com.furture.wson.typetest;

import com.github.gubaojian.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class StringTypeTest extends TestCase {

    @Test
    public void testStringAsciiUf8() {
        String hello = "hello";
        byte[] bts = Wson.toWson(hello); //FIXME UTF-8编码
        System.out.println(bts.length + " " + new String(bts));
        Assert.assertEquals(hello.length() + 2, bts.length);
        String back = (String) Wson.parse(bts);
        Assert.assertEquals(hello, back);
    }

    @Test
    public void testStringAscii22() {
        String hello = "hello中国";
        byte[] bts = Wson.toWson(hello);
        String back = (String) Wson.parse(bts);
        Assert.assertEquals(hello, back);
    }
}
