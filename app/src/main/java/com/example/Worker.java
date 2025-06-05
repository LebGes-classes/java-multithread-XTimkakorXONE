package com.example;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CopyOnWriteArrayList;

public class Worker {
    private final int id;
    private final String name;
    private final List<Task> tasks;
    private final AtomicInteger totalWorkTime;
    private final AtomicInteger idleTime;
    private final AtomicInteger daysWorked;

    public Worker(int id, String name) {
        this.id = id;
        this.name = name;
        this.tasks = new CopyOnWriteArrayList<>();
        this.totalWorkTime = new AtomicInteger(0);
        this.idleTime = new AtomicInteger(0);
        this.daysWorked = new AtomicInteger(0);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addWorkTime(int time) {
        totalWorkTime.addAndGet(time);
    }

    public void addIdleTime(int time) {
        idleTime.addAndGet(time);
    }

    public void addDaysWorked() {
        daysWorked.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public int getTotalWorkTime() {
        return totalWorkTime.get();
    }

    public int getIdleTime() {
        return idleTime.get();
    }

    public int getDaysWorked() {
        return daysWorked.get();
    }

    @Override
    public String toString() {
        return String.format("Работник %d (%s):\n" +
                "  Всего отработано часов: %d\n" +
                "  Время простоя: %d часов\n" +
                "  Отработано дней: %d\n" +
                "  Осталось задач: %d",
                id, name, getTotalWorkTime(), getIdleTime(), getDaysWorked(), tasks.size());
    }
}