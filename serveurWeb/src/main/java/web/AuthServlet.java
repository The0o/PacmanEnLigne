package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import dao.UserDao;
import utils.HttpUtil;
import utils.JsonUtil;
import utils.PasswordUtil;

import javax.servlet.http.*;
import java.sql.SQLException;


/**
 * Servlet implementation class AuthServlet
 */
@WebServlet("/api/auth/login")
public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	 private final UserDao userDao = new UserDao();

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

	      JsonObject out = new JsonObject();
	      out.addProperty("ok", true);
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
