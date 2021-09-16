package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

import java.math.BigDecimal;

public class BigDecimalSerializer implements Serializer<BigDecimal> {

    public static final BigDecimalSerializer instance = new BigDecimalSerializer();

    @Override
    public void write(Config config, Output output, BigDecimal value) {

    }
}
