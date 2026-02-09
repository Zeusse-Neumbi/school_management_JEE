package com.example.school.dao.impl;

import com.example.school.dao.CourseDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDaoSqliteImpl implements CourseDao {

    @Override
    public void save(Course course) {
        String sql = "INSERT INTO courses (course_name, course_code, teacher_id, credits) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            pstmt.setInt(3, course.getTeacherId());
            pstmt.setInt(4, course.getCredits());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Course course) {
        String sql = "UPDATE courses SET course_name = ?, course_code = ?, teacher_id = ?, credits = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            pstmt.setInt(3, course.getTeacherId());
            pstmt.setInt(4, course.getCredits());
            pstmt.setInt(5, course.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Course> findById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public List<Course> findByTeacherId(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public List<Course> findByStudentId(int studentId) {
        List<Course> courses = new ArrayList<>();
        // Join with enrollments to find courses for a student
        String sql = "SELECT c.* FROM courses c " +
                "JOIN enrollments e ON c.id = e.course_id " +
                "WHERE e.student_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("id"),
                rs.getString("course_name"),
                rs.getString("course_code"),
                rs.getInt("teacher_id"),
                rs.getInt("credits"));
    }
}
