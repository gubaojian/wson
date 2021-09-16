package com.github.gubaojian.pson.io;

import java.nio.Buffer;
import java.util.ArrayList;

public class Output {

    protected byte[] buffer;
    protected int position;
    private ArrayList refs;

    public Output() {
        this(1024*8);
    }

    public Output(int bufferSize) {
        buffer = new byte[bufferSize];
        position = 0;
    }

    public byte[] getPack() {
        byte[] bts = new byte[position];
        System.arraycopy(buffer, 0, bts, 0, position);
        return  bts;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getPosition() {
        return position;
    }

    public final void writeVarInt(int value){
        writeUInt((value << 1) ^ (value >> 31));
    }

    private final void  writeUInt(int value){
        while ((value & 0xFFFFFF80) != 0) {
            buffer[position] = (byte)((value & 0x7F) | 0x80);
            position++;
            value >>>= 7;
        }
        buffer[position] = (byte)(value & 0x7F);
        position++;
    }
}
