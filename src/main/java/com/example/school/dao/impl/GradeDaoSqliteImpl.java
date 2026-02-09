package com.example.school.dao.impl;

import com.example.school.dao.GradeDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDaoSqliteImpl implements GradeDao {

    @Override
    public void save(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, grade, date_recorded) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grade.getEnrollmentId());
            pstmt.setDouble(2, grade.getGrade());
            pstmt.setString(3, grade.getDateRecorded());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Grade> findByEnrollmentId(int enrollmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollmentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Grade(
                            rs.getInt("id"),
                            rs.getInt("enrollment_id"),
                            rs.getDouble("grade"),
                            rs.getString("date_recorded")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
