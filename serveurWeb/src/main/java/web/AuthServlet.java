package web;

import java.io.IOException;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setAttribute("pageTitle", "Connexion");
        try {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Credentials credentials = extractCredentials(req);
            if (credentials == null) {
                writeError(req, resp, 400, "Missing field: username or password", "/login.jsp");
                return;
            }

            String username = credentials.username;
            String password = credentials.password;

            UserDao.User user = userDao.findByUsername(username);
            if (user == null) {
                writeError(req, resp, 401, "invalid credentials", "/login.jsp");
                return;
            }

            if (!user.isActive) {
                writeError(req, resp, 403, "account disabled", "/login.jsp");
                return;
            }

            boolean ok = PasswordUtil.verify(password, user.passwordHash);
            if (!ok) {
                userDao.incFailedLogin(user.id);
                writeError(req, resp, 401, "invalid credentials", "/login.jsp");
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

            if (isJsonRequest(req)) {
                JsonUtil.writeJson(resp, 200, out);
            } else {
                resp.sendRedirect(req.getContextPath() + "/home.jsp");
            }

        } catch (SQLException e) {
            writeError(req, resp, 500, "database error", "/login.jsp");
        } catch (Exception e) {
            writeError(req, resp, 400, "invalid json", "/login.jsp");
        }
    }

    private Credentials extractCredentials(HttpServletRequest req) throws IOException {
        if (isJsonRequest(req)) {
            JsonObject obj = JsonUtil.GSON.fromJson(HttpUtil.readBody(req), JsonObject.class);
            if (obj == null || !obj.has("username") || !obj.has("password")) {
                return null;
            }
            return new Credentials(
                obj.get("username").getAsString(),
                obj.get("password").getAsString()
            );
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null || password == null) {
            return null;
        }
        return new Credentials(username, password);
    }

    private void writeError(HttpServletRequest req, HttpServletResponse resp, int status, String msg, String viewPath)
            throws IOException {
        if (isJsonRequest(req)) {
            JsonUtil.writeJson(resp, status, error(msg));
            return;
        }

        req.setAttribute("error", msg);
        req.setAttribute("username", req.getParameter("username"));
        resp.setStatus(status);
        try {
            req.getRequestDispatcher(viewPath).forward(req, resp);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isJsonRequest(HttpServletRequest req) {
        String contentType = req.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }

    private JsonObject error(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("error", msg);
        return o;
    }

    private static class Credentials {
        private final String username;
        private final String password;

        private Credentials(String username, String password) {
            this.username = username == null ? null : username.trim().toLowerCase();
            this.password = password;
        }
    }
}
