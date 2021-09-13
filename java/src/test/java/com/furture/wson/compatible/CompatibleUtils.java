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
package com.furture.wson.compatible;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.gubaojian.pson.wson.Wson;

import java.util.Set;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/30.
 */
public class CompatibleUtils {


    public static void checkDiff(Object object){
        Object json = JSON.parse(JSON.toJSONString(object));
        Object wson = Wson.parse(Wson.toWson(object));
        checkSame(json, wson);
    }

    private static void checkSame(Object src, Object dest){
        if(src instanceof JSONArray){
            if(dest instanceof JSONArray){
                if(((JSONArray) src).size() != ((JSONArray) dest).size()){
                    throw new RuntimeException(src  + " " +  dest + "Not same");
                }
                for(int i=0; i<((JSONArray) src).size(); i++){
                    checkSame(((JSONArray) src).get(i), ((JSONArray) dest).get(i));
                }
            }else{
                throw new RuntimeException(src  + " " +  dest + " not same JSONArray");
            }
            return;
        }else if(src instanceof JSONObject){
            if(dest instanceof JSONObject){
                if(((JSONObject) src).size() != ((JSONObject) dest).size()){
                    throw new RuntimeException(src  + " " +  dest + " not same JSONObject Size "
                    + ((JSONObject) dest).size()  + "  "+ ((JSONObject) src).size()
                    + " key " + ((JSONObject) src).keySet()
                    +  "  " + ((JSONObject) dest).keySet());
                }
                Set<String> keys = ((JSONObject) src).keySet();
                for(String key : keys){
                    checkSame(((JSONObject) src).get(key), ((JSONObject) dest).get(key));
                }
            }else{
                throw new RuntimeException(src  + " " +  dest + " not same JSONObject");
            }
            return;
        }
        if(src == null && dest == null){
            return;
        }
        if(src.equals(dest)){
            return;
        }
        if(dest instanceof  Double){
            if(((Double) dest).doubleValue() == ((Number)src).doubleValue()){
                return;
            }
        }
        if(dest instanceof  Float){
            if(((Float) dest).doubleValue() == ((Number)src).floatValue()){
                return;
            }
        }
        throw new RuntimeException(src  + " " +  dest + " not same Object");
    }



    public static void  benchParse(String data, byte[] bts){
        int count = 1000;
        benchWsonParse(bts, count);
        benchJSONParse(data, count);

        benchJSONParse(data, count);
        benchWsonParse(bts, count);


        benchWsonParse(bts, count);
        benchJSONParse(data, count);
    }

    private static void benchWsonParse(byte[] bts, int count){
        Wson.parse(bts);
        long start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            Wson.parse(bts);
        }
        long end = System.currentTimeMillis();
        System.out.println("WSON parse used " + (end - start));
    }

    private static void benchJSONParse(String data, int count){
        JSON.parse(data);
        long start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            JSON.parse(data);
        }
        long end = System.currentTimeMillis();
        System.out.println("FastJSON parse used " + (end - start));
    }


    public static void  benchSerialize(Object object){
        int count  = 1000;
        benchWsonSerialize(object, count);
        benchJsonSerialize(object, count);

        benchWsonSerialize(object, count);
        benchJsonSerialize(object, count);

        benchWsonSerialize(object, count);
        benchJsonSerialize(object, count);
    }

    private static void benchWsonSerialize(Object map, int count){
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            Wson.toWson(map);
        }
        long end = System.currentTimeMillis();
        System.out.println("WSON serialize used " + (end - start));
    }

    private static void benchJsonSerialize(Object map, int count){
        long start = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            JSON.toJSONString(map);
        }
        long end = System.currentTimeMillis();
        System.out.println("FASTJSON serialize used " + (end - start));

    }


    public static  void  testMediaBench(){
        String media = "{\n" +
                "  \"media\": {\n" +
                "    \"uri\": \"g\",\n" +
                "    \"title\": \"J\",\n" +
                "    \"width\": 640,\n" +
                "    \"height\": 480,\n" +
                "    \"format\": \"v\",\n" +
                "    \"duration\": 18000000,\n" +
                "    \"size\": 58982400,\n" +
                "    \"bitrate\": 262144,\n" +
                "    \"persons\": [\n" +
                "      \"B\",\n" +
                "      \"S\"\n" +
                "    ],\n" +
                "    \"player\": \"JAVA\",\n" +
                "    \"copyright\": null\n" +
                "  },\n" +
                "  \"images\": [\n" +
                "    {\n" +
                "      \"uri\": \"h\",\n" +
                "      \"title\": \"J\",\n" +
                "      \"width\": 1024,\n" +
                "      \"height\": 768,\n" +
                "      \"size\": \"LARGE\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"uri\": \"h\",\n" +
                "      \"title\": \"J\",\n" +
                "      \"width\": 320,\n" +
                "      \"height\": 240,\n" +
                "      \"size\": \"SMALL\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        Object object = JSON.parse(media);
        byte[] bts = Wson.toWson(object);
        CompatibleUtils.benchParse(JSON.toJSONString(object), bts);
        CompatibleUtils.benchSerialize(object);
    }

}
