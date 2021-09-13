package com.furture.wson;

import com.alibaba.fastjson.JSON;
import com.github.gubaojian.pson.wson.Wson;
import com.furture.wson.domain.Node;
import com.furture.wson.domain.User;
import com.furture.wson.util.LruCache;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/3.
 */
public class ObjectToMapTest extends TestCase {

    public void  testObjectToMap() throws InvocationTargetException, IllegalAccessException {
        User user = new User();
        user.name = "中国";
        user.country = "中国";
        user.next = new User();
        user.next.name = "Next中国";
        Map map = toMap(user);
        System.out.println(map);
        System.out.println(JSON.toJSON(user));

        System.out.println(JSON.toJSONString(user));
        System.out.println(JSON.toJSONString(user).getBytes().length);

        System.out.println(new String(Wson.toWson(user)));

        System.out.println(Wson.toWson(user).length);
    }


    public void  testRecursice(){
        Node node = new Node();
        node.name = "测试";
        node.next = node;
        System.out.println(JSON.toJSONString(node));
        System.out.println(new String(Wson.toWson(node)));
    }


    private Object mapToObject(Map map, Object targetClass){
        return  null;
    }


    private Map toMap(Object object) throws InvocationTargetException, IllegalAccessException {
        Map map = new HashMap<>();
        Class<?> targetClass = object.getClass();
        String key = targetClass.getName();
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
        return  map;
    }

    private List<Method> getMethods(Class targetClass){
        List<Method> methods = new ArrayList<>();
        while (targetClass != Object.class){
            Method[] declaredMethods = targetClass.getDeclaredMethods();
            for(Method declaredMethod : declaredMethods){
                String methodName = declaredMethod.getName();
                if(methodName.startsWith(METHOD_PREFIX_GET)
                        || methodName.startsWith(METHOD_PREFIX_IS)) {
                    if(Modifier.isPublic(declaredMethod.getModifiers())) {
                        methods.add(declaredMethod);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return methods;
    }

    private static final String METHOD_PREFIX_GET = "get";
    private static final String METHOD_PREFIX_IS = "is";
    private static LruCache<String, List<Method>> methodsCache = new LruCache<>(32);
    private static LruCache<String, Field[]> fieldsCache = new LruCache<>(32);


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

}
