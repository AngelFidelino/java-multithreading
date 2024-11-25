package com.aflr.multithreading.inter.thread;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


public class CountDownLatchExample {
    private static final Logger LOGGER = Logger.getLogger(CountDownLatchExample.class.getName());

    public static void main(String[] args) throws InterruptedException {
        //divideAndConquer();
        startThreadAtTheSameTime();
    }

    /**
     * In this example we'll block each worker thread until all the others have started. And the main thread resumes its
     * flow once all workers are done
     */
    static void startThreadAtTheSameTime() throws InterruptedException {
        class Worker extends Thread {
            private CountDownLatch readyToStartSignal;
            private CountDownLatch startSignal;
            private CountDownLatch doneSignal;

            //Params in this constructor can be confusing. Better option could be using setters to avoid misuse
            public Worker(CountDownLatch readyToStartSignal, CountDownLatch startSignal, CountDownLatch doneSignal) {
                this.readyToStartSignal = readyToStartSignal;
                this.startSignal = startSignal;
                this.doneSignal = doneSignal;
            }

            @Override
            public void run() {
                readyToStartSignal.countDown();//Notify that this thread is ready to start
                try {
                    startSignal.await();//wait here until all threads have being created to continue
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneSignal.countDown();//Notify that this thread is done
                }
            }
        }
        int threads_number = 10;

        //Reaches zero once all thread are created and ready to start
        CountDownLatch readyToStartSignal = new CountDownLatch(threads_number);

        //Reaches zero once the main countdown to start all created threads
        CountDownLatch startSignal = new CountDownLatch(1);

        CountDownLatch doneSignal = new CountDownLatch(threads_number);//Reaches zero once all thread are done

        for (int i = 0; i < threads_number; i++)
            new Worker(readyToStartSignal, startSignal, doneSignal).start();

        readyToStartSignal.await();
        LOGGER.info("All workers are ready to start");

        LOGGER.info("Starting all threads");
        startSignal.countDown();

        long init = System.currentTimeMillis();

        LOGGER.info("Waiting for all thread to finish");
        doneSignal.await();

        LOGGER.info("Workers complete in " + (System.currentTimeMillis() - init) + " ms");
    }

    /**
     * This method considers a whole problem and divides it into N parts and each thread executes each part. The main
     * thread must wait until all thread have done their part
     */
    static void divideAndConquer() throws InterruptedException {
        class LocalWorker extends Thread {
            CountDownLatch doneSignal;

            public LocalWorker(CountDownLatch doneSignal) {
                this.doneSignal = doneSignal;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneSignal.countDown();
                }
            }
        }

        int threads_number = 10;
        CountDownLatch doneSignal = new CountDownLatch(threads_number);
        LOGGER.info("Starting threads");
        long init = System.currentTimeMillis();
        for (int i = 0; i < threads_number; i++)
            new LocalWorker(doneSignal).start();

        doneSignal.await();
        LOGGER.info("All threads are done in " + (System.currentTimeMillis() - init) + " ms");
    }



}
