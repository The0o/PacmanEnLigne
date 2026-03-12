package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import dao.UserDao;
import utils.HttpUtil;
import utils.JsonUtil;
import utils.PasswordUtil;

import java.sql.SQLException;

// http://localhost:8080/serveurWeb/api/auth/login
@WebServlet("/api/auth/login")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();

    public AuthServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonObject obj = JsonUtil.GSON.fromJson(HttpUtil.readBody(req), JsonObject.class);

            if (obj == null || !obj.has("username") || !obj.has("password")) {
                JsonUtil.writeJson(resp, 400, error("Missing field: username or password"));
                return;
            }

            String username = obj.get("username").getAsString().trim().toLowerCase();
            String password = obj.get("password").getAsString();

            UserDao.User user = userDao.findByUsername(username);
            if (user == null) {
                JsonUtil.writeJson(resp, 401, error("invalid credentials"));
                return;
            }

            if (!user.isActive) {
                JsonUtil.writeJson(resp, 403, error("account disabled"));
                return;
            }

            boolean ok = PasswordUtil.verify(password, user.passwordHash);
            if (!ok) {
                userDao.incFailedLogin(user.id);
                JsonUtil.writeJson(resp, 401, error("invalid credentials"));
                return;
            }

            userDao.updateLoginSuccess(user.id);

            // creer session apres login success
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.id);
            session.setAttribute("username", user.username);
            session.setAttribute("role", user.role);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            JsonObject out = new JsonObject();
            out.addProperty("ok", true);
            out.addProperty("message", "login success");
            out.addProperty("userId", user.id);
            out.addProperty("username", user.username);
            out.addProperty("role", user.role);

            JsonUtil.writeJson(resp, 200, out);

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