package ru.nsu.tokarev.model;

import java.time.LocalDate;

public class Task {
    private String id;
    private String name;
    private double maxScore;
    private LocalDate softDeadline;
    private LocalDate hardDeadline;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }

    public LocalDate getSoftDeadline() { return softDeadline; }
    public void setSoftDeadline(LocalDate softDeadline) { this.softDeadline = softDeadline; }
    public void setSoftDeadline(String softDeadline) { this.softDeadline = LocalDate.parse(softDeadline); }

    public LocalDate getHardDeadline() { return hardDeadline; }
    public void setHardDeadline(LocalDate hardDeadline) { this.hardDeadline = hardDeadline; }
    public void setHardDeadline(String hardDeadline) { this.hardDeadline = LocalDate.parse(hardDeadline); }

    @Override
    public String toString() {
        return "Task{id='" + id + "', name='" + name + "', maxScore=" + maxScore + "}";
    }
}
