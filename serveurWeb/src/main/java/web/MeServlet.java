package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.JsonObject;
import utils.JsonUtil;
/**
 * Servlet implementation class MeServlet
 */
@WebServlet("/api/me")
public class MeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            JsonUtil.writeJson(resp, 401, error("not logged in"));
            return;
        }

        JsonObject out = new JsonObject();
        out.addProperty("userId", (Integer) session.getAttribute("userId"));
        out.addProperty("username", (String) session.getAttribute("username"));
        out.addProperty("role", (String) session.getAttribute("role"));
        JsonUtil.writeJson(resp, 200, out);
    }

    private JsonObject error(String msg) {
        JsonObject o = new JsonObject();
        o.addProperty("error", msg);
        return o;
    }

}
