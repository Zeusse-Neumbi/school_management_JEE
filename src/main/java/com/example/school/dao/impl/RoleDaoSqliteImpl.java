package com.example.school.dao.impl;

import com.example.school.dao.RoleDao;
import com.example.school.dao.db.DatabaseManager;
import com.example.school.model.Role;
import java.sql.*;
import java.util.Optional;

public class RoleDaoSqliteImpl implements RoleDao {

    @Override
    public Optional<Role> findById(int id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Role(rs.getInt("id"), rs.getString("role_name")));
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE role_name = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Role(rs.getInt("id"), rs.getString("role_name")));
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
        return Optional.empty();
    }
}
