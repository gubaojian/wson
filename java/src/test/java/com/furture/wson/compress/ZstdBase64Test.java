package com.furture.wson.compress;

import cn.hutool.socket.protocol.MsgEncoder;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furture.wson.utils.FileUtils;
import com.github.gubaojian.wson.io.Output;
import com.github.luben.zstd.Zstd;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.jackson.dataformat.MessagePackFactory;
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

/**
 * 一定要开启
 * JSONWriter.Feature.WriteByteArrayAsBase64
 * 不然，byte数组，在fastjson内部会被转换为int数组，导致空间增大。
 *  public void writeBinary(byte[] bytes) {
 *         if (bytes == null) {
 *             writeArrayNull();
 *             return;
 *         }
 *
 *         if ((context.features & WriteByteArrayAsBase64.mask) != 0) {
 *             writeBase64(bytes);
 *             return;
 *         }
 *
 *         startArray();
 *         for (int i = 0; i < bytes.length; i++) {
 *             if (i != 0) {
 *                 writeComma();
 *             }
 *             writeInt32(bytes[i]);
 *         }
 *         endArray();
 *     }
 * json byte array as base64 string
 * JSON 1560
 * jsonb 1191
 * msgpack 1193
 * json zstd 1239
 * jsonb zstd 1201
 * msgpack zstd 1203
 * base64
 * JSON 1560
 * jsonb 1535
 * msgpack 1537
 * json zstd 1239
 * jsonb zstd 1224
 * msgpack zstd 1239
 * 字符串
 * JSON 2468
 * jsonb 2216
 * msgpack 2445
 * json zstd 1995
 * jsonb zstd 1931
 * msgpack zstd 1969
 * json
 * JSON 490
 * jsonb 372
 * msgpack 386
 * json zstd 325
 * jsonb zstd 310
 * msgpack zstd 327
 * json1
 * JSON 560
 * jsonb 467
 * msgpack 469
 * json zstd 350
 * jsonb zstd 334
 * msgpack zstd 342
 * json2
 * JSON 434
 * jsonb 358
 * msgpack 356
 * json zstd 273
 * jsonb zstd 258
 * msgpack zstd 265
 */
public class ZstdBase64Test {

    public static void testData(Object data) throws JsonProcessingException {
        Map<String, Object> json = new HashMap<>();
        json.put("action", "bmsg");
        json.put("msg",  data);
        json.put("authId", UUID.randomUUID().toString());
        json.put("authToken", UUID.randomUUID().toString());
        json.put("appId", RandomUtils.nextInt());
        json.put("hwssId", UUID.randomUUID().toString());

        //https://github.com/alibaba/fastjson2/issues/2066
        // FASTJSON要开启feature，不然会把byte[] 转换成 int array增大空间。
        String str = JSON.toJSONString(json, JSONWriter.Feature.WriteByteArrayAsBase64);
        byte [] bin = JSONB.toBytes(json);

        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] msgPackBytes = objectMapper.writeValueAsBytes(json);


        byte[] zjson = Zstd.compress(str.getBytes(StandardCharsets.UTF_8));
        byte[] zbin = Zstd.compress(bin);
        byte[] zmsgPack = Zstd.compress(msgPackBytes);
        /**
         * 3764
         * 1045
         * 1648
         * 1055
         * */
        System.out.println("JSON " + str.getBytes(StandardCharsets.UTF_8).length);
        System.out.println("jsonb " + bin.length);
        System.out.println("msgpack " + msgPackBytes.length);

        System.out.println("json zstd " + zjson.length);
        System.out.println("jsonb zstd " + zbin.length);
        System.out.println("msgpack zstd " + zmsgPack.length);
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
     * 对于文本：json + 压缩 和 二进制 + 压缩。 未压缩前有区别，但压缩后实际结果区别不大，但json通用性和可读性更好，相差在5%内。
     * json byte array as base64 string
     * JSON 1559
     * jsonb 1191
     * json zstd 1238
     * jsonb zstd 1201
     * base64
     * JSON 1560
     * jsonb 1535
     * json zstd 1239
     * jsonb zstd 1218
     * 字符串
     * JSON 2487
     * jsonb 2216
     * json zstd 2011
     * jsonb zstd 1943
     * json
     * JSON 489
     * jsonb 372
     * json zstd 324
     * jsonb zstd 310
     * json1
     * JSON 560
     * jsonb 467
     * json zstd 351
     * jsonb zstd 336
     * json2
     * JSON 435
     * jsonb 358
     * json zstd 270
     * jsonb zstd 256
     *
     * */
    public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
        System.out.println("json byte array as base64 string");
        byte[] bts = RandomUtils.nextBytes(1024);
        testData(bts);
        System.out.println("base64");
        testData(Base64.encodeBase64String(bts));
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
