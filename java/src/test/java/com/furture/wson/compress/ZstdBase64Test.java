package com.furture.wson.compress;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.furture.wson.utils.FileUtils;
import com.github.gubaojian.wson.io.Output;
import com.github.luben.zstd.Zstd;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ZstdBase64Test {

    public  static void  testData(Object data) {
         Map<String, Object> json = new HashMap<>();
        json.put("action", "bmsg");
        json.put("msg",  data);
        json.put("authId", UUID.randomUUID().toString());
        json.put("authToken", UUID.randomUUID().toString());
        json.put("appId", RandomUtils.nextInt());
        json.put("hwssId", UUID.randomUUID().toString());

        String str = JSON.toJSONString(json);
        byte [] bin = JSONB.toBytes(json);
        byte[] zjson = Zstd.compress(str.getBytes(StandardCharsets.UTF_8));
        byte[] zbin = Zstd.compress(bin);
        /**
         * 3764
         * 1045
         * 1648
         * 1055
         * */
        System.out.println(str.getBytes(StandardCharsets.UTF_8).length);
        System.out.println(bin.length);
        System.out.println(zjson.length);
        System.out.println(zbin.length);
    }

    /**
     * 对于字节流：二进制压缩后，消息可能更大：有时压缩后数据可能更大。
     * 3850
     * 1191
     *
     * 1744
     *
     * 1201
     *
     * 对于文本：json + 压缩 和 二进制 + 压缩。 实际结果区别不大，但json通用性和可读性更好，相差在5%内。
     * 字符串
     * 2474
     * 2216
     *
     * 1994
     * 1925
     *
     * json
     * 490
     * 372
     *
     * 325
     * 312
     *
     * json2
     * 435
     * 358
     *
     * 271
     * 258
     *
     * */
    public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
        System.out.println("二进制");
        testData(RandomUtils.nextBytes(1024));
        System.out.println("字符串");
        testData(RandomStringUtils.random(1024));

        System.out.println("json");
        testData(JSON.parse(FileUtils.readFile("/media.json")));

        System.out.println("json1");
        testData(JSON.parse(FileUtils.readFile("/media.json")).toString());

        System.out.println("json2");
        testData(JSON.parse(FileUtils.readFile("/ele.json")));


    }
}
