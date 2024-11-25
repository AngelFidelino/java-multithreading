package com.aflr.multithreading.interrupt;

/*
 * Copyright (c) 2019-2023. Michael Pogrebinsky - Top Developer Academy
 * https://topdeveloperacademy.com
 * All rights reserved
 */

import java.math.BigInteger;

/**
 * In this example if the user introduced a very large number to process. For example: base=200000, power=100000000. We can interrumpt the calculation process as it will take too much time and resources.
 * Notice: we call interrupt() in the main method, but we need to deal with it in the target method otherwise the interrupt call won't have an effect.
 * We can either use Thread.currentThread().isInterrupted() to know if the thread has been interrupted and return or if the method uses like sleep(), wait(), join() we can use the try/catch InterruptedException
 *
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("200000"), new BigInteger("100000000")));

        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable {
        private BigInteger base;
        private BigInteger power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;

            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }
                result = result.multiply(base);
            }

            return result;
        }
    }
}

