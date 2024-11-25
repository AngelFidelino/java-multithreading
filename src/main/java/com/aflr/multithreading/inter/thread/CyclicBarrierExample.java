package com.aflr.multithreading.inter.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ThreadLocalRandom;

/*
threads perform and store the corresponding results in a list.
When all threads finish performing their action, the last one starts processing the data that was fetched by each of the threads.
* */
public class CyclicBarrierExample {
    public static void main(String[] args) {
        Collections.sort(List.of(""));
        CyclicBarrierDemo cyclicBarrierDemo = new CyclicBarrierDemo();
        cyclicBarrierDemo.execute();
    }

    static class CyclicBarrierDemo {
        public static final int THREAD_NUMBER = 4;
        List<Integer> sharedResult;
        CyclicBarrier cyclicBarrier;

        public CyclicBarrierDemo() {
            cyclicBarrier = new CyclicBarrier(THREAD_NUMBER, aggregator);
            sharedResult = Collections.synchronizedList(new ArrayList<>());
        }

        public void execute() {
            long init = System.currentTimeMillis();
            for (int i = 0; i < THREAD_NUMBER; i++) {
                new Thread(new Worker(cyclicBarrier, sharedResult)).start();
            }
        }

        private Runnable aggregator = () -> {
            final int sum = sharedResult.stream().mapToInt(i -> i).sum();
            System.out.println("Final sum: " + sum);
        };
    }


    static class Worker implements Runnable {
        List<Integer> sharedResult;
        CyclicBarrier cyclicBarrier;
        List<Integer> partialResult;

        public Worker(CyclicBarrier cyclicBarrier, List<Integer> s) {
            this.cyclicBarrier = cyclicBarrier;
            this.sharedResult = s;
            partialResult = new ArrayList<>();
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                final int localTotal = ThreadLocalRandom.current().nextInt(10);
                partialResult.add(localTotal);
            }

            sharedResult.addAll(partialResult);

            try {
                System.out.println(Thread.currentThread().getName() + ": local total sum: " + partialResult.stream().mapToInt(i -> i).sum());
                System.out.println(Thread.currentThread().getName() + ": waiting for others to reach barrier");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
