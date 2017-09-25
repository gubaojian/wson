package com.furture.tson;

import com.alibaba.fastjson.JSON;
import com.efurture.tson.Tson;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import junit.framework.TestCase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/15.
 */
public class PrepareTsonFile extends TestCase {



    /**
     * prepare tson file
     * */
    public void testPrepareTson() throws IOException {
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
                "/weex5.json"};
        for(String file : files) {
            convertToTson(file);
        }
    }



    public void  convertToTson(String jsonFile) throws IOException {
        String data = readFile(jsonFile);
        Object map = JSON.parse(data);
        byte[] tson = Tson.toTson(map);
        String tsonFile = "src/test/resources/" + (jsonFile.substring(1, jsonFile.indexOf('.')) + ".tson");
        saveFile(tsonFile, tson);
    }

    private String readFile(String file) throws IOException {
        ByteOutputStream outputStream = new ByteOutputStream(1024);
        InputStream inputStream = this.getClass().getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.getBytes());
    }


    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();
    }
}
