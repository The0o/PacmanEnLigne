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

    public List<LeaderboardItem> getLeaderboard(int limit) throws SQLException {
        String sql =
            "SELECT u.username, MAX(s.score) AS best_score " +
            "FROM users u " +
            "JOIN scores s ON u.id = s.user_id " +
            "GROUP BY u.id, u.username " +
            "ORDER BY best_score DESC " +
            "LIMIT ?";

        List<LeaderboardItem> items = new ArrayList<>();

        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

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

    public static class LeaderboardItem {
        public String username;
        public int bestScore;

        public LeaderboardItem(String username, int bestScore) {
            this.username = username;
            this.bestScore = bestScore;
        }
    }
}