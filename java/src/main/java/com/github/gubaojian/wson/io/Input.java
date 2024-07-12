package com.github.gubaojian.wson.io;

/**
 * 解析基础类，方便手工操作，自定义数据格式
 * */
public class Input {
    private int position;
    private byte[] buffer;
    private int end;
    private int start;

    public Input(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }

    /**
     *  contains start， exclude end
     *  start <= data < end
     * */
    public Input(byte[] buffer, int start, int end) {
        this.buffer = buffer;
        this.position = start;
        this.start = start;
        this.end = end;
    }

    /**
     * none need call
     * */
    public void reset(){
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

    public final int getEnd() {
        return end;
    }

    public final int readVarInt(){
        int raw = readUInt();
        // This undoes the trick in putVarInt()
        int num = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return num ^ (raw & (1 << 31));
    }

    public final int readUInt(){
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

    public float readFloat() {
        int number = (((buffer[position + 3] & 0xFF)      ) +
                ((buffer[position + 2] & 0xFF) <<  8) +
                ((buffer[position + 1] & 0xFF) << 16) +
                ((buffer[position  ] & 0xFF) << 24));
        position +=4;
        return  Float.intBitsToFloat(number);
    }

}
