package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonUtil {
    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void writeJson(HttpServletResponse response, int statusCode, Object data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(GSON.toJson(data));
    }
}
