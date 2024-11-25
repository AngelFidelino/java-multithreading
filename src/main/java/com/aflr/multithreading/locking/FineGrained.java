package com.aflr.multithreading.locking;

import java.util.ArrayList;
import java.util.List;

/**
 * Using Fine-Grained locking strategy we lock on every shared resource individually which is equivalent to creating a
 * separate lock for every resource. So if many thread use the same lock (object) they can still execute any other
 * synchronized methods while another thread is executing any other one. There is no single lock. This allows more
 * parallelism and less contention.
 *
 * COMPARE THIS TO CoarseGrained
 */
public class FineGrained {
    public static void main(String[] args) throws InterruptedException {
        //In this example thread will run in parallel due to the lock strategy. To get the elapsed time, we can take the highest method execution time of all method involved.
        SharedClass sharedClass = new SharedClass();

        Thread thread1 = new Thread(() -> sharedClass.getObjectFromDB());
        Thread thread2 = new Thread(() -> sharedClass.addTask(new Object()));
        long init = System.currentTimeMillis();
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        long end = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (end - init) / 1000);
    }



    static class SharedClass {
        private Object dbConnection;
        private List<Object> tasks;

        public SharedClass() {
            dbConnection = new Object();
            tasks = new ArrayList<>();
        }

        public Object getObjectFromDB() {
            synchronized (dbConnection) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return dbConnection;
        }

        public synchronized void addTask(Object task) {

            synchronized (tasks) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tasks.add(task);
            }
        }

    }
}

