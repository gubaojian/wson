package com.github.gubaojian.pson.serializers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.Map;

public class SerializersConfig {

    private Map<Class, Serializer<?>> serializersMap = null;

    public SerializersConfig() {
        this.serializersMap = new IdentityHashMap<>();
        register(Integer.class, IntsSerializer.instance);
        register(int.class, IntsSerializer.instance);
        register(Short.class, IntsSerializer.instance);
        register(short.class, IntsSerializer.instance);
        register(Byte.class, IntsSerializer.instance);
        register(byte.class, IntsSerializer.instance);
        register(Long.class, LongSerializer.instance);
        register(long.class, LongSerializer.instance);

        register(Number.class, NumberSerializer.instance);
        register(BigDecimal.class, BigDecimalSerializer.instance);
        register(BigInteger.class, BigIntegerSerializer.instance);

        register(byte[].class, ArraySerializer.instance);
        register(float[].class, ArraySerializer.instance);
        register(short[].class, ArraySerializer.instance);
        register(int[].class, ArraySerializer.instance);
        register(long[].class, ArraySerializer.instance);
        register(Object[].class, ArraySerializer.instance);
    }

    public void register(Class type, Serializer<?> serializer) {
        serializersMap.put(type, serializer);
    }

}
