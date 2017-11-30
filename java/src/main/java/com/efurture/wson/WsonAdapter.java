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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * adapter for different environment, adapter for fastjson
 * Created by 剑白(jianbai.gbj) on 2017/9/26.
 */
class WsonAdapter {

    static final List createArray(int length){
        return  new JSONArray(length);
    }

    static final Map createMap(){
        return  new JSONObject();
    }

    /**
     * convert object to map, only first layer is convert to map.
     * */
    static Map toMap(Object object){
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
            throw  new RuntimeException(e);
        }
        return  map;
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
        protected boolean removeEldestEntry(Entry eldest) {
            return size() > cacheSize;
        }
    }

    private static final String METHOD_PREFIX_GET = "get";
    private static final String METHOD_PREFIX_IS = "is";
    private static LruCache<String, List<Method>> methodsCache = new LruCache<>(128);
    private static LruCache<String, List<Field>> fieldsCache = new LruCache<>(128);



    private static List<Method> getBeanMethod(String key, Class targetClass){
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
                    methods.add(method);
                }
            }
            methodsCache.put(key, methods);
        }
        return methods;
    }



    private static  List<Field> getBeanFields(String key, Class targetClass){
        List<Field> fieldList = fieldsCache.get(key);
        if(fieldList == null) {
            Field[] fields = targetClass.getFields();
            fieldList = new ArrayList<>(fields.length);
            for(Field field : fields){
                if((field.getModifiers() & Modifier.STATIC) != 0){
                    continue;
                }
                fieldList.add(field);
            }
            fieldsCache.put(key, fieldList);
        }
        return  fieldList;
    }

}
