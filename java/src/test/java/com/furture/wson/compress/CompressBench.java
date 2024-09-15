package com.furture.wson.compress;

import cn.hutool.core.util.ZipUtil;
import com.furture.wson.utils.FileUtils;
import com.github.gubaojian.wson.io.Pool;
import com.github.luben.zstd.Zstd;
import io.airlift.compress.Compressor;
import io.airlift.compress.lz4.Lz4Compressor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.xerial.snappy.Snappy;

import java.io.IOException;


@State(Scope.Benchmark)
public class CompressBench {


    static byte[] mediaJson ;
    static byte[] mediaJsonZstddData;
    static byte[] mediaJsonGZIP;
    static byte[] mediaJsonSnappy;
    static long mediaJsonLZ4;

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
        Pool.returnBuffer(dstBuffer);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testZstdMethod3() {
        byte[] dstBuffer = new byte[1024];
        long bts = Zstd.compressByteArray(dstBuffer, 4, 1000, mediaJson, 0, mediaJson.length, Zstd.defaultCompressionLevel());
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

    /**
     * Benchmark                       Mode  Cnt       Score       Error  Units
     * CompressBench.testGZipMethod   thrpt    5   76540.533 ± 14874.451  ops/s
     * CompressBench.testZstdMethod   thrpt    5  189661.647 ± 10487.454  ops/s
     * CompressBench.testZstdMethod2  thrpt    5  193316.564 ± 10536.317  ops/s
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testSnappyMethod() {
        try {
            mediaJsonSnappy = Snappy.compress(mediaJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testLz4() throws IOException {
        Compressor compressor = new Lz4Compressor();
        byte[] compressed = new byte[compressor.maxCompressedLength(mediaJson.length)];
        int compressedSize = compressor.compress(mediaJson, 0, mediaJson.length, compressed, 0, compressed.length);
        mediaJsonLZ4 = compressedSize;
    }





    @TearDown
    public  void tearDown() {
        System.out.println("mediaJson length " + mediaJson.length);
        if (mediaJsonZstddData != null) {
            System.out.println("mediaJsonZstdData length " + mediaJsonZstddData.length);
        }
        if (mediaJsonGZIP != null) {
            System.out.println(" mediaJsonGZIP length " + mediaJsonGZIP.length);
        }

        if (mediaJsonSnappy != null) {
            System.out.println("mediaJsonSnappy length " + mediaJsonSnappy.length);
        }

        if (mediaJsonLZ4 > 0) {
            System.out.println("mediaJsonLZ4 length " + mediaJsonLZ4);
        }
    }

    /**
     * mediaJson length 518
     * mediaJsonZstdData length 256
     * mediaJsonSnappy length 311
     * mediaJsonGZIP length 253
     * <p>
     * Benchmark                        Mode  Cnt        Score        Error  Units
     * CompressBench.testGZipMethod    thrpt    5   240152.316 ±  46975.487  ops/s
     * CompressBench.testLz4           thrpt    5   869716.826 ±   8540.505  ops/s
     * CompressBench.testSnappyMethod  thrpt    5  4798250.372 ±  87471.326  ops/s
     * CompressBench.testZstdMethod    thrpt    5   556414.462 ± 317990.391  ops/s
     * CompressBench.testZstdMethod2   thrpt    5   636500.773 ±  61554.324  ops/s
     * CompressBench.testZstdMethod3   thrpt    5   633694.747 ±  39903.770  ops/s
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(CompressBench.class.getSimpleName())
                .forks(1)
                .threads(4)
                .build();

        new Runner(opt).run();
    }
}
