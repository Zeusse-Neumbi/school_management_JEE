package com.example.school.dao.impl;

import com.example.school.dao.AttendanceDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}
