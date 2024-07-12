package com.github.gubaojian.wson.io;

/**
 * 数据协议类型
 * */
public class Protocol {
    /**
     * wson data type
     * */
    public static final byte NULL_TYPE = '0';

    /**
     * UTF-16 string
     * */
    public static final byte STRING_TYPE = 's';

    /**
     * UTF-8 string
     * */
    public static final byte UTF8_STRING_TYPE = 'u';

    public static final byte BOOLEAN_TYPE_TRUE = 't';

    public static final byte BOOLEAN_TYPE_FALSE = 'f';

    public static final byte NUMBER_INT_TYPE = 'i';

    public static final byte NUMBER_LONG_TYPE = 'l';

    public static final byte NUMBER_BIG_INTEGER_TYPE = 'g';

    public static final byte NUMBER_BIG_DECIMAL_TYPE = 'e';

    public static final byte NUMBER_DOUBLE_TYPE = 'd';

    public static final byte NUMBER_FLOAT_TYPE = 'F';

    public static final byte ARRAY_TYPE = '[';

    public static final byte MAP_TYPE = '{';


}
