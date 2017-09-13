package com.efurture.tson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by efurture on 2017/8/16.
 */
public class Tson {
    /**
     * tson data type
     * */
    private static final byte NULL_TYPE = '0';

    private static final byte STRING_TYPE = 's';

    private static final byte BOOLEAN_TYPE_TRUE = 't';

    private static final byte BOOLEAN_TYPE_FALSE = 'f';

    private static final byte NUMBER_INT_TYPE = 'i';

    private static final byte NUMBER_DOUBLE_TYPE = 'd';

    private static final byte ARRAY_TYPE = '[';

    private static final byte MAP_TYPE = '{';

    private static final String STRING_UTF8_CHARSET_NAME = "UTF-8";

    /**
     * parse tson data  to object
     * @param  data  byte array
     * */
    public static Object parse(byte[] data){
        if(data == null){
            return  null;
        }
        Parser parser =  new Parser(data);
        Object object = parser.parse();
        parser.close();
        return object;
    }

    /**
     * serialize object to tson data
     * */
    public static byte[] toTson(Object object){
        if(object == null){
            return  null;
        }
        Builder builder = new Builder();
        byte[]  bts  = builder.toTson(object);
        builder.close();
        return bts;
    }


    /**
     * tson data parser
     * */
    private static final class Parser {

        private int position = 0;
        private byte[] buffer;
        /**
         * identifer cache for tson
         * */
        private StringCache[] stringCache;

        public Parser(byte[] buffer) {
            this.buffer = buffer;
            stringCache = localStringBytesCache.get();
            if(stringCache != null){
                localStringBytesCache.set(null);
            }else{
                stringCache = new StringCache[128];
            }
        }


        public  final Object parse(){
            return  readObject();
        }

        public final void close(){
            position = 0;
            buffer = null;
            if(stringCache != null){
                localStringBytesCache.set(stringCache);
            }
            stringCache = null;
        }

        private final Object readObject(){
            byte type  = readType();
            switch (type){
                case STRING_TYPE:
                    return  readString();
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
                    break;
            }
            return  null;
        }

        private final Object readMap(){
            int size = readUInt();
            Map<String, Object> object = createMap();
            for(int i=0; i<size; i++){
                String key = readMapKey();
                Object value = readObject();
                object.put(key, value);
            }
            return object;
        }

