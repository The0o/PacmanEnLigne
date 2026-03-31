package utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class HttpUtil {
    public static String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}

//package utils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.BufferedReader;
//import java.io.IOException;
//
//public class HttpUtil {
//  public static String readBody(HttpServletRequest request) throws IOException {
//      StringBuilder sb = new StringBuilder();
//      String line;
//
//      BufferedReader reader = request.getReader();
//      while ((line = reader.readLine()) != null) {
//          sb.append(line);
//      }
//      return sb.toString();
//  }
//}