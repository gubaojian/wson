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
import com.efurture.wson.Wson;

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
}
