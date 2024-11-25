package com.aflr.multithreading.deadlock;

import java.util.Random;

/**
 * This class simulate a deadlock scenario where there are two roads: A and B Two train want to take a road. Once a
 * train take a road it will take the next road in a row. Then say that the trainA take the roadA and at the same time
 * trainB take the roadB, trainA will try to take the roadB that was already taken by train B and trainB will try to
 * take roadA that was taken by trainA: a deadlock
 *
 * The solution will be to acquire the lock in the same order everywhere in the code.
 */
public class DeadLockExample {

    public static void main(String[] args) {
        Intersection intersection = new Intersection();

        TrainA trainA = new TrainA(intersection);
        TrainB trainB = new TrainB(intersection);
        trainA.start();
        trainB.start();
    }

    public static class TrainA extends Thread {
        private Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadA();
            }
        }
    }


    public static class TrainB extends Thread {
        private Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadB();
            }
        }
    }


    static class Intersection {

        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by " + Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadB) {
                System.out.println("Road B is locked by " + Thread.currentThread().getName());
                synchronized (roadA) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
