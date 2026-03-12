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

import java.sql.SQLException;

/**
 * Servlet implementation class UsersServlet
 */
//Register: http://localhost:8080/serveurWeb/api/users
@WebServlet("/api/users")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServlet() {
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

	      String username = obj.get("username").getAsString().trim();
	      String password = obj.get("password").getAsString();

	      if (username.isEmpty()) {
	        JsonUtil.writeJson(resp, 400, error("username is empty"));
	        return;
	      }
	      if (password == null || password.length() < 6) {
	        JsonUtil.writeJson(resp, 400, error("password must be at least 6 chars"));
	        return;
	      }

	      // Normalize username
	      username = username.toLowerCase();

	      String hash = PasswordUtil.hash(password);
	      int id = userDao.createUser(username, hash);

	      JsonObject out = new JsonObject();
	      out.addProperty("id", id);
	      out.addProperty("username", username);
	      out.addProperty("role", "USER");
	      JsonUtil.writeJson(resp, 201, out);

	    } catch (SQLException e) {
	      if ("23505".equals(e.getSQLState())) {
	        JsonUtil.writeJson(resp, 409, error("username already exists"));
	      } else {
	        JsonUtil.writeJson(resp, 500, error("database error"));
	      }
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
