package com.furture.wson.types;

import com.github.gubaojian.pson.wson.Wson;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class StringTypeTest extends TestCase {

    @Test
    public void  testStringAscii(){
        String hello = "hello";
        byte[] bts = Wson.toWson(hello);
        Assert.assertEquals(hello.length() + 2, bts.length);
        String back = (String) Wson.parse(bts);
        Assert.assertEquals(hello, back);
    }
}
