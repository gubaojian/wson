package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

public class IntsSerializer implements Serializer<Number> {


    public static final IntsSerializer instance = new IntsSerializer();


    @Override
    public void write(Config config, Output output, Number value) {

    }
}
