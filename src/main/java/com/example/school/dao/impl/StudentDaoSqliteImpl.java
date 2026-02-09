package com.example.school.dao.impl;

import com.example.school.dao.StudentDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDaoSqliteImpl implements StudentDao {

    @Override
    public void save(Student student) {
        String sql = "INSERT INTO students (user_id, student_number, email, date_of_birth) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getStudentNumber());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getDateOfBirth());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Student student) {
        String sql = "UPDATE students SET user_id = ?, student_number = ?, email = ?, date_of_birth = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getUserId());
            pstmt.setString(2, student.getStudentNumber());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getDateOfBirth());
            pstmt.setInt(5, student.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Student> findById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByStudentNumber(String studentNumber) {
        String sql = "SELECT * FROM students WHERE student_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByUserId(int userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students";
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM students WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Student> search(String query, int page, int pageSize) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.* FROM students s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR s.student_number LIKE ? OR s.email LIKE ? " +
                "LIMIT ? OFFSET ?";
        int offset = (page - 1) * pageSize;
        if (offset < 0)
            offset = 0;
        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setInt(5, pageSize);
            pstmt.setInt(6, offset);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public int count(String query) {
        String sql = "SELECT COUNT(*) FROM students s " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE u.first_name LIKE ? OR u.last_name LIKE ? OR s.student_number LIKE ? OR s.email LIKE ?";
        String searchPattern = "%" + query + "%";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("student_number"),
                rs.getString("email"),
                rs.getString("date_of_birth"));
    }
}
