package com.example.school.model;

public class Attendance {
    private int id;
    private int enrollmentId;
    private String attendanceDate;
    private String status;

    public Attendance() {
    }

    public Attendance(int id, int enrollmentId, String attendanceDate, String status) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.attendanceDate = attendanceDate;
        this.status = status;
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

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
