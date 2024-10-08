package com.github.gubaojian.wson.io;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 序列化基础类，方便手工操作，自定义数据格式。也方便定义混合数据格式。
 * 你也可以扩展或者自定义协议。
 */
public class Output {
    private byte[] buffer;
    private int position;

    public Output() {
        this(new byte[4096], 0);
    }

    public Output(byte[] buffer) {
        this(buffer, 0);
    }

    public Output(byte[] buffer, int position) {
        this.buffer = buffer;
        this.position = position;
    }

    public final void move(int step) {
        position += step;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public final void reset() {
        position = 0;
    }

    public final void close() {
        buffer = null;
        position = 0;
    }


    public final int getPosition() {
        return position;
    }

    public final byte[] toBytes() {
        byte[] bts = new byte[position];
        System.arraycopy(buffer, 0, bts, 0, position);
        return bts;
    }

    public final void writeType(byte type) {
        buffer[position++] = type;
    }

    public final void writeByte(byte type) {
        buffer[position++] = type;
    }

    public final void writeBytes(byte[] bts) {
        ensureCapacity(bts.length + 16);
        System.arraycopy(bts, 0, buffer, position, bts.length);
        position += bts.length;
    }

    public final void writeStringUTF8(String str) {
        if (str != null) {
            //可以优化，自定义转换过程，可以减少一次byte[]的copy
            byte[] bts = str.getBytes(StandardCharsets.UTF_8);
            ensureCapacity(bts.length + 12);
            writeVarInt(bts.length);
            if (bts.length > 0) {
                System.arraycopy(bts, 0, buffer, position, bts.length);
                position += bts.length;
            }
        } else {
            ensureCapacity(8);
            writeVarInt(-1);
        }
    }

    /**
     * 仅写入值，不写入类型
     */
    public final void writeDouble(double value) {
        writeLong(Double.doubleToLongBits(value));
    }

    /**
     * 仅写入值，不写入类型
     */
    public final void writeFloat(float value) {
        int val = Float.floatToIntBits(value);
        buffer[position + 3] = (byte) (val);
        buffer[position + 2] = (byte) (val >>> 8);
        buffer[position + 1] = (byte) (val >>> 16);
        buffer[position] = (byte) (val >>> 24);
        position += 4;
    }

    /**
     * 4位定长编码
     */
    public final void writeFixedInt32(int val) {
        writeInt(val);
    }

    /**
     * 定长编码，仅读取值，不读取类型， 无符号数
     * 参考 DataOutputStream 和 DataInputStream 和DataInput 用了大端编码，方便网络传输。
     * 及 ProtoBuf中的CodedInputStream用了小端编码，估计为了优化性能才用的小端
     * Writes an int value, which is comprised of four bytes, to the output stream. The byte values to be written, in the order shown, are:
     * (byte)(0xff & (v >> 24)) (byte)(0xff & (v >> 16)) (byte)(0xff & (v >>  8)) (byte)(0xff & v)
     * The bytes written by this method may be read by the readInt method of interface DataInput, which will then return an int equal to v.
     */
    public final void writeInt(int val) {
        buffer[position + 3] = (byte) (val);
        buffer[position + 2] = (byte) (val >>> 8);
        buffer[position + 1] = (byte) (val >>> 16);
        buffer[position] = (byte) (val >>> 24);
        position += 4;
    }

    /**
     * 固定8位
     */
    public final void writeFixedInt64(long value) {
        writeLong(value);
    }

    /**
     * 仅写入值，不写入类型，固定长度编码
     */
    public final void writeLong(long val) {
        buffer[position + 7] = (byte) (val);
        buffer[position + 6] = (byte) (val >>> 8);
        buffer[position + 5] = (byte) (val >>> 16);
        buffer[position + 4] = (byte) (val >>> 24);
        buffer[position + 3] = (byte) (val >>> 32);
        buffer[position + 2] = (byte) (val >>> 40);
        buffer[position + 1] = (byte) (val >>> 48);
        buffer[position] = (byte) (val >>> 56);
        position += 8;
    }

    /**
     * 仅写入值，不写入类型.
     * 有符号变长编码
     */
    public final void writeVarInt(int value) {
        writeUInt((value << 1) ^ (value >> 31));
    }


    /**
     * 仅写入值，不写入类型.
     * 无符号变长编码
     */
    public final void writeUInt(int value) {
        while ((value & 0xFFFFFF80) != 0) {
            buffer[position++] = (byte) ((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buffer[position++] = (byte) (value & 0x7F);
    }


    /**
     * 有符号变长编码
     */
    public final void writeVarInt64(final long value) {
        writeUInt64(encodeZigZag64(value));
    }

    /**
     * 无符号变长编码
     */
    public final void writeUInt64(long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                buffer[position++] = (byte) value;
                return;
            } else {
                buffer[position++] = (byte) (((int) value | 0x80) & 0xFF);
                value >>>= 7;
            }
        }
    }


    public final void ensureCapacity(int minCapacity) {
        minCapacity += position;
        // overflow-conscious code
        if (minCapacity > buffer.length) {
            int oldCapacity = buffer.length;
            int newCapacity = oldCapacity << 1;
            if (newCapacity < 1024 * 16) {
                newCapacity = 1024 * 16;
            }
            //seems none use
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity << 1;
            }
            buffer = Arrays.copyOf(buffer, newCapacity);
        }
    }

    /**
     * Encode a ZigZag-encoded 64-bit value. ZigZag encodes signed integers into values that can be
     * efficiently encoded with varint. (Otherwise, negative values must be sign-extended to 64 bits
     * to be varint encoded, thus always taking 10 bytes on the wire.)
     * CodedOutputSteam
     *
     * @param n A signed 64-bit integer.
     * @return An unsigned 64-bit integer, stored in a signed int because Java has no explicit
     * unsigned support.
     */
    public static long encodeZigZag64(final long n) {
        // Note:  the right-shift must be arithmetic
        return (n << 1) ^ (n >> 63);
    }

}
