package com.furture.wson.benckmark;

public class Benckmark {

    public static boolean run(String label, Runnable winer, Runnable failer){
        return run(label, 10000000, winer, failer);
    }

    public static boolean run(String label, int times, Runnable winer, Runnable failer){
        doGc();
        long start = System.currentTimeMillis();
        for(int i=0; i< times; i++){
            winer.run();
        }
        long winnerUsed  =  (System.currentTimeMillis() - start);
        System.out.println(label + " winner used " + winnerUsed);
        doGc();
        start = System.currentTimeMillis();
        for(int i=0; i< times; i++){
            failer.run();
        }
        long failerUsed = (System.currentTimeMillis() - start);
        System.out.println(label + " failer used " + failerUsed);
        return winnerUsed < failerUsed;
    }

    public static boolean runTime(String label, int times, Runnable winer, Runnable failer){
        //doGc();
        long start = System.currentTimeMillis();
        for(int i=0; i< times; i++){
            winer.run();
        }
        long winnerUsed  =  (System.currentTimeMillis() - start);
        System.out.println(label + " winner used " + winnerUsed);
        //doGc();
        start = System.currentTimeMillis();
        for(int i=0; i< times; i++){
            failer.run();
        }
        long failerUsed = (System.currentTimeMillis() - start);
        System.out.println(label + " failer used " + failerUsed);
        return winnerUsed < failerUsed;
    }


    protected static void doGc()
    {
        try {
            Thread.sleep(50L);
        } catch (InterruptedException ie) {
            System.err.println("Interrupted while sleeping in serializers.BenchmarkBase.doGc()");
        }
        System.gc();
        try { // longer sleep afterwards (not needed by GC, but may help with scheduling)
            Thread.sleep(200L);
        } catch (InterruptedException ie) {
            System.err.println("Interrupted while sleeping in serializers.BenchmarkBase.doGc()");
        }
    }
}
