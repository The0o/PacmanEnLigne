package utils;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
  private PasswordUtil() {}

  // cost 10-12
  private static final int COST = 12;

  public static String hash(String plainPassword) {
    if (plainPassword == null || plainPassword.isEmpty()) {
      throw new IllegalArgumentException("Password empty");
    }
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt(COST));
  }

  public static boolean verify(String plainPassword, String passwordHash) {
    if (plainPassword == null || passwordHash == null) return false;
    return BCrypt.checkpw(plainPassword, passwordHash);
  }
}