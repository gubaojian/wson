package com.github.gubaojian.wson.io;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 解析基础类，方便手工操作，自定义数据格式
 * */
public class Output {
    private byte[] buffer;
    private int position;
    private final static ThreadLocal<byte[]> bufLocal = new ThreadLocal<byte[]>();

    public Output() {
        buffer =  bufLocal.get();
        if(buffer != null) {
            bufLocal.set(null);
        }else{
            buffer = new byte[1024];
        }
    }

    public Output(int bufferSize){
        buffer =  bufLocal.get();
        if(buffer != null) {
            bufLocal.set(null);
        }else{
            buffer = new byte[1024];
        }
    }

    public final void move(int step) {
        position +=step;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public final void reset(){
        position = 0;
    }

    public final void close(){
        if(buffer.length <= 1024*16){
            bufLocal.set(buffer);
        }
        buffer = null;
        position = 0;
    }


    public final int getPosition() {
        return position;
    }

    public byte[] toBytes() {
        byte[] bts = new byte[position];
        System.arraycopy(buffer, 0, bts, 0, position);
        return  bts;
    }

    public final void writeByte(byte type){
        buffer[position] = type;
        position++;
    }

    public final void writeDouble(double value){
        writeLong(Double.doubleToLongBits(value));
    }

    public final void writeFloat(float value){
        int val = Float.floatToIntBits(value);
        buffer[position + 3] = (byte) (val       );
        buffer[position + 2] = (byte) (val >>>  8);
        buffer[position + 1] = (byte) (val >>> 16);
        buffer[position ] = (byte) (val >>> 24);
        position += 4;
    }

    public final void writeLong(long val){
        buffer[position + 7] = (byte) (val       );
        buffer[position + 6] = (byte) (val >>>  8);
        buffer[position + 5] = (byte) (val >>> 16);
        buffer[position + 4] = (byte) (val >>> 24);
        buffer[position + 3] = (byte) (val >>> 32);
        buffer[position + 2] = (byte) (val >>> 40);
        buffer[position + 1] = (byte) (val >>> 48);
        buffer[position    ] = (byte) (val >>> 56);
        position += 8;
    }

    public final void writeVarInt(int value){
        writeUInt((value << 1) ^ (value >> 31));
    }

    public final void  writeUInt(int value){
        while ((value & 0xFFFFFF80) != 0) {
            buffer[position] = (byte)((value & 0x7F) | 0x80);
            position++;
            value >>>= 7;
        }
        buffer[position] = (byte)(value & 0x7F);
        position++;
    }

    public final void ensureCapacity(int minCapacity) {
        minCapacity += position;
        // overflow-conscious code
        if (minCapacity > buffer.length){
            int oldCapacity = buffer.length;
            int newCapacity = oldCapacity << 1;
            if(newCapacity < 1024*16){
                newCapacity = 1024*16;
            }
            if (newCapacity - minCapacity < 0) {
                newCapacity = minCapacity;
            }
            buffer = Arrays.copyOf(buffer, newCapacity);
        }
    }

}