        private final Object readArray(){
            int length = readUInt();
            List<Object> array = createArray(length);
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

        /**
         * most of json object, has repeat property key,
         * property cache, reduce string object.
         * */
        private final String readMapKey() {
            int length = readUInt();
            String  string;
            try {
                int hash = hash(buffer, position, length);
                int globalIndex = (globalStringBytesCache.length - 1)&hash;
                StringCache cache = globalStringBytesCache[globalIndex];
                if(cache != null &&  bytesEquals(buffer, position, length, cache.bts)){
                    position += length;
                    return cache.key;
                }
                boolean globalEmpty = (cache == null);
                int localIndex = (stringCache.length - 1)&hash;
                cache = stringCache[localIndex];
                if(cache != null &&  bytesEquals(buffer, position, length, cache.bts)){
                    position += length;
                    return cache.key;
                }
                string = new String(buffer, position, length, STRING_UTF8_CHARSET_NAME);
                if(length > 0
                        &&  length <= 32
                        && Character.isJavaIdentifierPart(string.charAt(0))){
                    if(globalEmpty){
                        cache = new StringCache();
                        cache.key = string;
                        cache.bts = Arrays.copyOfRange(buffer, position, position + length);
                        globalStringBytesCache[globalIndex] = cache;
                    }else{
                        if(cache == null){
                            cache = new StringCache();
                        }
                        cache.key = string;
                        cache.bts = Arrays.copyOfRange(buffer, position, position + length);
                        stringCache[localIndex] = cache;
                    }
                }
            } catch (UnsupportedEncodingException e) {
                string = new String(buffer, position, length);
            }

            position += length;
            return  string;
        }

        private final String readString(){
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


        private  final boolean readBoolean(){
            byte bt = buffer[position];
            position++;
            return  bt != 0;
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

        private final Map<String,Object> createMap(){
            return new HashMap<>();
        }

        private final List<Object> createArray(int length){
            return new ArrayList<>(length);
        }

    }

    /**
     * tson builder
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


        private final byte[] toTson(Object object){
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
            if(object instanceof  String){
                ensureCapacity(2);
                writeByte(STRING_TYPE);
                writeString(object.toString());
                return;
            }else if (object instanceof Map){
                if(refs.contains(object)){
                    ensureCapacity(2);
                    writeByte(NULL_TYPE);
                    return;
                }
                refs.add(object);
                Map map = (Map) object;
                ensureCapacity(8);
                writeByte(MAP_TYPE);
                writeUInt(map.size());
                Set<Map.Entry<Object,Object>>  entries = map.entrySet();
                for(Map.Entry<Object,Object> entry : entries){
                    writeKey(entry.getKey().toString());
                    writeObject(entry.getValue());
                }
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
                ensureCapacity(12);
                Number number = (Number) object;
                if(object instanceof  Integer || object instanceof  Short){
                    writeByte(NUMBER_INT_TYPE);
                    writeVarInt(number.intValue());
                }else{
                    writeByte(NUMBER_DOUBLE_TYPE);
                    writeDouble(number.doubleValue());
                }
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
                    writeObject(toMap(object));
                    refs.remove(refs.size()-1);
                }
                return;
            }
        }


        private final void writeByte(byte type){
            buffer[position] = type;
            position++;
        }

        private  final Map  toMap(Object object){
            Map map = new HashMap<>();
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
                Field[] fields = getBeanFields(key, targetClass);
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

        private  final void writeKey(String value){
            if(value.length() == 0){
                ensureCapacity(2);
                writeUInt(0);
                return;
            }
            int  index = value.hashCode() & (globalStringBytesCache.length - 1);
            StringCache cache  = globalStringBytesCache[index];
            byte[] bts = null;
            if(cache != null && value.equals(cache.key)){
                bts = cache.bts;
            }
            if(bts == null){
                try {
                    bts = value.getBytes(STRING_UTF8_CHARSET_NAME);
                } catch (UnsupportedEncodingException e) {
                    bts = value.getBytes();
                }
                if(cache == null
                        && Character.isJavaIdentifierPart(value.charAt(0))
                        && bts.length <= 32){
                    cache = new StringCache();
                    cache.key = value;
                    cache.bts = bts;
                    globalStringBytesCache[index] = cache;
                }
            }
            ensureCapacity(bts.length + 8);
            writeUInt(bts.length);
            if(bts.length > 0) {
                writeBytes(bts);
            }
        }
        private  final void writeString(String value){
            byte[] bts = null;
            try {
                bts = value.getBytes(STRING_UTF8_CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                bts = value.getBytes();
            }
            ensureCapacity(bts.length + 8);
            writeUInt(bts.length);
            if(bts.length > 0) {
                writeBytes(bts);
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

        private final void  writeBytes(byte[] bts){
            System.arraycopy(bts, 0, buffer, position, bts.length);
            position += bts.length;
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

    private static final String METHOD_PREFIX_GET = "get";
    private static final String METHOD_PREFIX_IS = "is";
    private static Tson.LruCache<String, List<Method>> methodsCache = new Tson.LruCache<>(128);
    private static Tson.LruCache<String, Field[]> fieldsCache = new Tson.LruCache<>(128);


    private static List<Method> getBeanMethod(String key, Class targetClass){
        List<Method> methods = methodsCache.get(key);
        if(methods == null){
            methods = new ArrayList<>();
            Method[]  allMethods = targetClass.getMethods();
            for(Method method : allMethods){
                if(method.getDeclaringClass() == Object.class){
                    continue;
                }
                String methodName = method.getName();
                if(methodName.startsWith(METHOD_PREFIX_GET)
                        || methodName.startsWith(METHOD_PREFIX_IS)) {
                    methods.add(method);
                }
            }
            methodsCache.put(key, methods);
        }
        return methods;
    }

    private static  Field[] getBeanFields(String key, Class targetClass){
        Field[] fields = fieldsCache.get(key);
        if(fields == null) {
            fields = targetClass.getFields();
            fieldsCache.put(key, fields);
        }
        return  fields;
    }


    /**
     * cache json property key, most of them all same
     * */
    private static final ThreadLocal<StringCache[]> localStringBytesCache = new ThreadLocal<>();
    private static final StringCache[] globalStringBytesCache = new StringCache[1024*2];
    private static final  class StringCache {
        String key;
        byte[] bts;
    }


    private static final int hash(byte[] bts, int offset, int len){
        int h = 0;
        int end = offset + len;
        for (int i=offset; i<end; i++) {
            h = 31 * h + bts[i];
        }
        return h;
    }

    private static final boolean bytesEquals(byte[] buffer, int offset, int len,
                                             byte[] bts){
        if(len != bts.length){
            return  false;
        }
        for(byte bt : bts){
            if(bt != buffer[offset]){
                return  false;
            }
            offset ++;
        }
        return  true;
    }
}
