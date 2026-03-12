package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import dao.ScoreDao;
import utils.HttpUtil;
import utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet implementation class ScoresServlet
 */
@WebServlet("/api/scores")
public class ScoresServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ScoreDao scoreDao = new ScoreDao();

    public ScoresServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //verifier session
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                JsonUtil.writeJson(resp, 401, error("you must login first"));
                return;
            }

            Integer userId = (Integer) session.getAttribute("userId");

            JsonObject obj = JsonUtil.GSON.fromJson(HttpUtil.readBody(req), JsonObject.class);

            if (obj == null || !obj.has("score")) {
                JsonUtil.writeJson(resp, 400, error("Missing field: score"));
                return;
            }

            int score = obj.get("score").getAsInt();

            if (score < 0) {
                JsonUtil.writeJson(resp, 400, error("score must be >= 0"));
                return;
            }

            int id = scoreDao.createScore(userId, score);

            JsonObject out = new JsonObject();
            out.addProperty("id", id);
            out.addProperty("userId", userId);
            out.addProperty("score", score);

            JsonUtil.writeJson(resp, 201, out);

        } catch (SQLException e) {
            JsonUtil.writeJson(resp, 500, error("database error"));
        } catch (Exception e) {
            JsonUtil.writeJson(resp, 400, error("invalid json"));
        }
    }

    private JsonObject error(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("error", msg);
        return o;
    }
}


