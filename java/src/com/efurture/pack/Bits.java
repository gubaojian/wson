package com.efurture.pack;

/**
 * Utility methods for packing/unpacking primitive values in/out of byte arrays
 * big endian, java is big endian
 */
class Bits {

    static boolean getBoolean(byte[] b, int off) {
        return b[off] != 0;
    }

    static int getInt(byte[] b, int off) {
        return ((b[off + 3] & 0xFF)      ) +
                ((b[off + 2] & 0xFF) <<  8) +
                ((b[off + 1] & 0xFF) << 16) +
                ((b[off    ]       ) << 24);
    }

    static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)      ) +
                ((b[off + 6] & 0xFFL) <<  8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off])      << 56);
    }

    static double getDouble(byte[] b, int off) {
        return Double.longBitsToDouble(getLong(b, off));
    }

    static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }


    static void putInt(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val       );
        b[off + 2] = (byte) (val >>>  8);
        b[off + 1] = (byte) (val >>> 16);
        b[off    ] = (byte) (val >>> 24);
    }



    static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val       );
        b[off + 6] = (byte) (val >>>  8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off    ] = (byte) (val >>> 56);
    }

    static void putDouble(byte[] b, int off, double val) {
        putLong(b, off, Double.doubleToLongBits(val));
    }

    static int putVarInt(byte[] bts, int off, int value){
        return putUInt(bts, off, (value << 1) ^ (value >> 31));
    }

    static int getVarInt(byte[] bts, int off){
        int raw = getUInt(bts, off);
        // This undoes the trick in putVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
    }

    static int putUInt(byte[] bts, int off, int value){
        int size = 0;
        while ((value & 0xFFFFFF80) != 0) {
            bts[off] = (byte)((value & 0x7F) | 0x80);
            off++;
            size ++;
            value >>>= 7;

        }
        bts[off] = (byte)(value & 0x7F);
        size ++;
        return size;
    }

    static int getUInt(byte[] bts, int off){
        int value = 0;
        int i = 0;
        int b;
        while (((b = bts[off]) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            off++;
            if (i > 35) {
                throw new IllegalArgumentException("Variable length quantity is too long");
            }
        }
        return value | (b << i);
    }
}
