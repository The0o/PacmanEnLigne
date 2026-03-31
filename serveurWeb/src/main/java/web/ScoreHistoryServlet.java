package web;


import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.*;

import com.google.gson.JsonObject;

import dao.ScoreDao;
import utils.JsonUtil;
/**
 * Servlet implementation class ScoreHistoryServlet
 */
@WebServlet("/api/scores/history")
public class ScoreHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ScoreDao scoreDao = new ScoreDao();

    public ScoreHistoryServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                JsonUtil.writeJson(resp, 401, error("you must login first"));
                return;
            }

            Integer userId = (Integer) session.getAttribute("userId");
            String currentUser = (String) session.getAttribute("username");

            int limit = 10;
            int offset = 0;

            String limitStr = req.getParameter("limit");
            String offsetStr = req.getParameter("offset");

            if (limitStr != null) {
                try {
                    limit = Integer.parseInt(limitStr);
                } catch (NumberFormatException e) {
                    limit = 10;
                }
            }

            if (offsetStr != null) {
                try {
                    offset = Integer.parseInt(offsetStr);
                } catch (NumberFormatException e) {
                    offset = 0;
                }
            }

            if (limit <= 0) limit = 10;
            if (limit > 100) limit = 100;
            if (offset < 0) offset = 0;

            List<ScoreDao.ScoreHistoryItem> items = scoreDao.getScoreHistoryByUserId(userId, limit, offset);
            int totalScores = scoreDao.countScoresByUserId(userId);
            Integer bestScore = scoreDao.getBestScoreByUserId(userId);

            JsonObject out = new JsonObject();
            out.addProperty("currentUser", currentUser);
            out.addProperty("limit", limit);
            out.addProperty("offset", offset);
            out.addProperty("totalScores", totalScores);

            if (bestScore != null) {
                out.addProperty("bestScore", bestScore);
            } else {
                out.add("bestScore", null);
            }

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
