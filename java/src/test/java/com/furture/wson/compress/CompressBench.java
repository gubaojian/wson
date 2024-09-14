package com.furture.wson.compress;

import cn.hutool.core.util.ZipUtil;
import com.furture.wson.utils.FileUtils;
import com.github.gubaojian.wson.io.Pool;
import com.github.luben.zstd.Zstd;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;


@State(Scope.Benchmark)
public class CompressBench {


    static byte[] mediaJson ;
    static byte[] mediaJsonZstddData;
    static byte[] mediaJsonGZIP;

    @Setup
    public void setUp() throws IOException {
         mediaJson = FileUtils.readFile("/media.json").getBytes();
    }

    /**
     * Benchmark                       Mode  Cnt       Score       Error  Units
     * CompressBench.testGZipMethod   thrpt    5   76540.533 ± 14874.451  ops/s
     * CompressBench.testZstdMethod   thrpt    5  189661.647 ± 10487.454  ops/s
     * CompressBench.testZstdMethod2  thrpt    5  193316.564 ± 10536.317  ops/s
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testZstdMethod() {
        mediaJsonZstddData = Zstd.compress(mediaJson);
    }

    /**
     * Benchmark                       Mode  Cnt       Score       Error  Units
     * CompressBench.testGZipMethod   thrpt    5   76540.533 ± 14874.451  ops/s
     * CompressBench.testZstdMethod   thrpt    5  189661.647 ± 10487.454  ops/s
     * CompressBench.testZstdMethod2  thrpt    5  193316.564 ± 10536.317  ops/s
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testZstdMethod2() {
        byte[]  dstBuffer = Pool.requireBuffer(1024);
        long bts  = Zstd.compressByteArray(dstBuffer, 4, 1000, mediaJson, 0, mediaJson.length, Zstd.defaultCompressionLevel());

        //Zstd.isError()
    }

    /**
     * Benchmark                       Mode  Cnt       Score       Error  Units
     * CompressBench.testGZipMethod   thrpt    5   76540.533 ± 14874.451  ops/s
     * CompressBench.testZstdMethod   thrpt    5  189661.647 ± 10487.454  ops/s
     * CompressBench.testZstdMethod2  thrpt    5  193316.564 ± 10536.317  ops/s
     * */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testGZipMethod() {
        mediaJsonGZIP = ZipUtil.gzip(mediaJson);
    }

    @TearDown
    public  void tearDown() {
        System.out.println("mediaJson length " + mediaJson.length);
        if (mediaJsonZstddData != null) {
            System.out.println("mediaJsonCompressedData length " + mediaJsonZstddData.length);
        }

        if (mediaJsonGZIP != null) {
            System.out.println(" mediaJsonGZIP length " + mediaJsonGZIP.length);
        }


    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CompressBench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
