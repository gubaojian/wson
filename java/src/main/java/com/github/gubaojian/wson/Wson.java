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
package com.github.gubaojian.wson;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.gubaojian.wson.cache.LruCache;
import com.github.gubaojian.wson.config.Protocol;
import com.github.gubaojian.wson.io.Input;
import com.github.gubaojian.wson.io.Output;
import com.github.gubaojian.wson.io.Platform;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * parse wson data  to object, please use WXJsonUtils.parseWson
     * @param  data  byte array
     * */
    public static final Object parse(byte[] data){
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
     * serialize object to wson data, please use WXJsonUtils.toWsonOrJsonWXJSObject
     * */
    public static final byte[] toWson(Object object){
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
        private char[]  charsBuffer;
        private Input input = null;
        private Parser(byte[] buffer) {
            this.input = new Input(buffer);
            charsBuffer = Platform.localCharsBufferCache.get();
            //FIXME Lazy Init
            if(charsBuffer != null){
                Platform.localCharsBufferCache.set(null);
            }else{
                charsBuffer = new char[512];
            }
        }

        private final Object parse(){
            return  readObject();
        }

        private final void close(){
            input.close();
            if(charsBuffer != null){
                Platform.localCharsBufferCache.set(charsBuffer);
            }
            charsBuffer = null;
        }

        private final Object readObject(){
            byte type  = input.readType();
            switch (type){
                case Protocol.STRING_TYPE:
                    return readUTF16String();
                case Protocol.NUMBER_INT_TYPE :
                    return  input.readVarInt();
                case Protocol.NUMBER_FLOAT_TYPE :
                    return  input.readFloat();
                case Protocol.MAP_TYPE:
                    return readMap();
                case Protocol.ARRAY_TYPE:
                    return readArray();
                case Protocol.NUMBER_DOUBLE_TYPE :
                    return input.readDouble();
                case Protocol.NUMBER_LONG_TYPE :
                    return  input.readLong();
                case Protocol.NUMBER_BIG_INTEGER_TYPE :
                    return  new BigInteger(readUTF16String());
                case Protocol.NUMBER_BIG_DECIMAL_TYPE :
                    return  new BigDecimal(readUTF16String());
                case Protocol.BOOLEAN_TYPE_FALSE:
                    return  Boolean.FALSE;
                case Protocol.BOOLEAN_TYPE_TRUE:
                    return  Boolean.TRUE;
                case Protocol.NULL_TYPE:
                    return  null;
                default:
                    throw new RuntimeException("wson unhandled type " + type + " " +
                            input.getPosition()  +  " length " + input.getEnd());
            }
        }

        private final Object readMap(){
            int size = input.readUInt();
            Map<String, Object> object = new JSONObject();;
            for(int i=0; i<size; i++){
                String key = readMapKeyUTF16();
                Object value = readObject();
                object.put(key, value);
            }
            return object;
        }

        private final Object readArray(){
            int length = input.readUInt();
            List<Object> array = new JSONArray(length);
            for(int i=0; i<length; i++){
                array.add(readObject());
            }
            return  array;
        }

        /**
         * speed read for json key with cache
         * FIXME 移动出去。
         * */
        private final String readMapKeyUTF16() {
            int length = input.readUInt()/2; //one char 2 byte
            //TODO improve, REMOVE FROM THREAD LOCAL
            charsBuffer = Platform.localCharsBufferCache.get();
            if(charsBuffer != null){
                Platform.localCharsBufferCache.set(null);
            }else{
                if (length > 256) {
                    charsBuffer = new char[length]; //设置cache
                } else {
                    charsBuffer = new char[256];
                }
            }
            if(charsBuffer.length < length){
                charsBuffer = new char[length];
            }
            int hash = 5381;
            byte[] buffer = input.getBuffer();
            if(Platform.IS_NATIVE_LITTLE_ENDIAN){
                for(int i=0; i<length; i++){
                    int position = input.getPosition();
                    char ch = (char) ((buffer[position] & 0xFF) +
                            (buffer[position + 1] << 8));
                    charsBuffer[i] = (ch);
                    hash = ((hash << 5) + hash)  + ch;
                    input.move(2);
                }
            }else{
                for(int i=0; i<length; i++){
                    int position = input.getPosition();
                    char ch = (char) ((buffer[position + 1] & 0xFF) +
                            (buffer[position] << 8));
                    charsBuffer[i] = (ch);
                    hash = ((hash << 5) + hash)  + ch;
                    input.move(2);
                }
            }
            String[] globalStringBytesCache = Platform.globalStringBytesCache;
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
        /**
         * method for json value
         * FIXME 移动出去。
         * */
        private final String readUTF16String(){
            int length = input.readUInt()/2; //one char 2 byte
            if(charsBuffer == null || charsBuffer.length < length){
                charsBuffer = new char[length];
            }
            //FIXME String UTF8
            final byte[] buffer = input.getBuffer();
            if(Platform.IS_NATIVE_LITTLE_ENDIAN){
                for(int i=0; i<length; i++){
                    int position = input.getPosition();
                    char ch = (char) ((buffer[position] & 0xFF) +
                            (buffer[position + 1] << 8));
                    charsBuffer[i] = (ch);
                    input.move(2);
                }
            }else{
                for(int i=0; i<length; i++){
                    int position = input.getPosition();
                    char ch = (char) ((buffer[position + 1] & 0xFF) +
                            (buffer[position] << 8));
                    charsBuffer[i] = (ch);
                    input.move(2);
                }
            }
            return  new String(charsBuffer, 0, length);
        }

    }

    /**
     * wson builder
     * */
    private static final class Builder {
        private ArrayList refs;
        private Output output;
        private final static ThreadLocal<byte[]> bufLocal = new ThreadLocal<byte[]>();
        private final static ThreadLocal<ArrayList> refsLocal = new ThreadLocal<ArrayList>();



        private Builder(){
            output = new Output();
            refs = refsLocal.get();
            if(refs != null){
                refsLocal.set(null);
            }else{
                refs = new ArrayList<>(16); //FIXME 用set提示查找性能，或者用lnkedlist，因为一般深度不深
            }
        }

        private final byte[] toWson(Object object){
            writeObject(object);
            return  output.toBytes();
        }

        private final void close(){
            output.close();
            if(refs.isEmpty()){
                refsLocal.set(refs);
            }else{
                refs.clear();
            }
            refs = null;
        }

        private final void writeObject(Object object) {
            if(object instanceof  CharSequence){
                output.ensureCapacity(2);
                output.writeByte(Protocol.STRING_TYPE);
                writeUTF16String((CharSequence) object);
                return;
            }else if (object instanceof Map){
                if(refs.contains(object)){
                    output.ensureCapacity(2);
                    output.writeByte(Protocol.NULL_TYPE);
                    return;
                }
                refs.add(object);
                Map map = (Map) object;
                writeMap(map);
                refs.remove(refs.size()-1);
                return;
            }else if (object instanceof List){
                if(refs.contains(object)){
                    output.ensureCapacity(2);
                    output.writeByte(Protocol.NULL_TYPE);
                    return;
                }
                refs.add(object);
                output.ensureCapacity(8);
                List list = (List) object;
                output.writeByte(Protocol.ARRAY_TYPE);
                output.writeUInt(list.size());
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
                output.ensureCapacity(2);
                Boolean value  = (Boolean) object;
                if(value){
                    output.writeByte(Protocol.BOOLEAN_TYPE_TRUE);
                }else{
                    output.writeByte(Protocol.BOOLEAN_TYPE_FALSE);
                }
                return;
            }else if(object == null){
                output.ensureCapacity(2);
                output.writeByte(Protocol.NULL_TYPE);
                return;
            }else if (object.getClass().isArray()){
                if(refs.contains(object)){
                    output.ensureCapacity(2);
                    output.writeByte(Protocol.NULL_TYPE);
                    return;
                }
                refs.add(object);
                output.ensureCapacity(8);
                int length = Array.getLength(object);
                output.writeByte(Protocol.ARRAY_TYPE);
                output.writeUInt(length);
                for(int i=0; i<length; i++){
                    Object value = Array.get(object, i);
                    writeObject(value);
                }
                refs.remove(refs.size()-1);
            }else  if(object instanceof  Date){
                output.ensureCapacity(10);
                double date = ((Date)object).getTime();
                output.writeByte(Protocol.NUMBER_DOUBLE_TYPE);
                output.writeDouble(date);
            }else  if(object instanceof  Calendar){
                output.ensureCapacity(10);
                double date = ((Calendar)object).getTime().getTime();
                output.writeByte(Protocol.NUMBER_DOUBLE_TYPE);
                output.writeDouble(date);
            }else  if(object instanceof  Collection){
                if(refs.contains(object)){
                    output.ensureCapacity(2);
                    output.writeByte(Protocol.NULL_TYPE);
                    return;
                }
                refs.add(object);
                output.ensureCapacity(8);
                Collection list = (Collection) object;
                output.writeByte(Protocol.ARRAY_TYPE);
                output.writeUInt(list.size());
                for(Object value : list){
                    writeObject(value);
                }
                refs.remove(refs.size()-1);
            }else{
                if(refs.contains(object)){
                    output.ensureCapacity(2);
                    output.writeByte(Protocol.NULL_TYPE);
                }else {
                    refs.add(object);
                    if(object.getClass().isEnum()){
                        writeObject(JSON.toJSONString(object));
                    }else{
                        writeAdapterObject(object);
                    }
                    refs.remove(refs.size()-1);
                }
            }
        }

        private final void writeNumber(Number number) {
            output.ensureCapacity(12);
            if(number instanceof  Integer){
                output.writeByte(Protocol.NUMBER_INT_TYPE);
                output.writeVarInt(number.intValue());
                return;
            }

            if(number instanceof Float){
                output.writeByte(Protocol.NUMBER_FLOAT_TYPE);
                output.writeFloat(number.floatValue());
                return;
            }
            if(number instanceof  Double){
                output.writeByte(Protocol.NUMBER_DOUBLE_TYPE);
                output.writeDouble(number.doubleValue());
                return;
            }

            if(number instanceof  Long){
                output.writeByte(Protocol.NUMBER_LONG_TYPE);
                output.writeLong(number.longValue());
                return;
            }

            if(number instanceof  Short
                    || number instanceof  Byte){
                output.writeByte(Protocol.NUMBER_INT_TYPE);
                output.writeVarInt(number.intValue());
                return;
            }

            if(number instanceof BigInteger){
                output.writeByte(Protocol.NUMBER_BIG_INTEGER_TYPE);
                writeUTF16String(number.toString());
                return;
            }

            if(number instanceof BigDecimal){
                String value = number.toString();
                double doubleValue = number.doubleValue();
                if(value.equals(Double.toString(doubleValue))){
                    output.writeByte(Protocol.NUMBER_DOUBLE_TYPE);
                    output.writeDouble(doubleValue);
                }else {
                    output.writeByte(Protocol.NUMBER_BIG_DECIMAL_TYPE);
                    writeUTF16String(value);
                }
                return;
            }
            output.writeByte(Protocol.STRING_TYPE);
            writeUTF16String(number.toString());

        }

        private final  void writeMap(Map map) {
            if(WriteMapNullValue){
                output.ensureCapacity(8);
                output.writeByte(Protocol.MAP_TYPE);
                output.writeUInt(map.size());
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

                output.ensureCapacity(8);
                output.writeByte(Protocol.MAP_TYPE);
                output.writeUInt(map.size()-nullValueSize);
                for(Map.Entry<Object,Object> entry : entries){
                    if(entry.getValue() == null){
                        continue;
                    }
                    writeMapKeyUTF16(entry.getKey().toString());
                    writeObject(entry.getValue());
                }
            }
        }



        private final void writeAdapterObject(Object object){
            if(specialClass.get(object.getClass().getName()) != null){
                writeObject(JSON.toJSON(object));
                return;
            }
            try{
                writeMap(toMap(object));
            }catch (Exception e){
                specialClass.put(object.getClass().getName(), true);
                writeObject(JSON.toJSON(object));
            }
        }

        private  final Map  toMap(Object object){
            Map map = new JSONObject();
            try {
                Class<?> targetClass = object.getClass();
                String key = targetClass.getName();
                List<Method> methods = getBeanMethod(key, targetClass);
                for (Method method : methods) {
                    String methodName = method.getName();
                    if (methodName.startsWith(METHOD_PREFIX_GET)) {
                        Object value = method.invoke(object);
                        if(value != null){
                            StringBuilder builder = new StringBuilder(method.getName().substring(3));
                            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
                            map.put(builder.toString(), (Object) value);
                        }
                    }else if(methodName.startsWith(METHOD_PREFIX_IS)){
                        Object value = method.invoke(object);
                        if(value != null){
                            StringBuilder builder = new StringBuilder(method.getName().substring(2));
                            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
                            map.put(builder.toString(), value);
                        }
                    }
                }
                List<Field> fields = getBeanFields(key, targetClass);
                for(Field field : fields){
                    String fieldName = field.getName();
                    if(map.containsKey(fieldName)){
                        continue;
                    }
                    Object value  = field.get(object);
                    if(value == null){
                        continue;
                    }
                    map.put(fieldName, value);
                }
            }catch (Exception e){
                if(e instanceof  RuntimeException){
                    throw  (RuntimeException)e;
                }else{
                    throw  new RuntimeException(e);
                }
            }
            return  map;
        }

        private  final void writeMapKeyUTF16(String value){
            writeUTF16String(value);
        }



        /**
         * writeString UTF-16
         * */
        private  final void writeUTF16String(CharSequence value){
            int length = value.length();
            output.ensureCapacity(length*2 + 8);
            output.writeUInt(length*2);
            byte[] buffer = output.getBuffer();
            if(Platform.IS_NATIVE_LITTLE_ENDIAN){
                for(int i=0; i<length; i++){
                    int position = output.getPosition();
                    char ch = value.charAt(i);
                    buffer[position] = (byte) (ch);
                    buffer[position+1] = (byte) (ch >>> 8);
                    output.move(2);
                }
            }else{
                for(int i=0; i<length; i++){
                    int position = output.getPosition();
                    char ch = value.charAt(i);
                    buffer[position + 1] = (byte) (ch      );
                    buffer[position] = (byte) (ch >>> 8);
                    output.move(2);
                }
            }
        }


    }



    /**
     * lru cache, to map helper
     * */
    private static final String METHOD_PREFIX_GET = "get";
    private static final String METHOD_PREFIX_IS = "is";
    private static LruCache<String, List<Method>> methodsCache = new LruCache<>(128);
    private static LruCache<String, List<Field>> fieldsCache = new LruCache<>(128);
    private static LruCache<String, Boolean> specialClass = new LruCache<>(16);


    private static final List<Method> getBeanMethod(String key, Class targetClass){
        List<Method> methods = methodsCache.get(key);
        if(methods == null){
            methods = new ArrayList<>();
            Method[]  allMethods = targetClass.getMethods();
            for(Method method : allMethods){
                if(method.getDeclaringClass() == Object.class){
                    continue;
                }
                if( (method.getModifiers() & Modifier.STATIC) != 0){
                    continue;
                }
                String methodName = method.getName();
                if(methodName.startsWith(METHOD_PREFIX_GET)
                        || methodName.startsWith(METHOD_PREFIX_IS)) {
                    if(method.getAnnotation(JSONField.class) != null){
                        throw new UnsupportedOperationException("getBeanMethod JSONField Annotation Not Handled, Use toJSON");
                    }
                    methods.add(method);
                }
            }
            methodsCache.put(key, methods);
        }
        return methods;
    }



    private static  final List<Field> getBeanFields(String key, Class targetClass){
        List<Field> fieldList = fieldsCache.get(key);
        if(fieldList == null) {
            Field[] fields = targetClass.getFields();
            fieldList = new ArrayList<>(fields.length);
            for(Field field : fields){
                if((field.getModifiers() & Modifier.STATIC) != 0){
                    continue;
                }
                if(field.getAnnotation(JSONField.class) != null){
                    throw new UnsupportedOperationException("getBeanMethod JSONField Annotation Not Handled, Use toJSON");
                }
                fieldList.add(field);
            }
            fieldsCache.put(key, fieldList);
        }
        return  fieldList;
    }

}
