package com.furture.wson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.efurture.wson.Wson;
import junit.framework.TestCase;

/**
 * Created by 剑白(jianbai.gbj) on 2017/11/27.
 */
public class DoubleTest extends TestCase {


    public void  testDouble(){
       JSONArray array =  JSON.parseArray("[\n" +
               "    {\n" +
               "        \"args\": [\n" +
               "            \"55\", \n" +
               "            \"touchstart\", \n" +
               "            {\n" +
               "                \"changedTouches\": [\n" +
               "                    {\n" +
               "                        \"identifier\": 0, \n" +
               "                        \"pageX\": 442.0943, \n" +
               "                        \"pageY\": 137.80086, \n" +
               "                        \"screenX\": 441.66666, \n" +
               "                        \"screenY\": 947.9167\n" +
               "                    }\n" +
               "                ]\n" +
               "            }, \n" +
               "            null\n" +
               "        ], \n" +
               "        \"method\": \"fireEvent\"\n" +
               "    }\n" +
               "]");
       byte[] bts =  Wson.toWson(array);
       Object arrayBack = Wson.parse(bts);
        System.out.println(JSON.toJSONString(arrayBack));

        Float number = 1065.625f;

        float f = 352.63522f;
        Float fObject = f;


        double d = fObject.doubleValue();

        System.out.println(number.doubleValue()
        + "  " + fObject.doubleValue()
         + "  " + d  + "  "
         +  Double.parseDouble(fObject.toString())


        + " " + Float.floatToIntBits(352.635f));
    }
}
