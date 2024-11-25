package com.aflr.multithreading.race.condition.volatile_;

import java.util.Random;

/**
 * In this class well use the volatile keyword to make a non-atomic operation into an atomic operation There'll be a
 * Metrics class with a method to add samples to the metric (BusinessLogic) and another method to get the average
 * (MetricsPrinter). Many thread can add samples and many thread can get the average information. We need to make sure
 * that the data is consistent between threads and no one gets incorrect data.
 */
public class VolatileExample {
    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);

        BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);

        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

        businessLogicThread1.start();
        businessLogicThread2.start();
        metricsPrinter.start();
    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;

        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }

                double currentAverage = metrics.getAverage();

                System.out.println("Current Average is " + currentAverage);
            }
        }
    }


    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(2));
                } catch (InterruptedException e) {
                }

                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }
        }
    }


    public static class Metrics {
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addSample(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        //We don't want this method to be synchronized not to slow down other threads, and it can be executed 100% in parallel to the other threads: volatile
        public double getAverage() {
            return average;
        }
    }
}
