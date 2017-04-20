package codeu.chat.util;

import java.util.regex.Pattern;

/**
 * Class used to determine whether or not certain inputs are valid for different fields throughout
 * the program
 */
public final class TextValidator {

  private static final Pattern VALID_USER_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
  private static final Pattern VALID_PASSWORD_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

  /**
   * @param username – the string to be validated
   * @return – whether or not the string was valid for username type input
   */
  public static boolean isValidUserName(String username) {
    if (username == null) {
      return false;
    } else if (username.length() != 0 && VALID_USER_NAME_PATTERN.matcher(username).matches()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param password – the string to be validated
   * @return – whether or not the string was valid for password type input
   */
  public static boolean isValidPassword(String password) {
    if (password == null) {
      return false;
    } else if (password.length() != 0 && VALID_PASSWORD_PATTERN.matcher(password).matches()) {
      return true;
    } else {
      return false;
    }
  }
}