package com.example;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkingProcess implements Runnable {
    private final Worker worker;
    private final List<Task> tasks;
    private final int WORK_DAY_HOURS = 8;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public WorkingProcess(Worker worker) {
        this.worker = worker;
        this.tasks = worker.getTasks();
    }

    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void run() {
        int id = worker.getId();
        System.out.println("Работник " + id + " начал работать.");

        try {
            while (!tasks.isEmpty() && isRunning.get()) {
                int workDayHours = 0;

                while (workDayHours < WORK_DAY_HOURS && !tasks.isEmpty() && isRunning.get()) {
                    Task currentTask;
                    synchronized (tasks) {
                        if (tasks.isEmpty()) break;
                        currentTask = tasks.get(0);
                    }

                    int taskDuration = currentTask.getDuration();

                    if (taskDuration > WORK_DAY_HOURS - workDayHours) {
                        int timeToWorkToday = WORK_DAY_HOURS - workDayHours;

                        System.out.println("Работник " + id + ". Выполняет задачу \"" +
                                currentTask.getTaskName() + "\". Время выполнения за сегодня: " +
                                timeToWorkToday + " ч. Осталось: " + (taskDuration - timeToWorkToday) + " ч.");

                        try {
                            Thread.sleep(1000 * timeToWorkToday);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }

                        worker.addWorkTime(timeToWorkToday);
                        workDayHours += timeToWorkToday;

                        Task remainingTask = new Task(currentTask.getTaskName(), taskDuration - timeToWorkToday);
                        synchronized (tasks) {
                            tasks.set(0, remainingTask);
                        }

                    } else {
                        System.out.println("Работник " + id + ". Выполняет задачу \"" +
                                currentTask.getTaskName() + "\". Время выполнения: " +
                                taskDuration + " ч.");

                        try {
                            Thread.sleep(1000 * taskDuration);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }

                        worker.addWorkTime(taskDuration);
                        workDayHours += taskDuration;

                        synchronized (tasks) {
                            tasks.remove(0);
                        }
                    }
                }

                if (isRunning.get()) {
                    int dayIdleTime = WORK_DAY_HOURS - workDayHours;
                    worker.addIdleTime(dayIdleTime);
                    worker.addDaysWorked();

                    System.out.println("Работник " + id + ". День " + worker.getDaysWorked() +
                            ". Работал: " + workDayHours + " ч. Простой: " + dayIdleTime + " ч.");
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка в работе работника " + id + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Работник " + id + " завершил работу.");
        }
    }
}