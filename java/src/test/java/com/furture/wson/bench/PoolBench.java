package com.furture.wson.bench;

import cn.hutool.core.util.ZipUtil;
import com.furture.wson.bench.custom.LockFreePool;
import com.furture.wson.bench.custom.PoolObject;
import com.furture.wson.compress.FastJSONBase64Bench;
import com.github.gubaojian.wson.io.Pool;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;

@State(Scope.Benchmark)
public class PoolBench {

    private LockFreePool lockFreePool = new LockFreePool(128,  () -> new byte[1024]);

    private  byte[] bts;

    @Setup
    public void setUp() throws IOException {

    }


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testThreadLocalPool() {
        byte[] buffer  = Pool.requireBuffer(1024);
        bts =  buffer;
        Pool.returnBuffer(buffer);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testLockFreePool() {
       PoolObject<byte[]> poolObject =  lockFreePool.getPoolObject();
        bts = poolObject.getObject();
        lockFreePool.returnPoolObject(poolObject);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void testNewObject() {
        byte[] buffer  = new byte[1024];
        bts = buffer;
    }


    /**
     * Benchmark                       Mode  Cnt         Score         Error  Units
     * PoolBench.testLockFreePool     thrpt    5  37688972.198 ± 2262317.467  ops/s
     * PoolBench.testNewObject        thrpt    5  12261365.704 ± 1817972.654  ops/s
     * PoolBench.testThreadLocalPool  thrpt    5  49716607.457 ± 7912791.177  ops/s
     *
     * Benchmark                       Mode  Cnt         Score          Error  Units
     * PoolBench.testLockFreePool     thrpt    5  36725655.067 ±  1302965.271  ops/s
     * PoolBench.testNewObject        thrpt    5  11819963.533 ±  1481136.832  ops/s
     * PoolBench.testThreadLocalPool  thrpt    5  54735853.065 ± 10651429.370  ops/s
     *
     * Benchmark                       Mode  Cnt         Score          Error  Units
     * PoolBench.testLockFreePool     thrpt    5  36533056.312 ±  2983558.823  ops/s
     * PoolBench.testNewObject        thrpt    5  12227047.891 ±  1182531.711  ops/s
     * PoolBench.testThreadLocalPool  thrpt    5  40558966.906 ± 12416095.478  ops/s
     * */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PoolBench.class.getSimpleName())
                .forks(1)
                //.threads(2)
                .build();

        new Runner(opt).run();
    }
}
