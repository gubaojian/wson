/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.efurture.wson;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.util.*;

/**
 * fast binary json format for parse map and serialize map
 * Created by efurture on 2017/8/16.
 */
public class Wson {

    /**
     * skip map null values
     * */
    public static final boolean WriteMapNullValue = false;
    /**
     * wson data type
     * */
    private static final byte NULL_TYPE = '0';

    private static final byte STRING_TYPE = 's';

    private static final byte BOOLEAN_TYPE_TRUE = 't';

    private static final byte BOOLEAN_TYPE_FALSE = 'f';

    private static final byte NUMBER_INT_TYPE = 'i';

    private static final byte NUMBER_DOUBLE_TYPE = 'd';

    private static final byte ARRAY_TYPE = '[';

    private static final byte MAP_TYPE = '{';

    /**
     * StringUTF-16, byte order with native byte order
     * */
    private static final boolean IS_NATIVE_LITTLE_ENDIAN = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);


    /**
     * parse wson data  to object
     * @param  data  byte array
     * */
    public static Object parse(byte[] data){
        if(data == null){
            return  null;
        }
        try{
            Parser parser =  new Parser(data);
            Object object = parser.parse();
            parser.close();
            return object;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }


    /**
     * serialize object to wson data
     * */
    public static byte[] toWson(Object object){
        if(object == null){
            return  null;
        }
        Builder builder = new Builder();
        byte[]  bts  = builder.toWson(object);
        builder.close();
        return bts;
    }


    /**
     * wson data parser
     * */
    private static final class Parser {

        private int position = 0;
        private byte[] buffer;
        private char[]  charsBuffer;

        public Parser(byte[] buffer) {
            this.buffer = buffer;
            charsBuffer = localCharsBufferCache.get();
            if(charsBuffer != null){
                localCharsBufferCache.set(null);
            }else{
                charsBuffer = new char[512];
            }
        }


        public  final Object parse(){
            return  readObject();
        }

        public final void close(){
            position = 0;
            buffer = null;
            if(charsBuffer != null){
                localCharsBufferCache.set(charsBuffer);
            }
            charsBuffer = null;
        }

        private final Object readObject(){
            byte type  = readType();
            switch (type){
                case STRING_TYPE:
                    return readUTF16String();
                case NUMBER_INT_TYPE :
                    return  readVarInt();
                case MAP_TYPE:
                    return readMap();
                case ARRAY_TYPE:
                    return readArray();
                case NUMBER_DOUBLE_TYPE :
                    return readDouble();
                case BOOLEAN_TYPE_FALSE:
                    return  Boolean.FALSE;
                case BOOLEAN_TYPE_TRUE:
                    return  Boolean.TRUE;
                case NULL_TYPE:
                    return  null;
                default:
                    throw new RuntimeException("wson unhandled type " + type + " " +
                     position  +  " length " + buffer.length);
            }
        }

        private final Object readMap(){
            int size = readUInt();
            Map<String, Object> object = new JSONObject();;
            for(int i=0; i<size; i++){
                String key = readMapKeyUTF16();
                Object value = readObject();
                object.put(key, value);
            }
            return object;
        }

        private final Object readArray(){
            int length = readUInt();
            List<Object> array = new JSONArray(length);
            for(int i=0; i<length; i++){
                array.add(readObject());
            }
            return  array;
        }

        private  final byte readType(){
            byte type = buffer[position];
            position ++;
            return  type;
        }


        private final String readMapKeyUTF16() {
                int length = readUInt();
                length = length/2;
                if(charsBuffer.length < length){
                    charsBuffer = new char[length];
                }
                int hash = 5381;
                if(IS_NATIVE_LITTLE_ENDIAN){
                    for(int i=0; i<length; i++){
                        char ch = (char) ((buffer[position] & 0xFF) +
                                (buffer[position + 1] << 8));
                        charsBuffer[i] = (ch);
                        hash = ((hash << 5) + hash)  + ch;
                        position+=2;
                    }
                }else{
                    for(int i=0; i<length; i++){
                        char ch = (char) ((buffer[position + 1] & 0xFF) +
                                (buffer[position] << 8));
                        charsBuffer[i] = (ch);
                        hash = ((hash << 5) + hash)  + ch;
                        position+=2;
                    }
                }
                int globalIndex = (globalStringBytesCache.length - 1)&hash;
               String cache = globalStringBytesCache[globalIndex];
                if(cache != null
                        && cache.length() == length){
                    boolean isStringEqual  = true;
                    for(int i=0; i<length; i++){
                        if(charsBuffer[i] != cache.charAt(i)){
                            isStringEqual = false;
                            break;
                        }
                    }
                    if(isStringEqual) {
                        return cache;
                    }
                }
                cache = new String(charsBuffer, 0, length);
                if(length < 64) {
                    globalStringBytesCache[globalIndex] = cache;
                }
                return  cache;
        }

        private final String readUTF16String(){
            int length = readUInt()/2;
            if(charsBuffer.length < length){
                charsBuffer = new char[length];
            }
            if(IS_NATIVE_LITTLE_ENDIAN){
                for(int i=0; i<length; i++){
                    char ch = (char) ((buffer[position] & 0xFF) +
                            (buffer[position + 1] << 8));
                    charsBuffer[i] = (ch);
                    position+=2;
                }
            }else{
                for(int i=0; i<length; i++){
                    char ch = (char) ((buffer[position + 1] & 0xFF) +
                            (buffer[position] << 8));
                    charsBuffer[i] = (ch);
                    position+=2;
                }
            }
            return  new String(charsBuffer, 0, length);
        }





        private   final int readVarInt(){
            int raw = readUInt();
            // This undoes the trick in putVarInt()
            int num = (((raw << 31) >> 31) ^ raw) >> 1;
            // This extra step lets us deal with the largest signed values by treating
            // negative results from read unsigned methods as like unsigned values.
            // Must re-flip the top bit if the original read value had it set.
            return num ^ (raw & (1 << 31));
        }

        private final  int readUInt(){
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

        private final long readLong(){
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

        private  final double readDouble(){
            double number = Double.longBitsToDouble(readLong());
            return  number;
        }
    }

    /**
     * wson builder
     * */
    public static final class Builder {

        private byte[] buffer;
        private int position;
        private ArrayList refs;
        private final static ThreadLocal<byte[]> bufLocal = new ThreadLocal<byte[]>();
        private final static ThreadLocal<ArrayList> refsLocal = new ThreadLocal<ArrayList>();



        public Builder(){
            buffer =  bufLocal.get();
            if(buffer != null) {
                 bufLocal.set(null);
            }else{
                buffer = new byte[1024];
            }
            refs = refsLocal.get();
            if(refs != null){
                refsLocal.set(null);
            }else{
                refs = new ArrayList<>(16);
            }
        }


        private final byte[] toWson(Object object){
            writeObject(object);
            byte[] bts = new byte[position];
            System.arraycopy(buffer, 0, bts, 0, position);
            return  bts;
        }

        private final void close(){
            if(buffer.length <= 1024*16){
                bufLocal.set(buffer);
            }
            if(refs.isEmpty()){
                refsLocal.set(refs);
            }else{
                refs.clear();
            }
            refs = null;
            buffer = null;
            position = 0;
        }

        private final void writeObject(Object object) {
            if(object instanceof  CharSequence){
                ensureCapacity(2);
                writeByte(STRING_TYPE);
                writeUTF16String((CharSequence) object);
                return;
            }else if (object instanceof Map){
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refs.add(object);
                Map map = (Map) object;
                writeMap(map);
                refs.remove(refs.size()-1);
                return;
            }else if (object instanceof List){
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refs.add(object);
                ensureCapacity(8);
                List list = (List) object;
                writeByte(ARRAY_TYPE);
                writeUInt(list.size());
                for(Object value : list){
                    writeObject(value);
                }
                refs.remove(refs.size()-1);
                return;
            }else if (object instanceof Number){
                Number number = (Number) object;
                writeNumber(number);
                return;
            }else if (object instanceof  Boolean){
                ensureCapacity(2);
                Boolean value  = (Boolean) object;
                if(value){
                    writeByte(BOOLEAN_TYPE_TRUE);
                }else{
                    writeByte(BOOLEAN_TYPE_FALSE);
                }
                return;
            }else if(object == null){
                ensureCapacity(2);
                writeByte(NULL_TYPE);
                return;
            }else if (object.getClass().isArray()){
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refs.add(object);
                ensureCapacity(8);
                int length = Array.getLength(object);
                writeByte(ARRAY_TYPE);
                writeUInt(length);
                for(int i=0; i<length; i++){
                    Object value = Array.get(object, i);
                    writeObject(value);
                }
                refs.remove(refs.size()-1);
                return;
            }else  if(object instanceof  Date){
                ensureCapacity(10);
                double date = ((Date)object).getTime();
                writeByte(NUMBER_DOUBLE_TYPE);
                writeDouble(date);
            }else  if(object instanceof  Calendar){
                ensureCapacity(10);
                double date = ((Calendar)object).getTime().getTime();
                writeByte(NUMBER_DOUBLE_TYPE);
                writeDouble(date);
            }else  if(object instanceof  Collection){
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refs.add(object);
                ensureCapacity(8);
                Collection list = (Collection) object;
                writeByte(ARRAY_TYPE);
                writeUInt(list.size());
                for(Object value : list){
                    writeObject(value);
                }
                refs.remove(refs.size()-1);
            }else{
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                }else {
                    refs.add(object);
                    writeMap(toMap(object));
                    refs.remove(refs.size()-1);
                }
                return;
            }
        }

        private final void writeNumber(Number number) {
            ensureCapacity(12);
            if(number instanceof  Integer
                    || number instanceof  Short
                    || number instanceof  Byte){
                writeByte(NUMBER_INT_TYPE);
                writeVarInt(number.intValue());
            }else{
                if(number instanceof  Float){
                    float value = number.floatValue();
                    if(value == Math.ceil(value)){
                        writeByte(NUMBER_INT_TYPE);
                        writeVarInt(number.intValue());
                        return;
                    }
                }
                writeByte(NUMBER_DOUBLE_TYPE);
                if(number instanceof Double){
                    writeDouble(number.doubleValue());
                }else{
                    writeDouble(Double.parseDouble(number.toString()));
                }
            }
        }

        private final  void writeMap(Map map) {
            if(WriteMapNullValue){
                ensureCapacity(8);
                writeByte(MAP_TYPE);
                writeUInt(map.size());
                Set<Map.Entry<Object,Object>>  entries = map.entrySet();
                for(Map.Entry<Object,Object> entry : entries){
                    writeMapKeyUTF16(entry.getKey().toString());
                    writeObject(entry.getValue());
                }
            }else{
                Set<Map.Entry<Object,Object>>  entries = map.entrySet();
                int nullValueSize = 0;
                for(Map.Entry<Object,Object> entry : entries){
                    if(entry.getValue() == null){
                        nullValueSize++;
                    }
                }

                ensureCapacity(8);
                writeByte(MAP_TYPE);
                writeUInt(map.size()-nullValueSize);
                for(Map.Entry<Object,Object> entry : entries){
                    if(entry.getValue() == null){
                        continue;
                    }
                    writeMapKeyUTF16(entry.getKey().toString());
                    writeObject(entry.getValue());
                }
            }
        }


        private final void writeByte(byte type){
            buffer[position] = type;
            position++;
        }

        private  final Map  toMap(Object object){
            return WsonAdapter.toMap(object);
        }

        private  final void writeMapKeyUTF16(String value){
            writeUTF16String(value);
        }




        /**
         * writeString UTF-16
         * */
        private  final void writeUTF16String(CharSequence value){
            int length = value.length();
            ensureCapacity(length*2 + 8);
            writeUInt(length*2);
            if(IS_NATIVE_LITTLE_ENDIAN){
                for(int i=0; i<length; i++){
                    char ch = value.charAt(i);
                    buffer[position] = (byte) (ch);
                    buffer[position+1] = (byte) (ch >>> 8);
                    position+=2;
                }
            }else{
                for(int i=0; i<length; i++){
                    char ch = value.charAt(i);
                    buffer[position + 1] = (byte) (ch      );
                    buffer[position] = (byte) (ch >>> 8);
                    position+=2;
                }
            }
        }


        private final void writeDouble(double value){
            writeLong(Double.doubleToLongBits(value));
        }

        private final void writeLong(long val){
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

        private final void writeVarInt(int value){
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


        private final void ensureCapacity(int minCapacity) {
            minCapacity += position;
            // overflow-conscious code
            if (minCapacity - buffer.length > 0){
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


    /**
     * cache json property key, most of them all same
     * */
    private static final int GLOBAL_STRING_CACHE_SIZE = 2*1024;
    private static final ThreadLocal<char[]> localCharsBufferCache = new ThreadLocal<>();
    private static final String[] globalStringBytesCache = new String[GLOBAL_STRING_CACHE_SIZE];
}