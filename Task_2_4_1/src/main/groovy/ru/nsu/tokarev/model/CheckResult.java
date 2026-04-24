package ru.nsu.tokarev.model;

public class CheckResult {
    private boolean buildSuccess;
    private boolean docsSuccess;
    private boolean styleSuccess;
    private int passedTests;
    private int failedTests;
    private int skippedTests;
    private double extraPoints;
    private double score;
    private String buildLog = "";
    private String errorMessage = "";

    public boolean isBuildSuccess() { return buildSuccess; }
    public void setBuildSuccess(boolean buildSuccess) { this.buildSuccess = buildSuccess; }

    public boolean isDocsSuccess() { return docsSuccess; }
    public void setDocsSuccess(boolean docsSuccess) { this.docsSuccess = docsSuccess; }

    public boolean isStyleSuccess() { return styleSuccess; }
    public void setStyleSuccess(boolean styleSuccess) { this.styleSuccess = styleSuccess; }

    public int getPassedTests() { return passedTests; }
    public void setPassedTests(int passedTests) { this.passedTests = passedTests; }

    public int getFailedTests() { return failedTests; }
    public void setFailedTests(int failedTests) { this.failedTests = failedTests; }

    public int getSkippedTests() { return skippedTests; }
    public void setSkippedTests(int skippedTests) { this.skippedTests = skippedTests; }

    public double getExtraPoints() { return extraPoints; }
    public void setExtraPoints(double extraPoints) { this.extraPoints = extraPoints; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getBuildLog() { return buildLog; }
    public void setBuildLog(String buildLog) { this.buildLog = buildLog; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String testsString() {
        return passedTests + "/" + failedTests + "/" + skippedTests;
    }
}
