package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

import java.math.BigInteger;

public class BigIntegerSerializer implements Serializer<BigInteger> {

    public static final BigIntegerSerializer instance = new BigIntegerSerializer();

    @Override
    public void write(Config config, Output output, BigInteger value) {

    }
}
