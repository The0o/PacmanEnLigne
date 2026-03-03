package utils;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

public final class HttpUtil {
  private HttpUtil() {}

  public static String readBody(HttpServletRequest req) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = req.getReader()) {
      String line;
      while ((line = br.readLine()) != null) sb.append(line);
    }
    return sb.toString();
  }
}