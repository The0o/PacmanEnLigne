package web;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.JsonObject;
import utils.JsonUtil;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logout(req);
        resp.sendRedirect(req.getContextPath() + "/api/auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logout(req);

        if (isJsonRequest(req)) {
            JsonObject out = new JsonObject();
            out.addProperty("ok", true);
            out.addProperty("message", "logout success");
            JsonUtil.writeJson(resp, 200, out);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/api/auth/login");
    }

    private void logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private boolean isJsonRequest(HttpServletRequest req) {
        String contentType = req.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
}
