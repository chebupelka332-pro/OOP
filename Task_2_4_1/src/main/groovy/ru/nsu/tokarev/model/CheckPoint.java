package ru.nsu.tokarev.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CheckPoint {
    private String name;
    private LocalDate date;
    private List<String> taskIds = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setDate(String date) { this.date = LocalDate.parse(date); }

    public List<String> getTaskIds() { return taskIds; }
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }
    public void addTaskId(String taskId) { this.taskIds.add(taskId); }
}
