package com.bytecode;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GetterInvokerTest {

    public  static  void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        byte[] bts = FileUtil.readBytes("/Users/efurture/code/wson/java/build/classes/java/test/com/bytecode/GetterInvoker.class");
        byte[] bts2 = FileUtil.readBytes("/Users/efurture/code/wson/java/build/classes/java/test/com/bytecode/GetterInvoker10.class");

        Map<String, byte[]> extraClassDefs = new HashMap<>();
        ByteClassLoader classLoader = new ByteClassLoader(GetterInvokerTest.class.getClassLoader(), extraClassDefs);

        classLoader.loadClass("com.bytecode.GetterInvoker");
        System.out.println(new String(bts));
        System.out.println(new String(bts2));
        System.out.println( ArrayUtils.toString(bts));
        System.out.println( ArrayUtils.toString(bts2));
        String generateClassName = "com.bytecode.GetterInvoker100";
        byte[] dbytes = binaryReplace(bts, "com/bytecode/GetterInvoker", generateClassName.replace('.', '/'));
        System.out.println("dbytes");
        System.out.println( new String(dbytes));
        System.out.println( ArrayUtils.toString(dbytes));

        System.out.println("start field class object replace");
        dbytes = binaryReplace(dbytes, "com/bytecode/Getter", "com/bytecode/User");
        System.out.println("dbytes replace target");
        System.out.println( new String(dbytes));
        System.out.println( ArrayUtils.toString(dbytes));

        System.out.println("start field name object replace with sign应该带上签名一起替换。");
        dbytes = binaryReplace(dbytes, "getName", "getNameUser");
        System.out.println("dbytes replace target");
        System.out.println( new String(dbytes));
        System.out.println( ArrayUtils.toString(dbytes));




        extraClassDefs.put(generateClassName, dbytes);
        FileUtil.writeBytes(dbytes, "GetterInvoker10.class");

        System.out.println(Arrays.equals(bts2, dbytes));

        Class<?> generateClass = classLoader.loadClass(generateClassName);
        Field field = generateClass.getField("getter");
        field.setAccessible(true);
        Invoker invoker = (Invoker) generateClass.newInstance();
        //field.set(invoker, new Getter());
        User user = new User();
        field.set(invoker, user);
        System.out.println(invoker.invokeGetFast());

        long start = System.currentTimeMillis();
        long end = System.currentTimeMillis();

        for(int i=0; i<10000*100; i++) {
            user.getNameUser();
        }

        start = System.currentTimeMillis();
        for(int i=0; i<10000*100; i++) {
            user.getNameUser();
        }
        end = System.currentTimeMillis();
        System.out.println("direct call used " + (end - start));

        start = System.currentTimeMillis();
        for(int i=0; i<10000*100; i++) {
            invoker.invokeGetFast();
        }
        end = System.currentTimeMillis();
        System.out.println("invokeGetFast used " + (end - start));

        Method method = user.getClass().getMethod("getNameUser");
        method.setAccessible(true);

        start = System.currentTimeMillis();
        for(int i=0; i<10000*100; i++) {
            method.invoke(user);
        }
        end = System.currentTimeMillis();
        System.out.println("reflect used " + (end - start));


    }



    public static byte[]  binaryReplace(byte[] bts, String name, String replace) {
       byte[] dest = new byte[bts.length*2];
       byte[] target = name.getBytes(StandardCharsets.UTF_8);
        byte[] rbytes = replace.getBytes(StandardCharsets.UTF_8);
       int index = 0;
       int start = 0;
       int offset = 0;
       do {
           if (showLog) {
               System.out.println("search from start " + start);
           }
           offset = binaryFind(bts, target, start);
           if (offset > 0) {
               if (showLog) {
                   System.out.println("search from find offset " + offset);
               }
               int copyLen = offset - start;
               boolean validMatch = false;
               if (bts[offset - 1] == target.length) {
                   if (showLog) {
                       System.out.println("match case with className " + new String(bts, offset-2, copyLen + 4));
                   }
                   validMatch = true;
               }
               //local variable tab 前面加一个 L 后面加一个分号 ;
               if (bts[offset - 1] == 'L' && bts[offset - 2] == target.length + 2 ) {
                   validMatch = true;
                   if (showLog) {
                       System.out.println("match case with className L" + new String(bts, offset - 2, copyLen + 4));
                   }
               }
               System.arraycopy(bts, start, dest, index, copyLen);
               index += copyLen;
               start = offset + target.length;
               if (!validMatch) {
                   System.arraycopy(target, 0, dest, index, target.length);
                   index += target.length;
                   continue;
               }

               //constructor
               if (dest[index - 1] == target.length) {
                   dest[index - 1] += (rbytes.length - target.length);
               }
               //local variable tab 前面加一个 L 后面加一个分号 ;
               if (dest[index - 1] == 'L' && dest[index - 2] == target.length + 2 ) {
                   dest[index - 2] += (rbytes.length - target.length);
               }
               System.arraycopy(rbytes, 0, dest, index, rbytes.length);
               index += rbytes.length;
           } else {
               int copyLen = bts.length - start;
               System.arraycopy(bts, start, dest, index, copyLen);
               index += copyLen;
           }
       } while (offset >= 0);
       System.out.println(new String(Arrays.copyOf(dest, index)));
       return Arrays.copyOf(dest, index);
    }


    public static int binaryFind(byte[] source,   byte[] target, int from) {
        for(int i=from; i<source.length; i++) {
            if(source[i] == target[0]) {
                boolean find = true;
                for(int j=1; j<target.length; j++) {
                    if (i + j >= source.length) {
                        return -1;
                    }
                    if (source[i + j] != target[j]) {
                        find = false;
                        break;
                    }
                }
                if (find) {
                  return i;
                }
            }
        }
        return -1;
    }

    private  static final boolean showLog = false;
}
