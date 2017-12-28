package com.furture.wson;

import com.alibaba.fastjson.JSON;
import com.efurture.wson.Wson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/15.
 */
public class PrepareWsonFile extends TestCase {



    /**
     * prepare wson file
     * */
    public void testPrepareWson() throws IOException {
        String[] files = {"/data.json",
                "/home.json",
                "/middle.json",
                "/tiny.json",
                "/media.json",
                "/media2.json",
                "/weex.json",
                "/weex2.json",
                "/weex3.json",
                "/weex4.json",
                "/weex5.json",
                "/bug/bigNumber.json",
                "/bug/bugintdouble.json",
                "/data/2.json",
                "/data/epub.json",
                "/data/group.json",
                "/data/int_100.json",
                "/data/int_500.json",
                "/data/int_1000.json",
                "/data/int_10000.json",
                "/data/int_array_100.json",
                "/data/int_array_200.json",
                "/data/int_array_500.json",
                "/data/int_array_1000.json",
                "/data/int_array_10000.json",
                "/data/maiksagill.json",
                "/data/object_f_emptyobj_10000.json",
                "/data/object_f_false_10000.json",
                "/data/object_f_int_1000.json",
                "/data/object_f_int_10000.json",
                "/data/object_f_null_10000.json",
                "/data/object_f_string_10000.json",
                "/data/object_f_true_10000.json",
                "/data/page_model_cached.json",
                "/data/string_array_10000.json",
                "/data/trade.json",
                "/data/monitor.json",
                "/data/Bug_2_Test.json",
                "/data/Bug_0_Test.json",
                "/data/json.json",
                "/data/wuyexiong.json"


        };


        for(String file : files) {
            convertToWson(file);
        }
    }


    public void testShowWsonFileContent() throws IOException {
        byte[] bts = readFileBytes("/data/trade.wson");
        Object object = Wson.parse(bts);
        System.out.println(JSON.toJSONString(object));

    }

    public void  convertToProtobuf(String jsonFile) throws IOException {
        String data = readFile(jsonFile);
        Object map = JSON.parse(data);
        byte[] wson = Wson.toWson(map);
        String wsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".wson");
        saveFile(wsonFile, wson);
    }


    public void  convertToWson(String jsonFile) throws IOException {
       try{
            String data = readFile(jsonFile);
            Object map = JSON.parse(data);
            byte[] wson = Wson.toWson(map);
            Assert.assertEquals(jsonFile + "convert not equals", Wson.parse(wson), JSON.parse(JSON.toJSONString(map)));
            String wsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".wson");
            saveFile(wsonFile, wson);
        }catch (Throwable e){
            System.out.println("fileName " + jsonFile);
            e.printStackTrace();
        }
    }

    private String readFile(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.toByteArray());
    }


    private byte[] readFileBytes(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  outputStream.toByteArray();
    }

    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();
    }
}
