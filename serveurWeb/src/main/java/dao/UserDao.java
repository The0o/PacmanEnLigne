package dao;

import utils.Db;

import java.sql.*;

public class UserDao {

  public int createUser(String username, String passwordHash) throws SQLException {
    String sql = "INSERT INTO users(username, password_hash) VALUES (?, ?) RETURNING id";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, passwordHash);
      try (ResultSet rs = ps.executeQuery()) {
        rs.next();
        return rs.getInt("id");
      }
    }
  }

  public User findByUsername(String username) throws SQLException {
    String sql = "SELECT id, username, password_hash, role, is_active, failed_login_count FROM users WHERE username = ?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return new User(
          rs.getInt("id"),
          rs.getString("username"),
          rs.getString("password_hash"),
          rs.getString("role"),
          rs.getBoolean("is_active"),
          rs.getInt("failed_login_count")
        );
      }
    }
  }

  public void updateLoginSuccess(int userId) throws SQLException {
    String sql = "UPDATE users SET failed_login_count = 0, last_login_at = NOW(), updated_at = NOW() WHERE id = ?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.executeUpdate();
    }
  }

  public void incFailedLogin(int userId) throws SQLException {
    String sql = "UPDATE users SET failed_login_count = failed_login_count + 1, updated_at = NOW() WHERE id = ?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.executeUpdate();
    }
  }

  public static class User {
    public int id;
    public String username;
    public String passwordHash;
    public String role;
    public boolean isActive;
    public int failedLoginCount;

    public User(int id, String username, String passwordHash, String role, boolean isActive, int failedLoginCount) {
      this.id = id;
      this.username = username;
      this.passwordHash = passwordHash;
      this.role = role;
      this.isActive = isActive;
      this.failedLoginCount = failedLoginCount;
    }
  }
}