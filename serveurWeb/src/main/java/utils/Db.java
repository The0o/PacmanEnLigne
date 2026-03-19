package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
	//local docker
//	private static final String URL  = "jdbc:postgresql://localhost:5433/testdb";
//	private static final String USER = "testuser";
//	private static final String PASS = "testpass";

//en ligne supabase
	private static final String URL  = "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:6543/postgres?user=postgres.prvshcoswpquvihbdhov&password=ProjetPacman26";
	private static final String USER = "postgres.prvshcoswpquvihbdhov";
	private static final String PASS = "ProjetPacman26";

  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("PostgreSQL Driver not found", e);
    }
  }

  private Db() {}

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }
}