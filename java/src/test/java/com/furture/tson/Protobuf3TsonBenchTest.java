package com.furture.tson;

import com.alibaba.fastjson.JSONObject;
import com.furture.tson.protobuf.MediaOuterClass;
import junit.framework.TestCase;
import java.io.IOException;

/**
 * Created by 剑白(jianbai.gbj) on 2017/9/25.
 */
public class Protobuf3TsonBenchTest extends TestCase {


    public void  testProtobuf() throws IOException {

        MediaOuterClass.MediaImage mediaImage = buildMediaImageProtobuf();
        mediaImage.toByteArray();


        byte[] bts = null;

        long start = System.currentTimeMillis();
        for(int i=0; i<10000; i++) {
            bts  = mediaImage.toByteArray();
        }
        long end = System.currentTimeMillis();
        System.out.println("protobuf length " + bts.length + " used " + (end -start));
    }


    private MediaOuterClass.MediaImage buildMediaImageProtobuf(){
        MediaOuterClass.MediaImage.Media.Builder mediaBuilder = MediaOuterClass.MediaImage.Media.newBuilder();
        mediaBuilder.setUri("g");
        mediaBuilder.setTitle("J");
        mediaBuilder.setWidth(640);
        mediaBuilder.setHeight(480);
        mediaBuilder.setFormat("v");
        mediaBuilder.setDuration(18000000);
        mediaBuilder.setSize(58982400);
        mediaBuilder.setBitrate(262144);
        mediaBuilder.addPersons("B");
        mediaBuilder.addPersons("S");
        mediaBuilder.setPlayer("JAVA");

        MediaOuterClass.MediaImage.Builder mediaImageBuilder = MediaOuterClass.MediaImage.newBuilder();
        mediaImageBuilder.setMedia(mediaBuilder);

        MediaOuterClass.MediaImage.Image.Builder imageOneBuilder = MediaOuterClass.MediaImage.Image.newBuilder();
        imageOneBuilder.setUri("h");
        imageOneBuilder.setTitle("J");
        imageOneBuilder.setWidth(1024);
        imageOneBuilder.setHeight(768);
        imageOneBuilder.setSize("LARGE");


        MediaOuterClass.MediaImage.Image.Builder imageTwoBuilder = MediaOuterClass.MediaImage.Image.newBuilder();
        imageTwoBuilder.setUri("h");
        imageTwoBuilder.setTitle("J");
        imageTwoBuilder.setWidth(320);
        imageTwoBuilder.setHeight(240);
        imageTwoBuilder.setSize("SMALL");

        mediaImageBuilder.addImages(imageOneBuilder);
        mediaImageBuilder.addImages(imageTwoBuilder);

        return mediaImageBuilder.build();
    }
}
