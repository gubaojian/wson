package com.efurture.tson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by 剑白(jianbai.gbj) on 2017/8/16.
 */
public class Tson {

    /**
     * tson data type
     * */
    public static final byte NULL_TYPE = '0';

    public static final byte STRING_TYPE = 's';

    public static final byte BOOLEAN_TYPE = 'b';

    public static final byte NUMBER_INT_TYPE = 'i';

    public static final byte NUMBER_DOUBLE_TYPE = 'd';

    public static final byte ARRAY_TYPE = '[';

    public static final byte MAP_TYPE = '{';


    public static final String STRING_UTF8_CHARSET_NAME = "UTF-8";

    /**
     * parse tson data  to object
     * @param  data  byte array
     * */
    public static Object parse(byte[] data){
        return new Parser(data).parse();
    }

    /**
     * serialize object to tson data
     * */
    public static byte[] toTson(Object object){
        return new Builder().toTson(object);
    }


    /**
     * tson data parser
     * */
    public static class Parser {

        protected int position = 0;
        protected byte[] buffer;

        public Parser(byte[] buffer) {
            this.buffer = buffer;
        }


        public  Object parse(){
            return  readObject();
        }

        protected Object readObject(){
            byte type  = readType();
            switch (type){
                case STRING_TYPE:
                    return  readString();
                case NUMBER_INT_TYPE :
                    return  readVarInt();
                case NUMBER_DOUBLE_TYPE :
                    return readDouble();
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
            Map<String, Object> object = createMap();
            for(int i=0; i<size; i++){
                String key = readString();
                Object value = readObject();
                object.put(key, value);
            }
            return object;
        }

        protected Object readArray(){
            int length = readUInt();
            List<Object> array = createArray(length);
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
                string = new String(buffer, position, length, STRING_UTF8_CHARSET_NAME);
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


        protected   int readVarInt(){
            int raw = readUInt();
            // This undoes the trick in putVarInt()
            int num = (((raw << 31) >> 31) ^ raw) >> 1;
            // This extra step lets us deal with the largest signed values by treating
            // negative results from read unsigned methods as like unsigned values.
            // Must re-flip the top bit if the original read value had it set.
            return num ^ (raw & (1 << 31));
        }

        protected   int readUInt(){
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

        protected long readLong(){
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

        protected  double readDouble(){
            double number = Double.longBitsToDouble(readLong());
            return  number;
        }

        protected Map<String,Object> createMap(){
            return new HashMap<>();
        }

        protected List<Object> createArray(int length){
            return new ArrayList<>(length);
        }

    }

    /**
     * tson builder
     * */
    public static class Builder {

        public static final String METHOD_PREFIX_GET = "get";
        public static final String METHOD_PREFIX_IS = "is";

        private static Tson.LruCache<String, List<Method>> methodsCache = new Tson.LruCache<>(32);
        private static Tson.LruCache<String, Field[]> fieldsCache = new Tson.LruCache<>(32);


        private byte[] buffer;
        private int position;
        private ArrayList refsList;

        public Builder(){
            buffer = new byte[256];
            refsList = new ArrayList<>();
        }


        public byte[] toTson(Object object){
            writeObject(object);
            byte[] bts = Arrays.copyOf(buffer, position);
            buffer = null;
            position = 0;
            return  bts;
        }

        protected void writeObject(Object object) {
            if(object == null){
                ensureCapacity(2);
                writeByte(NULL_TYPE);
            }else if(object instanceof  String){
                ensureCapacity(2);
                writeByte(STRING_TYPE);
                writeString((String) object);
            }else if (object instanceof Map){
                if(refsList.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refsList.add(object);
                Map<String,Object> map = (Map) object;
                ensureCapacity(8);
                writeByte(MAP_TYPE);
                writeUInt(map.size());
                Set<Map.Entry<String,Object>>  entries = map.entrySet();
                for(Map.Entry<String,Object> entry : entries){
                    writeString(entry.getKey().toString());
                    writeObject(entry.getValue());
                }
                refsList.remove(refsList.size()-1);
            }else if (object instanceof List){
                if(refsList.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refsList.add(object);
                ensureCapacity(8);
                List list = (List) object;
                writeByte(ARRAY_TYPE);
                writeUInt(list.size());
                for(Object value : list){
                    writeObject(value);
                }
                refsList.remove(refsList.size()-1);
            }else if (object instanceof Number){
                ensureCapacity(12);
                Number number = (Number) object;
                if(object instanceof  Integer || object instanceof  Short){
                    writeByte(NUMBER_INT_TYPE);
                    writeVarInt(number.intValue());
                }else{
                    writeByte(NUMBER_DOUBLE_TYPE);
                    writeDouble(number.doubleValue());
                }
            }else if (isBoolean(object)){
                ensureCapacity(2);
                writeByte(BOOLEAN_TYPE);
                Boolean value  = (Boolean) object;
                if(value){
                    writeByte((byte) 1);
                }else{
                    writeByte((byte) 0);
                }
            }else if (object.getClass().isArray()){
                if(refsList.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refsList.add(object);
                ensureCapacity(8);
                int length = Array.getLength(object);
                writeByte(ARRAY_TYPE);
                writeUInt(length);
                for(int i=0; i<length; i++){
                    Object value = Array.get(object, i);
                    writeObject(value);
                }
                refsList.remove(refsList.size()-1);
            }else{
                if(refsList.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                }else {
                    refsList.add(object);
                    writeObject(toMap(object));
                    refsList.remove(refsList.size()-1);
                }
            }
        }

        protected boolean isBoolean(Object object){
            if(object instanceof  Boolean){
                return  true;
            }
            return  false;
        }

        protected void writeByte(byte type){
            buffer[position] = type;
            position++;
        }

        protected Map  toMap(Object object){
            Map map = new HashMap<>();
            try {
                Class<?> targetClass = object.getClass();
                String key = targetClass.getName();
                List<Method> methods = methodsCache.get(key);
                if(methods == null){
                    methods = new ArrayList<>();
                    Class<?> parentClass =  targetClass;
                    while (parentClass != Object.class){
                        Method[] declaredMethods = parentClass.getDeclaredMethods();
                        for(Method declaredMethod : declaredMethods){
                            String methodName = declaredMethod.getName();
                            if(methodName.startsWith(METHOD_PREFIX_GET)
                                    || methodName.startsWith(METHOD_PREFIX_IS)) {
                                if(Modifier.isPublic(declaredMethod.getModifiers())) {
                                    methods.add(declaredMethod);
                                }
                            }
                        }
                        parentClass = parentClass.getSuperclass();
                    }
                    methodsCache.put(key, methods);
                }
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
                Field[] fields = fieldsCache.get(key);
                if(fields == null) {
                    fields = targetClass.getFields();
                    fieldsCache.put(key, fields);

                }
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
                throw  new RuntimeException(e);
            }
            return  map;
        }

        protected void writeString(String value){
            byte[] bts = null;
            try {
                bts = value.getBytes(STRING_UTF8_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                bts = value.getBytes();
            }
            ensureCapacity(bts.length + 8);
            writeUInt(bts.length);
            writeBytes(bts);
        }

        protected void writeDouble(double value){
            writeLong(Double.doubleToLongBits(value));
        }

        protected void writeLong(long val){
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

        protected void writeVarInt(int value){
            writeUInt((value << 1) ^ (value >> 31));
        }

        protected void  writeUInt(int value){
            while ((value & 0xFFFFFF80) != 0) {
                buffer[position] = (byte)((value & 0x7F) | 0x80);
                position++;
                value >>>= 7;
            }
            buffer[position] = (byte)(value & 0x7F);
            position++;
        }

        protected void  writeBytes(byte[] bts){
            System.arraycopy(bts, 0, buffer, position, bts.length);
            position += bts.length;
        }

        private void ensureCapacity(int minCapacity) {
            minCapacity += position;
            // overflow-conscious code
            if (minCapacity - buffer.length > 0){
                int oldCapacity = buffer.length;
                int newCapacity = oldCapacity << 1;
                if (newCapacity - minCapacity < 0)
                    newCapacity = minCapacity;
                buffer = Arrays.copyOf(buffer, newCapacity);
            }
        }
    }
    /**
     * lru cache
     * */
    public static class LruCache<K,V> extends LinkedHashMap<K,V> {
        private int cacheSize;

        public LruCache(int cacheSize) {
            super(cacheSize, 0.75f, true);
            this.cacheSize = cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > cacheSize;
        }
    }

}
