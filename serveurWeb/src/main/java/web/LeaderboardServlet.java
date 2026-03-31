package web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import dao.ScoreDao;
import utils.JsonUtil;

import java.sql.SQLException;
import java.util.List;

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
	        int offset = 0;

	        String limitStr = req.getParameter("limit");
	        String offsetStr = req.getParameter("offset");

	        if (limitStr != null) {
	            try {
	                limit = Integer.parseInt(limitStr);
	            } catch (NumberFormatException ignored) {
	                limit = 10;
	            }
	        }

	        if (offsetStr != null) {
	            try {
	                offset = Integer.parseInt(offsetStr);
	            } catch (NumberFormatException ignored) {
	                offset = 0;
	            }
	        }

	        if (limit <= 0) limit = 10;
	        if (limit > 100) limit = 100;
	        if (offset < 0) offset = 0;

	        try {
	            List<ScoreDao.LeaderboardItem> items = scoreDao.getLeaderboard(limit, offset);
	            int totalUsers = scoreDao.countLeaderboardUsers();

	            JsonArray arr = new JsonArray();
	            for (int i = 0; i < items.size(); i++) {
	                ScoreDao.LeaderboardItem item = items.get(i);

	                JsonObject obj = new JsonObject();
	                obj.addProperty("rank", offset + i + 1);
	                obj.addProperty("username", item.username);
	                obj.addProperty("bestScore", item.bestScore);

	                arr.add(obj);
	            }

	            JsonObject out = new JsonObject();
	            out.addProperty("limit", limit);
	            out.addProperty("offset", offset);
	            out.addProperty("totalUsers", totalUsers);
	            out.addProperty("count", items.size());
	            out.addProperty("hasMore", offset + items.size() < totalUsers);
	            out.add("items", arr);

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
