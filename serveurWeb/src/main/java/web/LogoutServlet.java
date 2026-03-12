package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.*;

import com.google.gson.JsonObject;
import utils.JsonUtil;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/api/auth/logout")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
  @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        JsonObject out = new JsonObject();
        out.addProperty("ok", true);
        out.addProperty("message", "logout success");

        JsonUtil.writeJson(resp, 200, out);
    }

}
