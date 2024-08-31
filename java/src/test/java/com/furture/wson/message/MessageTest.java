package com.furture.wson.message;

import com.alibaba.fastjson2.JSON;
import com.github.gubaojian.wson.Wson;
import com.furture.wson.util.CHStringUtil;
import junit.framework.TestCase;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MessageTest extends TestCase {

    @Test
    public void testSize() throws IOException {
        Messages messages = genMessage();
        byte[] bts = Wson.toWson(messages);
        System.out.println(bts.length);
        saveFile("file.pson", bts);
        saveFile("file.json", JSON.toJSONBytes(messages));
    }

    @Test
    public void testSize2() throws IOException {
        byte[] source = readFileBytes("/home.json");
        Object message = JSON.parse(source);
        long start = System.currentTimeMillis();
        byte[] bts = Wson.toWson(message);
        System.out.println((System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        JSON.toJSONBytes(message);
        System.out.println((System.currentTimeMillis() - start));
        System.out.println(bts.length);
        saveFile("message.pson", bts);
        saveFile("message.json", JSON.toJSONBytes(message));
    }

    public  static  Messages genMessage(){
        Messages messages = new Messages();
        messages.id = System.currentTimeMillis();
        messages.uuid = UUID.randomUUID().toString();
        messages.owner = UUID.randomUUID().toString();
        messages.preMessages = UUID.randomUUID().toString();
        messages.nextMessages = UUID.randomUUID().toString();


        ArrayList<MessageDO> list = new ArrayList<>();
        int num = 100;
        for(int i=0; i<num; i++){
            MessageDO message = new MessageDO();
            message.id = RandomUtils.nextLong(0, Long.MAX_VALUE);
            message.time = System.currentTimeMillis();
            int len = RandomUtils.nextInt(10, 50);
            message.message = CHStringUtil.randomString(len);
            list.add(message);
        }
        messages.messages = list;
        return  messages;
    }


    static public class Messages {
        long id;
        String uuid;
        String owner;

        String preMessages;
        String nextMessages;



        ArrayList<MessageDO> messages;

        long time;









        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getNextMessages() {
            return nextMessages;
        }

        public void setNextMessages(String nextMessages) {
            this.nextMessages = nextMessages;
        }

        public String getPreMessages() {
            return preMessages;
        }

        public void setPreMessages(String preMessages) {
            this.preMessages = preMessages;
        }

        public ArrayList<MessageDO> getMessages() {
            return messages;
        }

        public void setMessages(ArrayList<MessageDO> messages) {
            this.messages = messages;
        }
    }


    static public class MessageDO {
        long id;
        long time;
        String message;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


    }


    static public class Person {
        long id;
        String name;
    }

    private void saveFile(String file, byte[] bts) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bts);
        outputStream.close();
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
}
