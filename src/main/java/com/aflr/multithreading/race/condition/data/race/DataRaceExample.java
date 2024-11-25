package com.aflr.multithreading.race.condition.data.race;

/**
 * This class will show how the re-ordering and optimization of the jvm can alter the result In this example the
 * increment method should contain the code in an specific order so that the checkForDataRace won't fail and the result
 * be the same always The synchronized and the volatile keyword guarantee a free-data-race code. Declaring a shared
 * variable as volatile guarantees that code that comes before an access to that volatile variable will be executed
 * before that excess instruction and code that comes after an access to the volatile variable will be executed after
 * the excess instruction.
 */
public class DataRaceExample {
    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.checkForDataRace();
            }

        });

        thread1.start();
        thread2.start();
    }

    public static class SharedClass {
        private int x = 0;
        private volatile int y = 0;


        //increment and checkForDataRace have no dependence each other so the JVM can re-order the increment method so that y++ comes before x++
        // note: only making y volatile would be enough
        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if (y > x) {
                System.out.println("y > x - Data Race is detected");
            }
        }
    }
}
