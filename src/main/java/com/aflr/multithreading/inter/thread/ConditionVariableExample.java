package com.aflr.multithreading.inter.thread;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A condition variable provides us of a way of scheduling threads waiting on a condition variable (e.g., waking up
 * after a timeout). This example applies a backpressure on the producer as it cannot produce more than a defined
 * capacity until the consumer has processed the records in the queue
 */
public class ConditionVariableExample {

    public static void main(String[] args) throws InterruptedException {
        final int itemsNum = 16;
        Storage storage = new Storage(5);

        List<Object> items = new ArrayList<>();

        for (int i = 0; i < itemsNum; i++) {
            items.add(new Object());
        }

        Thread producer = new Thread(() -> {
            storage.setProcessing();
            for (Object item : items) {
                try {
                    storage.addItem(item);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            storage.setComplete();//If the caller doesn't set the complete status, the tread should finish itself automatically after a certain period
        });

        Thread consumer = new Thread(() -> {
            //Clear the queue until there is nothing else to process
            int counter = 0;
            do {
                counter++;
                storage.clearQueue();
            } while (!storage.isCompleted() || counter == itemsNum);
        });

        consumer.start();
        producer.start();

        consumer.join();
        producer.join();

        System.out.println("storage items: " + storage.getItems().size());
    }

    static class Storage {
        Lock lock = new ReentrantLock();
        Condition conditionConsumer = lock.newCondition();
        Condition conditionProducer = lock.newCondition();
        volatile Queue<Object> items = new ArrayDeque<>();
        int maxCapacity;
        private Status status;

        public Storage(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            status = Status.INITIAL;
        }

        public void setProcessing() {
            this.status = Status.PROCESSING;
        }

        public void setComplete() {
            this.status = Status.COMPLETE;
            lock.lock();
            try {
                conditionConsumer.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public boolean isCompleted() {
            return status == Status.COMPLETE || status == Status.INTERRUPTED;
        }

        public Queue<Object> getItems() {
            return items;
        }

        /**
         * Add items to the queue to be processed by the consumer until the former reaches the max capacity. Once it
         * reached the max capacity it has to wait until the consumer dequeues items.
         */
        public void addItem(Object newItem) throws InterruptedException {
            lock.lock();
            try {
                while (items.size() == maxCapacity) {
                    conditionConsumer.signalAll();
                    System.out.println(Thread.currentThread().getName() + " entering the WAIT state");
                    conditionProducer.await();
                }
            } finally {
                lock.unlock();
            }
            items.add(newItem);
            System.out.println(Thread.currentThread().getName() + " current size: " + items.size());
        }

        /**
         * It removes all the items from the queue when the latter has reached its max capacity otherwise it waits until
         * the queue is full or the process is complete If the producer doesn't end the process it waits for a certain
         * period of time til no more records are received and then finish itself
         */
        public void clearQueue() {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + " entering to clear queue");

            try {
                while (items.size() != maxCapacity && !isCompleted()) {
                    boolean elapsed = !conditionConsumer.await(5000, TimeUnit.MILLISECONDS);
                    if (elapsed)
                        status = Status.INTERRUPTED;
                }
                items.clear();
                System.out.println(Thread.currentThread().getName() + " clearing queue");
                conditionProducer.signalAll();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was Interrupted");
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }

        }

        public enum Status {
            INITIAL,
            PROCESSING,
            COMPLETE,
            INTERRUPTED
        }
    }
}
