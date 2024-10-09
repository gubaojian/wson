package com.furture.wson.compress;

import cn.hutool.core.thread.GlobalThreadPool;
import com.furture.wson.utils.FileUtils;
import com.github.luben.zstd.Zstd;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.*;

public class ZstdTest {

    public static void main(String[] args) throws RunnerException, IOException, InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(
                40, 100,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        byte[] bts = FileUtils.readFileBytes("/media.json");

        for (int i = 0; i < 100 * 10000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Zstd.compress(bts);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }
}
