package web;

import java.io.IOException;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			request.getRequestDispatcher("/register.jsp").forward(request, response);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	private final UserDao userDao = new UserDao();

	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    try {
	      Credentials credentials = extractCredentials(req);
	      if (credentials == null) {
	        writeError(req, resp, 400, "Missing field: username or password");
	        return;
	      }

	      String username = credentials.username;
	      String password = credentials.password;

	      if (username.isEmpty()) {
	        writeError(req, resp, 400, "username is empty");
	        return;
	      }
	      if (password == null || password.length() < 6) {
	        writeError(req, resp, 400, "password must be at least 6 chars");
	        return;
	      }

	      String hash = PasswordUtil.hash(password);
	      int id = userDao.createUser(username, hash);

	      JsonObject out = new JsonObject();
	      out.addProperty("id", id);
	      out.addProperty("username", username);
	      out.addProperty("role", "USER");
	      if (isJsonRequest(req)) {
	        JsonUtil.writeJson(resp, 201, out);
	      } else {
	        resp.sendRedirect(req.getContextPath() + "/api/auth/login?registered=1");
	      }

	    } catch (SQLException e) {
	     e.printStackTrace();
	      if ("23505".equals(e.getSQLState())) {
	        writeError(req, resp, 409, "username already exists");
	      } else {
	        writeError(req, resp, 500, "database error");
	      }
	    } catch (Exception e) {
	      writeError(req, resp, 400, "invalid json");
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

	  private void writeError(HttpServletRequest req, HttpServletResponse resp, int status, String msg) throws IOException {
		if (isJsonRequest(req)) {
			JsonUtil.writeJson(resp, status, error(msg));
			return;
		}

		req.setAttribute("error", msg);
		req.setAttribute("username", req.getParameter("username"));
		resp.setStatus(status);
		try {
			req.getRequestDispatcher("/register.jsp").forward(req, resp);
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
