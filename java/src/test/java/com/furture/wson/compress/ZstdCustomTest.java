package com.furture.wson.compress;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gubaojian.wson.io.Output;
import com.github.luben.zstd.Zstd;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 一定要开启
 * JSONWriter.Feature.WriteByteArrayAsBase64
 * 不然，byte数组，在fastjson内部会被转换为int数组，导致空间增大。
 * 自定义格式效率最，都可以不用压缩，json和jsonb没压缩前差距比较大，压缩后差别不大。
 * <p>
 * data json length 1559
 * data bin length 1191
 * data cbin length 1027
 * <p>
 * data zjson length 1238
 * data zbin length 1201
 * data zcbin length 1037
 */
public class ZstdCustomTest {


    public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
        byte[] data = RandomUtils.nextBytes(1024);
        Map<String, Object> json = new HashMap<>();
        json.put("action", "bmsg");
        json.put("msg", data);
        json.put("authId", UUID.randomUUID().toString());
        json.put("authToken", UUID.randomUUID().toString());
        json.put("appId", RandomUtils.nextInt());
        json.put("hwssId", UUID.randomUUID().toString());

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("json" + objectMapper.writeValueAsString(json));
        String str = JSON.toJSONString(json, JSONWriter.Feature.WriteByteArrayAsBase64);
        byte [] bin = JSONB.toBytes(json);
        Output output = new Output();
        output.writeByte((byte) 2);
        output.writeVarInt(1024);
        output.writeBytes(data);
        byte [] cbin = output.toBytes();
        byte[] zjson = Zstd.compress(str.getBytes(StandardCharsets.UTF_8));
        byte[] zbin = Zstd.compress(bin);
        byte[] zcbin = Zstd.compress(cbin);

        System.out.println(new String(bin));

        /**
         * data json length 1559
         * data bin length 1191
         * data cbin length 1027
         * data zjson length 1240
         * data zbin length 1201
         * data zcbin length 1037
         * */
        System.out.println("data json length " + str.getBytes(StandardCharsets.UTF_8).length);
        System.out.println("data bin length " + bin.length);
        System.out.println("data cbin length " + cbin.length);
        System.out.println("data zjson length " + zjson.length);
        System.out.println("data zbin length " + zbin.length);
        System.out.println("data zcbin length " + zcbin.length);
    }
}
