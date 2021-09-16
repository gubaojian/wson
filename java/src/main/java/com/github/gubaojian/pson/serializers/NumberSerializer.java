package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

public class NumberSerializer implements Serializer<Number> {

    public static final NumberSerializer instance = new NumberSerializer();

    @Override
    public void write(Config config, Output output, Number value) {

    }
}
