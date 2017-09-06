package com.efurture.tson;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static com.efurture.tson.Tson.STRING_UTF8_CHARSET_NAME;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/5.
 */
public class SymbolTable {

    private final Entry[] symbols;
    private final int  indexMask;

    public SymbolTable(int tableSize){
        this.indexMask = tableSize - 1;
        this.symbols = new Entry[tableSize];
    }


    public String findSymbol(byte[] buffer, final int offset, final int length) throws UnsupportedEncodingException {
        if(buffer[offset] <= 0 || length > 32){
            return  null;
        }
        int hash = hash(buffer, offset, length);
        if(hash < 0){
            return null;
        }
        int index = indexMask & hash;
        Entry entry = symbols[index];
        if(entry != null){
            if (hash == entry.hash //
                    && length == entry.length
                    && isBufferEquals(buffer, offset, length, entry.bts, entry.offset)) {
                return entry.value;
            }
        }

        String string = new String(buffer, offset, length, STRING_UTF8_CHARSET_NAME);
        if(entry == null){
            string = string.intern();
           entry = new Entry(string, buffer, offset, length, hash);
           symbols[index] = entry;
        }

        return string;
    }


    private static final int hash(byte[] buffer, int offset, int length){
        int hash = 0;
        for(int i=offset; i<offset+length; i++){
            byte  bt  = buffer[offset];
            if(bt < 0){
                return -1;
            }
            hash  = 31 * hash  + bt;
        }
        return hash;
    }

    private static final boolean isBufferEquals(byte[] src, final int offset, final int length,
                                                byte[] entry, int  entryOffset){
        for(int i=0; i<length; i++){
            if(src[offset+i] != entry[entryOffset + i]){
                return  false;
            }
        }
        return  true;
    }




    static class Entry {
        String value;
        byte[] bts;
        int offset;
        int length;
        int hash;

        Entry(String value, byte[] bts, int offset, int length, int hash) {
            this.value = value;
            this.bts = bts;
            this.offset = offset;
            this.length = length;
            this.hash = hash;
        }
    }
}
