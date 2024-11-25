package com.aflr.multithreading.inter.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * When running tasks by multiple threads concurrently, sometimes we would like to coordinate the work to guarantee that
 * some portion of the work (task1) is done by all threads before the rest of the work is performed (task 2).
 */
public class SemaphoreExample {

    public static void main(String[] args) throws InterruptedException {
        int numberOfThreads = 100;

        List<Thread> threads = new ArrayList<>();

        Barrier barrier = new Barrier(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(new CoordinatedWorkRunner(barrier)));
        }

        for (Thread thread : threads) {
            thread.start();
        }
    }

    /**
     * This class will help to control the locking strategy using a semaphore. At the beginning the semaphore won't have
     * any permits so any thread will be blocked after calling acquire(). Once the last thread ends processing the task.
     * The class will release all the necessary permits so that the blocked thread can continue their process.
     */
    static class Barrier {
        Semaphore semaphore = new Semaphore(0);
        int numberOfThreads;
        int count;
        Lock lock = new ReentrantLock();

        public Barrier(int numberOfThreads) {
            this.numberOfThreads = numberOfThreads;
        }

        public void waitForOthers() throws InterruptedException {
            lock.lock();
            boolean isTheLastThread = false;
            try {
                count++;
                if (count == numberOfThreads)
                    isTheLastThread = true;
            } finally {
                lock.unlock();
            }

            if (isTheLastThread)
                semaphore.release(numberOfThreads - 1);
            else
                semaphore.acquire();
        }
    }


    /**
     * This class contains the tasks that each thread needs to do. It will use the Barrier class (controller) to block
     * all thread until the last one finished its task
     */
    static class CoordinatedWorkRunner implements Runnable {
        private Barrier barrier;

        public CoordinatedWorkRunner(Barrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                task();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private void task() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " part 1 of the work is finished");
            barrier.waitForOthers();
            System.out.println(Thread.currentThread().getName() + " part 2 of the work is finished");
        }
    }
}
