package database;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.Uuid;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * JDBC connector is needed to connects java program to the database.
 * Description:
 * this is the database connector you will need to use to communicate with
 * the databased hosted on a remote machine.
 */

public class Connector {

  private BasicDataSource ds;
  final private static String USER = "User";
  final private static String CONVERSATION = "Conversation";
  final private static String MESSAGE = "Message";
  final private static String HOST_NAME = "ec2-176-34-225-252.eu-west-1.compute.amazonaws.com";
  final private static String DB_NAME = "CodeU_2017DB";
  /* SQL user queries */
  final static private String SQL_SELECT_USERNAMES = String.format("SELECT username FROM %s", USER);
  final static private String SQL_INSERT_ACCOUNT = String
      .format("INSERT INTO %s (username, password, salt, Uuid) VALUES(?,?,?,?)", USER);
  final static private String SQL_DROP = String.format("TRUNCATE TABLE %s", USER);
  final static private String SQL_SELECT_PASSWORD = String
      .format("SELECT password FROM %s WHERE username = ?", USER);
  final static private String SQL_UPDATE = String
      .format("UPDATE %s SET password = ? WHERE username = ?", USER);
  final static private String SQL_DELETE_USER = String
      .format("DELETE FROM %s WHERE username = ?", USER);
  final static private String SQL_SELECT_SALT = String
      .format("SELECT salt FROM %s WHERE username = ?", USER);
  /* SQL Conversation Queries */

