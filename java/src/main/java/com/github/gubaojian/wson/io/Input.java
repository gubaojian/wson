package com.github.gubaojian.wson.io;

/**
 * 解析基础类，方便手工操作，自定义数据格式。
 * 你也可以扩展或者自定义协议。
 * */
public class Input {
    private int position;
    private byte[] buffer;
    private int start;

    public Input(byte[] buffer) {
        this(buffer, 0);
    }

    /**
     *  contains start， exclude end
     *  start <= data < end
     * */
    public Input(byte[] buffer, int start) {
        this.buffer = buffer;
        this.position = start;
        this.start = start;
    }

    /**
     * none need call
     * */
    public final void reset(){
        position = start;
    }

    /**
     * none need call
     * */
    public final void close(){
        position = 0;
        buffer = null;
    }

    public final byte readType(){
        byte type = buffer[position];
        position ++;
        return  type;
    }

    public final void move(int step) {
        position +=step;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public final int getPosition() {
        return position;
    }

    public final int getLength() {
        return buffer.length;
    }

    /**
     * 仅读取值，不读取类型
     * */
    public final int readVarInt(){
        int raw = readUInt();
        // This undoes the trick in putVarInt()
        int num = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return num ^ (raw & (1 << 31));
    }

    /**
     * 仅读取值，不读取类型， 无符号数
     * */
    public final int readUInt() {
        int value = 0;
        int i = 0;
        int b;
        while (((b = buffer[position]) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            position+=1;
            if (i > 35) {
                throw new IllegalArgumentException("Variable length quantity is too long");
            }
        }
        position+=1;
        return value | (b << i);
    }

    /**
     * 有符号数
     */
    public long readVarInt64() {
        return decodeZigZag64(readUInt64());
    }

    /**
     * 无符号数
     */
    public final long readUInt64() {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = buffer[position++];
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new IllegalArgumentException("Variable int64 Variable length is too long");
    }

    /**
     * 仅读取值，不读取类型
     * */
    public final long readLong(){
        long number = (((buffer[position + 7] & 0xFFL)      ) +
                ((buffer[position + 6] & 0xFFL) <<  8) +
                ((buffer[position + 5] & 0xFFL) << 16) +
                ((buffer[position + 4] & 0xFFL) << 24) +
                ((buffer[position + 3] & 0xFFL) << 32) +
                ((buffer[position + 2] & 0xFFL) << 40) +
                ((buffer[position + 1] & 0xFFL) << 48) +
                (((long) buffer[position])      << 56));
        position += 8;
        return  number;
    }

    /**
     * 仅读取值，不读取类型
     * */
    public final double readDouble(){
        double number = Double.longBitsToDouble(readLong());
        if(number > Integer.MAX_VALUE){
            long numberLong = (long) number;
            double doubleLong = (numberLong);
            if(number - doubleLong < Double.MIN_NORMAL){
                return numberLong;
            }
        }
        return  number;
    }

    /**
     * 仅读取值，不读取类型
     * */
    public float readFloat() {
        int number = (((buffer[position + 3] & 0xFF)      ) +
                ((buffer[position + 2] & 0xFF) <<  8) +
                ((buffer[position + 1] & 0xFF) << 16) +
                ((buffer[position  ] & 0xFF) << 24));
        position +=4;
        return  Float.intBitsToFloat(number);
    }


    /**
     * Decode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into values that can be
     * efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64 bits
     * to be varint encoded, thus always taking 10 bytes on the wire.)
     * CodedInputStream
     *
     * @param n An unsigned 64-bit integer, stored in a signed int because Java has no explicit
     *          unsigned support.
     * @return A signed 64-bit integer.
     */
    public static long decodeZigZag64(final long n) {
        return (n >>> 1) ^ -(n & 1);
    }

}
