package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

public class ArraySerializer implements Serializer<Object[]> {

    public static final ArraySerializer instance = new ArraySerializer();


    @Override
    public void write(Config config, Output output, Object[] value) {

    }
}