  /* Encryption */
  private static final Random ram = new SecureRandom();
  static final private char[] CHARS = "1234567890-=qwertyuiopasdfghjkl,./nbvcxz".toCharArray();
  static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
      'd', 'e', 'f'};

  public Connector() {

    ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.jdbc.Driver");

    try (Scanner in = new Scanner(new FileReader("third_party/databaseInfo"))) {
      ds.setUrl(String.format("jdbc:mysql://%s:3306/%s", HOST_NAME, DB_NAME));
      ds.setUsername(in.next());
      ds.setPassword(in.next());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Generate salt for new user
   * @return salt  the code assigned to each password
   */
  private String generateSalt() {


    char[] arr = new char[8];
    // Generate the salt
    for (int i = 0; i < arr.length; i++) {
      int index = ram.nextInt(CHARS.length);
      arr[i] = CHARS[index];
    }
    return new String(arr);
  }

  /**
   * Public method that is used to encode the password
   * @param password plain text password
   * @param salt code assigned to each password
   * @return encoded password
   */
  public static String encryptPassword(String password, String salt) throws EncryptFailException{

    String formattedPassword = salt + password;
    StringBuilder code = new StringBuilder();
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      // Mess up the byte converted from formattedPassword.
      byte[] hashedBytes = sha.digest(formattedPassword.getBytes());

      for (int i = 0; i < hashedBytes.length; i++) {
        // Get each byte from input string.
        byte b = hashedBytes[i];
        // First half byte is mapped into char c in the hash table.
        char c = DIGITS[(b & 0xf0) >> 4];
        // Add the code to the string.
        code.append(c);
        // Second half is also mapped into hash table and value appends to the code.
        code.append(DIGITS[b & 0x0f]);
      }
      return code.toString();
    }
    catch (NoSuchAlgorithmException e) {
      throw new EncryptFailException("the password is not encrypted");
    }
  }

  /**
   * Print all the current usernames
   * @return a list of usernames
   */
  public synchronized List<String> getAllUsers() {

    List<String> userNames = new ArrayList<>();
    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement getUsers = conn.prepareStatement(SQL_SELECT_USERNAMES)) {
        try (ResultSet users = getUsers.executeQuery()) {
          while (users.next()) {
            userNames.add(users.getString("username"));
          }
          return userNames;
        }
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * AddAccount is to add the new account to the database.
   * @param username name of the account that is being added
   * @param password password of the account made by user
   * @param uuid id assigned to each user
   * @return true if the insertion is successful and complete; false, if the insertion fails
   */
    public synchronized boolean addAccount(String username, String password, String uuid) {

    String salt = generateSalt();
    try {
      String codedPassword = encryptPassword(password, salt);

      try (Connection conn = ds.getConnection()) {
        try (PreparedStatement insertAccount = conn.prepareStatement(SQL_INSERT_ACCOUNT)) {
          insertAccount.setString(1, username);
          insertAccount.setString(2, codedPassword);
          insertAccount.setString(3, salt);
          insertAccount.setString(4, uuid);
          insertAccount.executeUpdate();
          return true;
        }
      }
    }
    catch (EncryptFailException e){
      return false;
    }
    catch (SQLException e) {
      return false;
    }
  }

  /**
   * Clean all the data inside the database
   * @return true if the data has been cleaned
   */
  public synchronized boolean dropAllAccounts() {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement dropAll = conn.prepareStatement(SQL_DROP)) {
        dropAll.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Acquire the existing salt from database
   * @param username username from account
   * @return salt
   */
  public String acquireSalt(String username) throws UserNotFoundException,SaltCannotRetrieveException {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement selectSalt = conn.prepareStatement(SQL_SELECT_SALT)) {
        selectSalt.setString(1, username);
        try (ResultSet resultSalt = selectSalt.executeQuery()) {
          if (resultSalt.next()) {
            // Get the stored salt from database
            return resultSalt.getString(1);
          }
          else{
            throw new UserNotFoundException("The User is not found");
          }
        }
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      throw new SaltCannotRetrieveException("the salt cannot be retrieved");
    }
  }

  /**
   * Verify if the account username and password input by users match what has been recorded in
   *  database
   * @param username the username that is being verified
   * @param password the password that is used to compare to the one in database
   * @return true if the account is verified, else, false, if the account is not valid
   */
  public synchronized boolean verifyAccount(String username, String password) {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement selectPassword = conn.prepareStatement(SQL_SELECT_PASSWORD)) {
        // The account exists, check password.
        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()) {
            // Get the stored password from database.
            String passwordInDB = resultPassword.getString(1);
            // Encrypt password:
            try {
              String salt = acquireSalt(username);
              String codedPassword = encryptPassword(password, salt);
              // Verify;
              if (!passwordInDB.equals(codedPassword)) {
                //the password does not match
                return false;
              }
              return true;
            }
            catch (UserNotFoundException e){return false;}
            catch(SaltCannotRetrieveException e){return false;}
            catch (EncryptFailException e){return false;}
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
    return false;
  }


  /** Verify if the account username is in the database
   *
   * @param username the username that is being verified
   * @return true if the account is valid, if the account is not valid
   */
  @SuppressWarnings("unused, and not sure if we will need this -> might delete")
  public synchronized boolean AccountExists(String username) {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement selectPassword = conn.prepareStatement(SQL_SELECT_PASSWORD)) {
        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()){
            return true;
          }
          return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete the existing account; deletion requires the user to sign in first
   * @param username the account name that is being dropped
   * @return false if the deletion fails; true if succeeds
   */
  public synchronized boolean deleteAccount(String username) {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement deleteAccount = conn.prepareStatement(SQL_DELETE_USER)) {
        deleteAccount.setString(1, username);
        if (deleteAccount.executeUpdate() == 1)
          return true;
        else
          return false;
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Update the existing account's password we assume that the change of password can only happen
   * after the user has logged in
   * @param username the username of account whose password is being updated
   * @param newPassword the new password
   * @return true if the update succeeds, else, false;
   */
  public synchronized boolean updatePassword(String username, String newPassword) {

    try (Connection conn = ds.getConnection()) {
      String assignedSalt = "";
      try (PreparedStatement selectSalt = conn.prepareStatement(SQL_SELECT_SALT)){
        selectSalt.setString(1,username);

        try (ResultSet salt = selectSalt.executeQuery()){
          if(salt.next()){
            assignedSalt = salt.toString();
          }
          else{
            return false;
          }
        }
      }
      try (PreparedStatement update = conn.prepareStatement(SQL_UPDATE)) {
        try {
          String encryptedPassword = encryptPassword(newPassword, assignedSalt);
          update.setString(1, encryptedPassword);
          update.setString(2, username);
          update.executeUpdate();
          return true;
        }
        catch(EncryptFailException e){
          return false;
        }
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /** When all has been done with database, call close to end the connection.
   *  can restart by creating a new instance of connector
   */
  public synchronized void closeConnection() {
    try {
      ds.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
