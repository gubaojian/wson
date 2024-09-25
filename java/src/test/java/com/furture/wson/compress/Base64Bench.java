package com.furture.wson.compress;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@State(Scope.Benchmark)
public class Base64Bench {


    byte[] data = new byte[1024];

    @Setup
    public void setUp() throws IOException {
        data = RandomUtils.nextBytes(1024);
    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBase64Fastjson() throws JsonProcessingException {
        JSON.toJSONString(data, JSONWriter.Feature.WriteByteArrayAsBase64);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBase64JDK() throws JsonProcessingException {
        Base64.getEncoder().encodeToString(data);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testBase64JDKByte() throws JsonProcessingException {
        Base64.getEncoder().encode(data);
    }



    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testCommonCodeBase64() throws JsonProcessingException {
        org.apache.commons.codec.binary.Base64.encodeBase64String(data);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testCommonCodeBase64Bytes() throws JsonProcessingException {
        org.apache.commons.codec.binary.Base64.encodeBase64(data);
    }


    @TearDown
    public  void tearDown() {
        //System.out.println("mediaJson length " +  json.size());
    }

    /**
     * Benchmark                               Mode  Cnt       Score        Error  Units
     * Base64Bench.testBase64JDK              thrpt    5  846337.356 ± 168653.162  ops/s
     * Base64Bench.testBase64JDKByte          thrpt    5  919724.191 ±  70030.016  ops/s
     * Base64Bench.testCommonCodeBase64       thrpt    5  148817.700 ±  37893.563  ops/s
     * Base64Bench.testCommonCodeBase64Bytes  thrpt    5  155301.239 ±  11612.358  ops/s
     * Base64Bench.testFastjsonBase64         thrpt    5  797425.127 ± 133254.396  ops/s
     * */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Base64Bench.class.getSimpleName())
                .forks(1)
                //.threads(2)
                .build();

        new Runner(opt).run();
    }
}
