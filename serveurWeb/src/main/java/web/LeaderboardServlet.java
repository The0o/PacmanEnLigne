package web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import dao.ScoreDao;
import utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet implementation class LeaderboardServlet
 */
@WebServlet("/api/leaderboard")
public class LeaderboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	 private final ScoreDao scoreDao = new ScoreDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int limit = 10;

        String limitStr = req.getParameter("limit");
        if (limitStr != null) {
            try {
                limit = Integer.parseInt(limitStr);
            } catch (NumberFormatException e) {
                limit = 10;
            }
        }

        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;

        try {
            List<ScoreDao.LeaderboardItem> items = scoreDao.getLeaderboard(limit);

            JsonObject out = new JsonObject();
            out.addProperty("limit", limit);
            out.add("items", JsonUtil.GSON.toJsonTree(items));

            JsonUtil.writeJson(resp, 200, out);

        } catch (SQLException e) {
            JsonUtil.writeJson(resp, 500, error("database error"));
        } catch (Exception e) {
            JsonUtil.writeJson(resp, 500, error("server error"));
        }
    }
    
    private JsonObject error(String msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("error", msg);
        return obj;
    }
}
