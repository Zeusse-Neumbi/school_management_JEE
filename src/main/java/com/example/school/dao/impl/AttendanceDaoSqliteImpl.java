package com.example.school.dao.impl;

import com.example.school.dao.AttendanceDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDaoSqliteImpl implements AttendanceDao {

    @Override
    public void save(Attendance attendance) {
        String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getEnrollmentId());
            pstmt.setString(2, attendance.getAttendanceDate());
            pstmt.setString(3, attendance.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Attendance> findByEnrollmentId(int enrollmentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Attendance(
                            rs.getInt("id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("attendance_date"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Attendance> findByEnrollmentIdAndDate(int enrollmentId, String date) {
        String sql = "SELECT * FROM attendance WHERE enrollment_id = ? AND attendance_date = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            pstmt.setString(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Attendance(
                            rs.getInt("id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("attendance_date"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void update(Attendance attendance) {
        String sql = "UPDATE attendance SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, attendance.getStatus());
            pstmt.setInt(2, attendance.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getAttendanceRate(int studentId) {
        // 1. Get all enrollments for the student
        String enrollmentSql = "SELECT id FROM enrollments WHERE student_id = ?";
        List<Integer> enrollmentIds = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(enrollmentSql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    enrollmentIds.add(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }

        if (enrollmentIds.isEmpty()) {
            return 100.0; // Default to 100% if no enrollments/attendance records found (benefit of doubt)
        }

        // 2. Count total attendance records and 'Present' status
        int totalClasses = 0;
        int presentClasses = 0;

        String attendanceSql = "SELECT status FROM attendance WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(attendanceSql)) {

            for (Integer enrollmentId : enrollmentIds) {
                pstmt.setInt(1, enrollmentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        totalClasses++;
                        if ("Present".equalsIgnoreCase(rs.getString("status"))) {
                            presentClasses++;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalClasses > 0 ? ((double) presentClasses / totalClasses) * 100.0 : 100.0;
    }
}
