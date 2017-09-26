package com.efurture.wson;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by efurture on 2017/8/16.
 */
public class Wson {
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

    private static final String STRING_UTF8_CHARSET_NAME = "UTF-8";

    /**
     * parse wson data  to object
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
        /**
         * identifer cache for wson
         * */
        private StringCache[] stringCache;

        public Parser(byte[] buffer) {
            this.buffer = buffer;
            stringCache = localStringBytesCache.get();
            if(stringCache != null){
                localStringBytesCache.set(null);
            }else{
                stringCache = new StringCache[LOCAL_STRING_CACHE_SIZE];
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
            Map<String, Object> object = WsonAdapter.createMap();
            for(int i=0; i<size; i++){
                String key = readMapKey();
                Object value = readObject();
                object.put(key, value);
            }
            return object;
        }

        private final Object readArray(){
            int length = readUInt();
            List<Object> array = WsonAdapter.createArray(length);
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
                int localIndex = (stringCache.length - 1)&hash;
                cache = stringCache[localIndex];
                if(cache != null &&  bytesEquals(buffer, position, length, cache.bts)){
                    position += length;
                    return cache.key;
                }
                string = new String(buffer, position, length, STRING_UTF8_CHARSET_NAME);
                if(length > 0
                        &&  length <= CACHE_STRING_MAX_LENGTH
                        && Character.isJavaIdentifierPart(string.charAt(0))){
                    cache = new StringCache();
                    cache.key = string;
                    cache.bts = Arrays.copyOfRange(buffer, position, position + length);
                    if(globalStringBytesCache[globalIndex] == null){
                        globalStringBytesCache[globalIndex] = cache;
                    }else{
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
        private StringCache[] stringCache;
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
            if(stringCache != null){
                localStringBytesCache.set(null);
            }else{
                stringCache = new StringCache[LOCAL_STRING_CACHE_SIZE];
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
            if(stringCache != null){
                localStringBytesCache.set(stringCache);
            }
            stringCache = null;
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
                    writeMapKey(entry.getKey().toString());
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
            return WsonAdapter.toMap(object);
        }

        private  final void writeMapKey(String value){
            if(value.length() == 0){
                ensureCapacity(2);
                writeUInt(0);
                return;
            }
            int hash = value.hashCode();
            int  globalIndex = hash & (globalStringBytesCache.length - 1);
            StringCache cache  = globalStringBytesCache[globalIndex];
            byte[] bts = null;
            if(cache != null && value.equals(cache.key)){
                bts = cache.bts;
            }
            if(bts == null){
                int localIndex = (stringCache.length - 1)&hash;
                cache = stringCache[localIndex];
                if(cache != null &&  value.equals(cache.key)){
                     bts = cache.bts;
                }
                if(bts == null){
                    try {
                        bts = value.getBytes(STRING_UTF8_CHARSET_NAME);
                    } catch (UnsupportedEncodingException e) {
                        bts = value.getBytes();
                    }
                    if(bts.length > 0
                            && Character.isJavaIdentifierPart(value.charAt(0))
                            && bts.length <= CACHE_STRING_MAX_LENGTH){
                        cache = new StringCache();
                        cache.key = value;
                        cache.bts = bts;
                        if(globalStringBytesCache[globalIndex] == null) {
                            globalStringBytesCache[globalIndex] = cache;
                        }else{
                            stringCache[localIndex] = cache;
                        }
                    }
                }
            }
            ensureCapacity(bts.length + 8);
            writeUInt(bts.length);
            if(bts.length > 0) {
                writeBytes(bts);
            }
        }
        private  final void writeString(String value){
            int utf8Length = value.length();
            int i = 0;
            // This loop optimizes for pure ASCII.
            while (i < utf8Length && value.charAt(i) < 0x80) {
                i++;
            }
            if(i == utf8Length){
                i = 0;
                ensureCapacity(utf8Length + 8);
                writeUInt(utf8Length);
                while (i < utf8Length){
                    buffer[position + i] = (byte)value.charAt(i);
                    i++;
                }
                this.position += utf8Length;
                return;
            }

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
            bts = null;
        }

        final  int encodeUtf8(CharSequence in, byte[] out, int offset, int length) {
            int utf16Length = in.length();
            int j = offset;
            int i = 0;
            int limit = offset + length;
            // Designed to take advantage of
            // https://wikis.oracle.com/display/HotSpotInternals/RangeCheckElimination
            for (char c; i < utf16Length && i + j < limit && (c = in.charAt(i)) < 0x80; i++) {
                out[j + i] = (byte) c;
            }
            if (i == utf16Length) {
                return j + utf16Length;
            }
            j += i;
            for (char c; i < utf16Length; i++) {
                c = in.charAt(i);
                if (c < 0x80 && j < limit) {
                    out[j++] = (byte) c;
                } else if (c < 0x800 && j <= limit - 2) { // 11 bits, two UTF-8 bytes
                    out[j++] = (byte) ((0xF << 6) | (c >>> 6));
                    out[j++] = (byte) (0x80 | (0x3F & c));
                } else if ((c < Character.MIN_SURROGATE || Character.MAX_SURROGATE < c) && j <= limit - 3) {
                    // Maximum single-char code point is 0xFFFF, 16 bits, three UTF-8 bytes
                    out[j++] = (byte) ((0xF << 5) | (c >>> 12));
                    out[j++] = (byte) (0x80 | (0x3F & (c >>> 6)));
                    out[j++] = (byte) (0x80 | (0x3F & c));
                } else if (j <= limit - 4) {
                    // Minimum code point represented by a surrogate pair is 0x10000, 17 bits,
                    // four UTF-8 bytes
                    final char low;
                    if (i + 1 == in.length()
                            || !Character.isSurrogatePair(c, (low = in.charAt(++i)))) {
                        throw new IllegalArgumentException("error utf-8 byte format");
                    }
                    int codePoint = Character.toCodePoint(c, low);
                    out[j++] = (byte) ((0xF << 4) | (codePoint >>> 18));
                    out[j++] = (byte) (0x80 | (0x3F & (codePoint >>> 12)));
                    out[j++] = (byte) (0x80 | (0x3F & (codePoint >>> 6)));
                    out[j++] = (byte) (0x80 | (0x3F & codePoint));
                } else {
                    // If we are surrogates and we're not a surrogate pair, always throw an
                    // UnpairedSurrogateException instead of an ArrayOutOfBoundsException.
                    if ((Character.MIN_SURROGATE <= c && c <= Character.MAX_SURROGATE)
                            && (i + 1 == in.length()
                            || !Character.isSurrogatePair(c, in.charAt(i + 1)))) {
                        throw new IllegalArgumentException("error utf-8 byte format");
                    }
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + c + " at index " + j);
                }
            }
            return j;
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


    private static final int LOCAL_STRING_CACHE_SIZE = 256;
    private static final int GLOBAL_STRING_CACHE_SIZE = 4*1024;
    private static final int CACHE_STRING_MAX_LENGTH = 32;
    /**
     * cache json property key, most of them all same
     * */
    private static final ThreadLocal<StringCache[]> localStringBytesCache = new ThreadLocal<>();
    private static final StringCache[] globalStringBytesCache = new StringCache[GLOBAL_STRING_CACHE_SIZE];
    private static final  class StringCache {
        String key;
        byte[] bts;
    }


    /**
     * keep same with string hash
     * */
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
