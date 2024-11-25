package com.aflr.multithreading.virtual.thread;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Here we can compare the use of virtual thread when calling external call versus a normal multi-thread operation using
 * the normal platform threads
 */
public class VirtualThreadExample {
    private final static Logger LOGGER = Logger.getLogger(VirtualThreadExample.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        int threadNumber = 500;
        final long init = System.currentTimeMillis();
        executeLongBlockingIOOperationUsingVirtualThreads(threadNumber); //Time elapsed: 6,301 ms
        //executeLongBlockingIOOperation(threadNumber);//9,523 ms
        LOGGER.log(Level.INFO, "Time elapsed: {0} ms", (System.currentTimeMillis() - init));
    }

    private static void executeLongBlockingIOOperation(int threadNumber) {
        try (ExecutorService executorService = Executors.newCachedThreadPool()) {
            for (int i = 0; i < threadNumber; i++) {
                executorService.submit(() -> {
                    try {
                        getExternalData();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private static void executeLongBlockingIOOperationUsingVirtualThreads(int threadNumber) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < threadNumber; i++) {
                executorService.submit(() -> {
                    try {
                        getExternalData();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private static void getExternalData() throws IOException, InterruptedException {
        Thread.sleep(10);
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dog.ceo/api/breeds/image/random"))
                .GET()
                .build();

        final HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.INFO, "Body: {0}", httpResponse.body());
    }
}
