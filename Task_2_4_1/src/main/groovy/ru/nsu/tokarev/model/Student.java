package ru.nsu.tokarev.model;

public class Student {
    private String nick;
    private String fullName;
    private String repo;

    public String getNick() { return nick; }
    public void setNick(String nick) { this.nick = nick; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRepo() { return repo; }
    public void setRepo(String repo) { this.repo = repo; }

    @Override
    public String toString() {
        return "Student{nick='" + nick + "', fullName='" + fullName + "'}";
    }
}
