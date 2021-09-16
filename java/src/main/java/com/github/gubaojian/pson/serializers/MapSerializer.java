package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

import java.util.Map;

/**
 * map not support not string keys
 * */
public class MapSerializer implements Serializer<Map<?,?>>{

    @Override
    public void write(Config config, Output output, Map<?, ?> value) {

    }
}
