package com.aflr.multithreading.locking;

import java.util.ArrayList;
import java.util.List;

/*
Using Coarse-Grained locking strategy we have only one lock for all the class.
So if many thread use the same lock (object) they will wait until the first thread ends using the method to execute any other synchronized method.

COMPARE THIS TO FineGrained
* */
public class CoarseGrained {
    public static void main(String[] args) throws InterruptedException {
        //In this example thread will run sequentially due to the lock strategy. We have to sum up each method execution time to get the elapsed time.
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

        public synchronized Object getObjectFromDB() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return dbConnection;
        }

        public synchronized void addTask(Object task) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tasks.add(task);
        }

    }
}

