package com.example.school.model;

public class Grade {
    private int id;
    private int enrollmentId;
    private double grade;
    private String dateRecorded;

    public Grade() {
    }

    public Grade(int id, int enrollmentId, double grade, String dateRecorded) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.grade = grade;
        this.dateRecorded = dateRecorded;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
    }
}
