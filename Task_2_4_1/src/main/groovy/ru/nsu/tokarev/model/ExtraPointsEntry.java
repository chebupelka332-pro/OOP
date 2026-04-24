package ru.nsu.tokarev.model;

public class ExtraPointsEntry {
    private String studentNick;
    private String taskId;
    private double points;

    public ExtraPointsEntry(String studentNick, String taskId, double points) {
        this.studentNick = studentNick;
        this.taskId = taskId;
        this.points = points;
    }

    public String getStudentNick() { return studentNick; }
    public String getTaskId() { return taskId; }
    public double getPoints() { return points; }
}
