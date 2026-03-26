package dao;

import utils.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDao {

    public int createScore(int userId, int score) throws SQLException {
        String sql = "INSERT INTO scores(user_id, score) VALUES (?, ?) RETURNING id";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, score);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id");
            }
        }
    }
    
    public List<LeaderboardItem> getLeaderboard(int limit, int offset) throws SQLException {
        String sql =
            "SELECT u.username, MAX(s.score) AS best_score " +
            "FROM users u " +
            "JOIN scores s ON u.id = s.user_id " +
            "GROUP BY u.id, u.username " +
            "ORDER BY best_score DESC, u.username ASC " +
            "LIMIT ? OFFSET ?";

        List<LeaderboardItem> items = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new LeaderboardItem(
                        rs.getString("username"),
                        rs.getInt("best_score")
                    ));
                }
            }
        }

        return items;
    }

    public int countLeaderboardUsers() throws SQLException {
        String sql =
            "SELECT COUNT(*) AS total " +
            "FROM ( " +
            "    SELECT u.id " +
            "    FROM users u " +
            "    JOIN scores s ON u.id = s.user_id " +
            "    GROUP BY u.id " +
            ") t";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getInt("total");
        }
    }
    
    public List<ScoreHistoryItem> getScoreHistoryByUserId(int userId, int limit, int offset) throws SQLException {
        String sql =
            "SELECT score, created_at " +
            "FROM scores " +
            "WHERE user_id = ? " +
            "ORDER BY created_at DESC " +
            "LIMIT ? OFFSET ?";

        List<ScoreHistoryItem> items = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    items.add(new ScoreHistoryItem(
                        rs.getInt("score"),
                        ts != null ? ts.toString() : null
                    ));
                }
            }
        }

        return items;
    }

    public int countScoresByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM scores WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("total");
            }
        }
    }

    public Integer getBestScoreByUserId(int userId) throws SQLException {
        String sql = "SELECT MAX(score) AS best_score FROM scores WHERE user_id = ?";

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int best = rs.getInt("best_score");
                if (rs.wasNull()) {
                    return null;
                }
                return best;
            }
        }
    }
  
    public static class LeaderboardItem {
        public String username;
        public int bestScore;

        public LeaderboardItem(String username, int bestScore) {
            this.username = username;
            this.bestScore = bestScore;
        }
    }
    
    public static class ScoreHistoryItem {
        public int score;
        public String createdAt;

        public ScoreHistoryItem(int score, String createdAt) {
            this.score = score;
            this.createdAt = createdAt;
        }
    }
}