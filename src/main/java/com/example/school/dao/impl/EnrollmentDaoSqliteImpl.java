package com.example.school.dao.impl;

import com.example.school.dao.EnrollmentDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDaoSqliteImpl implements EnrollmentDao {

    @Override
    public void save(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, enrollment.getStudentId());
            pstmt.setInt(2, enrollment.getCourseId());
            pstmt.setString(3, enrollment.getEnrollmentDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Enrollment> findById(int id) {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Enrollment> findByCourseId(int courseId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Enrollment(
                            rs.getInt("id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getString("enrollment_date")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
