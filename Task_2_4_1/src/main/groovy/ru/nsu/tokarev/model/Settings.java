package ru.nsu.tokarev.model;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private String workDir = "./repos";
    private int timeout = 120;
    private GradeScale gradeScale = new GradeScale();
    private List<ExtraPointsEntry> extraPoints = new ArrayList<>();
    private boolean checkStyleEnabled = true;
    private boolean activityBonusEnabled = false;
    private double activityThreshold = 0.8;
    private double activityBonus = 0.5;

    public String getWorkDir() { return workDir; }
    public void setWorkDir(String workDir) { this.workDir = workDir; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public GradeScale getGradeScale() { return gradeScale; }
    public void setGradeScale(GradeScale gradeScale) { this.gradeScale = gradeScale; }

    public List<ExtraPointsEntry> getExtraPoints() { return extraPoints; }
    public void addExtraPoints(ExtraPointsEntry entry) { this.extraPoints.add(entry); }

    public boolean isCheckStyleEnabled() { return checkStyleEnabled; }
    public void setCheckStyleEnabled(boolean checkStyleEnabled) { this.checkStyleEnabled = checkStyleEnabled; }

    public boolean isActivityBonusEnabled() { return activityBonusEnabled; }
    public void setActivityBonusEnabled(boolean activityBonusEnabled) { this.activityBonusEnabled = activityBonusEnabled; }

    public double getActivityThreshold() { return activityThreshold; }
    public void setActivityThreshold(double activityThreshold) { this.activityThreshold = activityThreshold; }

    public double getActivityBonus() { return activityBonus; }
    public void setActivityBonus(double activityBonus) { this.activityBonus = activityBonus; }

    public double getExtraPointsFor(String nick, String taskId) {
        return extraPoints.stream()
            .filter(e -> e.getStudentNick().equals(nick) && e.getTaskId().equals(taskId))
            .mapToDouble(ExtraPointsEntry::getPoints)
            .sum();
    }

    public static class GradeScale {
        private double excellent = 8.0;
        private double good = 6.0;
        private double satisfactory = 4.0;

        public double getExcellent() { return excellent; }
        public void setExcellent(double excellent) { this.excellent = excellent; }

        public double getGood() { return good; }
        public void setGood(double good) { this.good = good; }

        public double getSatisfactory() { return satisfactory; }
        public void setSatisfactory(double satisfactory) { this.satisfactory = satisfactory; }

        public String grade(double score) {
            if (score >= excellent) return "отлично";
            if (score >= good) return "хорошо";
            if (score >= satisfactory) return "удовлетворительно";
            return "-";
        }
    }
}
