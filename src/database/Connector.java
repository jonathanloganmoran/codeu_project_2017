package database;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
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
  private static String user;
  private static String conversation;
  private static String message;
  /*constants*/
  final private String SQL_SELECT_USERNAMES = String.format("SELECT username FROM "+user);
  final private String SQL_INSERT_ACCOUNT = String.format("INSERT INTO %s (username, password) VALUES(?,?)",user);
  final private String SQL_DROP = String.format("TRUNCATE TABLE %s", user);
  final private String SQL_SELECT_PASSWORD = String.format("SELECT PASSSWORD FROM %s WHERE username = ?",user);
  final private String SQL_UPDATE = String.format("UPDATE %s SET password = ? WHERE username = ?",user);
  final private String SQL_DELETE_USER = String.format("DELETE FROM %s WHERE username = ?",user);

  public Connector() {

    ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.jdbc.Driver");

    try(Scanner in = new Scanner(new FileReader("databaseInfo"))){
      user = in.next();
      conversation = in.next();
      message = in.next();

      ds.setUrl(String.format("jdbc:mysql:%s//:3306/%s", in.next(),in.next()));
      ds.setUsername(in.next());
      ds.setPassword(in.next());

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Conversation
   */
  boolean addConversation(){
    return true;
  }

  List<Conversation>getAllConversations(){
    return null;
  }

  /**
   * Messages
   */
  List<Message>getAllMessages(Conversation con){
    return null;
  }

  boolean writeMessage(Message message){
   return true;
  }
  /**
   * print all the current useNnames
   *
   */
  public List<String> getAllUsers() {

    LinkedList<String> userNames = new LinkedList<>();
    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement getUsers = conn.prepareStatement(SQL_SELECT_USERNAMES)) {
        try (ResultSet users = getUsers.executeQuery()) {
          while (users.next()) {
            userNames.add(users.getString("username"));
          }
          return userNames;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return  null;
    }

  }

  /**
   * AddAccount is to add the new account to the database.
   * 
   * @param username
   * @param password
   * @return true if the insertion is successful and complete; false, if the insertion fails
   */
  public boolean addAccount(String username, String password) {

    try (Connection conn = ds.getConnection()) {
      try(PreparedStatement insertAccount = conn.prepareStatement(SQL_INSERT_ACCOUNT)) {
        insertAccount.setString(1, username);
        insertAccount.setString(2, password);
        insertAccount.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * clean all the data inside the database
   *
   * @return true if the data has been cleaned
   */
  public boolean dropAllAccounts() {

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
   * verify if the account username and password input by users match what has been recorded in
   * database
   * 
   * @param username
   * @param password
   * @return true if the account is verified, else, false, if the account is not valid
   */
  public boolean verifyAccount(String username, String password) {


    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement selectPassword = conn.prepareStatement(SQL_SELECT_PASSWORD)) {
        // the account exists, check password
        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()) {
            // get the stored password from database
            String passwordInDB = resultPassword.getString(1);
            // password does not match
            if (!passwordInDB.equals(password)) {
              System.err.println("the password does not match");
              return false;
            }
            System.out.println("the account exists");
            return true;
          }
        }
      }
    } catch (SQLException e) {e.printStackTrace();return false;}
    System.err.println("the account does not exist");
    return false;
  }

  /**
   * delete the existing account; deletion requires the user to sign in first
   * 
   * @param username
   * @return false if the deletion fails; true if succeeds
   */
  public boolean deleteAccount(String username) {


    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement deleteAccount = conn.prepareStatement(SQL_DELETE_USER)) {
        deleteAccount.setString(1, username);
        if (deleteAccount.executeUpdate() == 1) return true;
        else return false;
      }
    } catch (SQLException e) {e.printStackTrace();return false;}
  }

  /**
   * update the existing account's password we assume that the change of password can only happen
   * after the user has logged in
   * 
   * @param username
   * @param newPassword
   * @return true if the update succeeds, else, false;
   */

  public boolean updatePassword(String username, String newPassword) {

    try (Connection conn = ds.getConnection()) {
      try (PreparedStatement update = conn.prepareStatement(SQL_UPDATE)){
        update.setString(1,newPassword);
        update.setString(2,username);
        update.executeUpdate();
        System.out.println("the account has been updated");
        return true;
      }
    } catch (SQLException e) {e.printStackTrace(); return false;}
  }

  /**
   * when all has been done with database, call close to end the connection.
   * can restart by creating a new instance of connector
   */
  public void closeConnection() {
    try {
      ds.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
