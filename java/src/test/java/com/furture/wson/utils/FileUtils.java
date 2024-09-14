package com.furture.wson.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static String readFile(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = FileUtils.class.getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  new String(outputStream.toByteArray());
    }

    public static byte[] readFileBytes(String file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        InputStream inputStream = FileUtils.class.getResourceAsStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) >=  0){
            outputStream.write(buffer, 0, length);
        }
        return  outputStream.toByteArray();
    }
}
