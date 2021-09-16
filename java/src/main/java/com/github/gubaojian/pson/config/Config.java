package com.github.gubaojian.pson.config;

import com.github.gubaojian.pson.serializers.Serializer;

import java.util.HashMap;
import java.util.Map;

public class Config {

    /**
     * skip map null values
     * */
    private boolean writeNullValue = false;

    private Config() {
        writeNullValue = false;
    }

    public boolean isWriteNullValue() {
        return writeNullValue;
    }

}
