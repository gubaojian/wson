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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@State(Scope.Benchmark)
public class FastJSONBase64Bench {


    Map<String, Object> json = new HashMap<>();
    Map<String, Object> strjson = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();


    @Setup
    public void setUp() throws IOException {
        byte[] data = RandomUtils.nextBytes(1024);
        json.put("action", "bmsg");
        json.put("msg", data);
        json.put("authId", UUID.randomUUID().toString());
        json.put("authToken", UUID.randomUUID().toString());
        json.put("appId", RandomUtils.nextInt());
        json.put("hwssId", UUID.randomUUID().toString());

        strjson.put("action", "bmsg");
        strjson.put("msg", RandomStringUtils.randomAlphabetic(512));
        strjson.put("authId", UUID.randomUUID().toString());
        strjson.put("authToken", UUID.randomUUID().toString());
        strjson.put("appId", RandomUtils.nextInt());
        strjson.put("hwssId", UUID.randomUUID().toString());



    }

    /**
     *
     # Run progress: 75.00% complete, ETA 00:01:41
     # Fork: 1 of 1
     # Warmup Iteration   1: 1144671.054 ops/s
     # Warmup Iteration   2: 897807.980 ops/s
     # Warmup Iteration   3: 726300.049 ops/s
     # Warmup Iteration   4: 789688.772 ops/s
     # Warmup Iteration   5: 984238.897 ops/s
     Iteration   1: 1039590.499 ops/s
     Iteration   2: 1155011.707 ops/s
     Iteration   3: 1275512.392 ops/s
     Iteration   4: 1261483.016 ops/s
     Iteration   5: mediaJson length 6
     1119178.172 ops/s


     Result "com.furture.wson.compress.JSONBench.testJacksonMethod":
     1170155.157 ±(99.9%) 381758.050 ops/s [Average]
     (min, avg, max) = (1039590.499, 1170155.157, 1275512.392), stdev = 99141.353
     CI (99.9%): [788397.107, 1551913.208] (assumes normal distribution)
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testJacksonMethod() throws JsonProcessingException {
        objectMapper.writeValueAsString(json);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testJacksonMethodStringMessage() throws JsonProcessingException {
        objectMapper.writeValueAsString(strjson);
    }

    /**
     *
     # Benchmark: com.furture.wson.compress.JSONBench.testJacksonBytesMethod

     # Run progress: 50.00% complete, ETA 00:03:23
     # Fork: 1 of 1
     # Warmup Iteration   1: 887310.298 ops/s
     # Warmup Iteration   2: 1000457.189 ops/s
     # Warmup Iteration   3: 1081816.127 ops/s
     # Warmup Iteration   4: 1206165.542 ops/s
     # Warmup Iteration   5: 1194208.585 ops/s
     Iteration   1: 1226240.414 ops/s
     Iteration   2: 1117295.860 ops/s
     Iteration   3: 1202978.179 ops/s
     Iteration   4: 1373328.524 ops/s
     Iteration   5: mediaJson length 6
     1331420.201 ops/s


     Result "com.furture.wson.compress.JSONBench.testJacksonBytesMethod":
     1250252.635 ±(99.9%) 395628.195 ops/s [Average]
     (min, avg, max) = (1117295.860, 1250252.635, 1373328.524), stdev = 102743.386
     CI (99.9%): [854624.441, 1645880.830] (assumes normal distribution)
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testJacksonBytesMethod() throws JsonProcessingException {
        objectMapper.writeValueAsBytes(json);
    }

    /**
     * # Benchmark: com.furture.wson.compress.JSONBench.testFastJSONString
     *
     * # Run progress: 0.00% complete, ETA 00:06:40
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 2651576.741 ops/s
     * # Warmup Iteration   2: 2451898.744 ops/s
     * # Warmup Iteration   3: 2414624.105 ops/s
     * # Warmup Iteration   4: 2258434.764 ops/s
     * # Warmup Iteration   5: 2532292.886 ops/s
     * Iteration   1: 2526067.040 ops/s
     * Iteration   2: 2408682.499 ops/s
     * Iteration   3: 1973740.478 ops/s
     * Iteration   4: 1643421.403 ops/s
     * Iteration   5: mediaJson length 6
     * 1396035.191 ops/s
     *
     *
     * Result "com.furture.wson.compress.JSONBench.testFastJSONString":
     *   1989589.322 ±(99.9%) 1862531.187 ops/s [Average]
     *   (min, avg, max) = (1396035.191, 1989589.322, 2526067.040), stdev = 483693.434
     *   CI (99.9%): [127058.136, 3852120.509] (assumes normal distribution)
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testFastJSONString() {
        JSON.toJSONString(json, JSONWriter.Feature.WriteByteArrayAsBase64);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testFastJSONStringMessage() {
        JSON.toJSONString(strjson, JSONWriter.Feature.WriteByteArrayAsBase64);
    }

    /**
     * # Warmup Iteration   1: 1434388.979 ops/s
     * # Warmup Iteration   2: 1598958.200 ops/s
     * # Warmup Iteration   3: 1612479.388 ops/s
     * # Warmup Iteration   4: 1774180.690 ops/s
     * # Warmup Iteration   5: 1915735.776 ops/s
     * Iteration   1: 2175543.092 ops/s
     * Iteration   2: 1772906.605 ops/s
     * Iteration   3: 1693881.758 ops/s
     * Iteration   4: 1435178.427 ops/s
     * Iteration   5: mediaJson length 6
     * 1350548.562 ops/s
     *
     *
     * Result "com.furture.wson.compress.JSONBench.testFastjsonBytes":
     *   1685611.689 ±(99.9%) 1251763.876 ops/s [Average]
     *   (min, avg, max) = (1350548.562, 1685611.689, 2175543.092), stdev = 325079.103
     *   CI (99.9%): [433847.813, 2937375.565] (assumes normal distribution)
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testFastjsonBytes() {
        JSON.toJSONBytes(json, JSONWriter.Feature.WriteByteArrayAsBase64);
    }



    @TearDown
    public  void tearDown() {
        //System.out.println("mediaJson length " +  json.size());
    }

    /**
     * Benchmark                                  Mode  Cnt        Score        Error  Units
     * JSONBench.testFastJSONString              thrpt    5  1192146.155 ± 484639.423  ops/s
     * JSONBench.testFastJSONStringMessage       thrpt    5  2444883.524 ± 564837.458  ops/s
     * JSONBench.testFastjsonBytes               thrpt    5   286281.957 ± 527852.469  ops/s
     * JSONBench.testJacksonBytesMethod          thrpt    5   699244.147 ±  68651.788  ops/s
     * JSONBench.testJacksonMethod               thrpt    5   637466.317 ±  70919.637  ops/s
     * JSONBench.testJacksonMethodStringMessage  thrpt    5  1250904.667 ± 811452.584  ops/s
     * */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FastJSONBase64Bench.class.getSimpleName())
                .forks(1)
                //.threads(2)
                .build();

        new Runner(opt).run();
    }
}
