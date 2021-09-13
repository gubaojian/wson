package com.github.gubaojian.pson.config;

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
