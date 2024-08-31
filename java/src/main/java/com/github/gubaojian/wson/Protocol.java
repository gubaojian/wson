package com.github.gubaojian.wson;

/**
 * 数据协议类型，你也可以扩展或者自定义协议。
 * https://www.asciim.cn/
 * 字母和符号。
 * 比如对小于1-22数字进行优化其实对于特定类型的值，无需编码，可以用在节省空间
 * */
public class Protocol {
    /**
     * wson data type
     * */
    public static final byte NULL_TYPE = '0';

    /**
     * 默认 UTF-16 string
     * */
    public static final byte STRING_TYPE = 's';

    /**
     * UTF-8 string
     * */
    public static final byte STRING_TYPE_UTF8 = 'u';

    public static final byte BOOLEAN_TYPE_TRUE = 't';

    public static final byte BOOLEAN_TYPE_FALSE = 'f';

    public static final byte NUMBER_INT_TYPE = 'i';

    public static final byte NUMBER_LONG_TYPE = 'l';

    /**
     * 大数字，采用UTF-16字符串存储
     * */
    public static final byte NUMBER_BIG_INTEGER_TYPE = 'g';

    /**
     * 大数字，采用UTF-8字符串存储
     * */
    public static final byte NUMBER_BIG_INTEGER_TYPE_UTF8 = 'G';

    /**
     * 大数字，采用UTF-16字符串存储
     * */
    public static final byte NUMBER_BIG_DECIMAL_TYPE = 'e';

    /**
     * 大数字，采用UTF-16字符串存储
     * */
    public static final byte NUMBER_BIG_DECIMAL_TYPE_UTF8 = 'E';


    public static final byte NUMBER_DOUBLE_TYPE = 'd';

    public static final byte NUMBER_FLOAT_TYPE = 'F';

    public static final byte ARRAY_TYPE = '[';

    /**
     * key默认采用 UTF-16 编码
     * */
    public static final byte MAP_TYPE = '{';

    /**
     * key默认采用 UTF-8 编码
     * */
    public static final byte MAP_TYPE_UTF8 = '}';


}
