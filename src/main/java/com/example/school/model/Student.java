package com.example.school.model;

public class Student {
    private int id;
    private int userId;
    private String studentNumber;
    private String email;
    private String dateOfBirth;

    public Student() {
    }

    public Student(int id, int userId, String studentNumber, String email, String dateOfBirth) {
        this.id = id;
        this.userId = userId;
        this.studentNumber = studentNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
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

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
