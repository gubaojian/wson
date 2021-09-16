package com.github.gubaojian.pson.serializers;

import com.github.gubaojian.pson.config.Config;
import com.github.gubaojian.pson.io.Output;

public interface Serializer<T> {

    void write(Config config, Output output, T value);

}
