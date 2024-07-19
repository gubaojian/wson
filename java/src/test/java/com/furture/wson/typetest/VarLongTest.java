package com.furture.wson.typetest;

import com.github.gubaojian.wson.io.Input;
import com.github.gubaojian.wson.io.Output;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

public class VarLongTest extends TestCase {

    @Test
    public void  testVarLong() throws IOException {
        Output output = new Output();
        long a = 10;
        long b = 20;
        output.writeVarInt64(a);
        output.writeVarInt64(b);
        output.writeUInt64(a);
        output.writeUInt64(b);
        byte[] bts = output.toBytes();
        System.out.println("bts length " + bts.length);
        Input input = new Input(bts);
        String message = "a=" + a + "b=" + b;
        Assert.assertEquals(message, a, input.readVarInt64());
        Assert.assertEquals(message, b, input.readVarInt64());
        Assert.assertEquals(message, a, input.readUInt64());
        Assert.assertEquals(message, b, input.readUInt64());
    }



    @Test
    public void  testVarLongMax() throws IOException {
        Output output = new Output();
        long a = Long.MAX_VALUE;
        long b = Long.MIN_VALUE;
        output.writeVarInt64(a);
        output.writeVarInt64(b);
        output.writeUInt64(a);
        output.writeUInt64(b);
        byte[] bts = output.toBytes();
        System.out.println("bts length " + bts.length);
        Input input = new Input(bts);
        String message = "a=" + a + "b=" + b;
        Assert.assertEquals(message, a, input.readVarInt64());
        Assert.assertEquals(message, b, input.readVarInt64());
        Assert.assertEquals(message, a, input.readUInt64());
        Assert.assertEquals(message, b, input.readUInt64());
    }


    @Test
    public void  testVarLongByRandom() throws IOException {
        Random random = new Random();
        for(int m=0; m<10000; m++) {
            Output output = new Output();
            long a = random.nextLong();
            long b = random.nextLong();
            output.writeVarInt64(a);
            output.writeVarInt64(b);
            output.writeUInt64(a);
            output.writeUInt64(b);
            byte[] bts = output.toBytes();
            Input input = new Input(bts);
            String message = "a=" + a + "b=" + b;
            Assert.assertEquals(message, a, input.readVarInt64());
            Assert.assertEquals(message, b, input.readVarInt64());
            Assert.assertEquals(message, a, input.readUInt64());
            Assert.assertEquals(message, b, input.readUInt64());
        }
    }
}
