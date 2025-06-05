package com.example;

public class Task {
    private final String taskName;
    private final int duration;

    public Task(String taskName, int duration) {
        this.taskName = taskName;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public String getTaskName() {
        return taskName;
    }
    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", duration=" + duration +
                '}';
    }
}
