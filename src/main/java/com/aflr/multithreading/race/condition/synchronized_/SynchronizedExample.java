package com.aflr.multithreading.race.condition.synchronized_;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class present an inventory class and an increment and a decrement thread. We loop 1000 through each thread an
 * the end we'd expect to have zero items in tne inventory since for each increment there was a decrement call. But
 * without any type of synchronization will have different results like: -400 items, -60 items, etc
 *
 * One solution could be use the synchronized in the increment and in the decrement method. With this only one thread
 * can call either of the methods at a time. Only one single lock. public synchronized void increment(){} public
 * synchronized void decrement(){} The second option is to use the synchronized keyword only in the critical section
 * without making the entire method synchronized. Many locks. public synchronized void increment(){  synchronized
 * (object){ }  }
 */
public class SynchronizedExample {
    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("We currently have " + inventoryCounter.getItems() + " items");
    }

    public static class DecrementingThread extends Thread {

        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }


    public static class IncrementingThread extends Thread {

        private InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }


    private static class InventoryCounter {
        private int items = 0;
        Object object = new Object();
        Lock lock = new ReentrantLock();

        public void increment() {
            synchronized (object) {
                items++;
            }
        }

        public void decrement() {
            synchronized (object) {
                items--;
            }
        }

        public int getItems() {
            return items;
        }
    }
}
