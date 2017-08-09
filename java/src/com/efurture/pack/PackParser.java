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
package com.efurture.pack;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by furture on 2017/8/1.
 */
public class PackParser {
    private int position = 0;
    private byte[] buffer;

    public PackParser(byte[] buffer) {
        this.buffer = buffer;
    }

    public  Object parse(){
         return  readObject();
    }

    private Object readObject(){
        byte type  = readType();
        switch (type){
            case STRING_TYPE:
                return  readString();
            case NUMBER_TYPE:{
                   byte flag = readType();
                   if(flag == NUMBER_INT_TYPE){
                        return  readInt();
                   }else{ //default double
                       return  readDouble();
                   }
                 }
            case BOOLEAN_TYPE:
                return  readBoolean();
            case ARRAY_TYPE:
                return readArray();
            case MAP_TYPE:
                return readMap();
            case NULL_TYPE:
                return  null;
            default:
                break;

        }
        return  null;
    }

    protected Object readMap(){
        int size = readUInt();
        Map<String, Object> object = new HashMap<>();
        for(int i=0; i<size; i++){
            String key = readString();
            Object value = readObject();
            object.put(key, value);
        }
        return object;
    }

    protected Object readArray(){
        int length = readUInt();
        ArrayList<Object> array = new ArrayList<>(length);
        for(int i=0; i<length; i++){
            array.add(readObject());
        }
        return  array;
    }

    protected  byte readType(){
        byte type = buffer[position];
        position ++;
        return  type;
    }

    protected String readString(){
        int length = readUInt();
        String string = null;
        try {
            string = new String(buffer, position, length, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            string = new String(buffer, position, length);
        }
        position += length;
        return  string;
    }

    protected   boolean readBoolean(){
        byte bt = buffer[position];
        position++;
        return  bt != 0;
    }


    protected   int readInt(){
        int value = Bits.getInt(buffer, position);
        position +=4;
        return  value;
    }

    protected   int readUInt(){
        int value = 0;
        int i = 0;
        int b;
        while (((b = buffer[position]) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            position+=1;
            if (i > 28) {
                throw new IllegalArgumentException("Variable length quantity is too long");
            }
        }
        position+=1;
        return value | (b << i);
    }

    protected  double readDouble(){
        double number = Bits.getDouble(buffer, position);
        position += 8;
        return  number;
    }


    public static final byte NULL_TYPE = '0';

    public static final byte NUMBER_TYPE = 'n';

    public static final byte STRING_TYPE = 's';

    public static final byte BOOLEAN_TYPE = 'b';

    public static final byte NUMBER_INT_TYPE = 'i';

    public static final byte NUMBER_DOUBLE_TYPE = 'd';

    public static final byte ARRAY_TYPE = '[';

    public static final byte MAP_TYPE = '{';


    public static final String CHARSET_NAME = "UTF-8";
}
