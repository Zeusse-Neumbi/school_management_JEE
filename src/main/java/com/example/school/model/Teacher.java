package com.example.school.model;

public class Teacher {
    private int id;
    private int userId;
    private String employeeId;
    private String email;
    private String specialization;

    public Teacher() {
    }

    public Teacher(int id, int userId, String employeeId, String email, String specialization) {
        this.id = id;
        this.userId = userId;
        this.employeeId = employeeId;
        this.email = email;
        this.specialization = specialization;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
