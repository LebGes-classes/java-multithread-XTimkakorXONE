package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        int m = 5;
        List<Worker> workers = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(m);
        
        ExcelHandler excelHandler = new ExcelHandler("worker_statistics.xlsx");

        Thread botThread = new Thread(() -> {
            TelegaBot.setWorkers(workers);
            TelegaBot.main(new String[]{});
        });
        botThread.start();

        for (int i = 0; i < m; i++) {
            Worker worker = new Worker(i+1, "Работник: " + (i+1));
            workers.add(worker);

            WorkingProcess process = new WorkingProcess(worker);
            executorService.submit(process);

            for (int j = 1; j <= m; j++) {
                Task task = new Task("Task: " + j, random.nextInt(16));
                worker.addTask(task);
                excelHandler.saveTaskStatistics(worker, task);
            }
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(30, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }

        for (Worker worker : workers) {
            System.out.println(worker);
            excelHandler.saveWorkerStatistics(worker);
        }
    }
}