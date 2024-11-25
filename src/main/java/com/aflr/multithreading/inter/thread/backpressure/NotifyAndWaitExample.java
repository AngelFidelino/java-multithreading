package com.aflr.multithreading.inter.thread.backpressure;

/**
 * When we use join the second thread has to finished for the first thread to wake up. In contrast, the wait() and
 * notify() methods allow the waiting thread to pause until worker thread has partially executed and is still active. We
 * define precisely the circumstances under which the waiting thread resumes. When the waiting thread calls wait(), the
 * execution of that thread is suspended until another thread calls notify() on that same object.
 *
 * We can use any object as a condition variable using wait and notify
 */
public class NotifyAndWaitExample {
    public static void main(String[] args) {
        exampleUsingArbitraryLock();
       // exampleUsingCurrentLock();
    }

    static void exampleUsingArbitraryLock() {
        Object monitorObj = new Object();
        Worker worker = new Worker(monitorObj);
        Thread thread = new Thread(worker);

        synchronized (monitorObj) { //acquire monitor
            thread.start(); //The worker thread has been started but leave in the WAITING state since the monitor is not free
            try {
                System.out.println(
                        Thread.currentThread().getName() + " is going to pause until another thread wakes it up");
                monitorObj.wait(); // release the lock
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " thread has been awakened");
    }

    static class Worker implements Runnable {
        Object lock;

        public Worker(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " waiting to acquire the monitor");
            synchronized (lock) {
                System.out.println(Thread.currentThread().getName() + " acquires the monitor and notifying others");
                lock.notify();
            }
            System.out.println(
                    Thread.currentThread().getName() + " thread can continue running as usual after notifying others");
        }
    }

    static void exampleUsingCurrentLock() {
        SharedClass sharedClass = new SharedClass();
        Thread waitingThread = new Thread(() -> {
            try {
                sharedClass.waitUntilComplete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread notifier = new Thread(() -> sharedClass.complete());

        waitingThread.start();
        notifier.start();
    }

    static class SharedClass {
        private boolean isComplete;

        public void waitUntilComplete() throws InterruptedException {
            System.out.println(Thread.currentThread().getName() + " executing waitUntilComplete() ...");
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " acquired the lock");
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " entering the WAITING state");
                while (!isComplete) {
                    this.wait();
                }
            }
            System.out.println(Thread.currentThread().getName() + " completes");
        }

        public void complete() {
            System.out.println(Thread.currentThread().getName() + " executing complete() ...");
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " acquired the lock");
                System.out.println(Thread.currentThread().getName() + " notifying WAITING thread");
                isComplete = true;
                notify();
            }
            System.out.println(Thread.currentThread().getName() + " completes");
        }
    }
}
