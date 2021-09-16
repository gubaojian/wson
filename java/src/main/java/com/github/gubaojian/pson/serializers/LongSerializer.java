package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

public class LongSerializer implements Serializer<Long> {

    public static final IntsSerializer instance = new IntsSerializer();

    @Override
    public void write(Config config, Output output, Long value) {

    }
}
