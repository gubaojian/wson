package com.furture.wson.compress;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONB;
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



        String str = JSON.toJSONString(json);
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
         * 3764
         * 1045
         * 1648
         * 1055
         * */
        System.out.println(str.getBytes(StandardCharsets.UTF_8).length);
        System.out.println(bin.length);
        System.out.println(cbin.length);
        System.out.println(zjson.length);
        System.out.println(zbin.length);
        System.out.println(zcbin.length);
    }
}
