package codeu.chat.database;

import codeu.chat.common.Time;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import codeu.chat.server.View;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * JDBC connector is needed to connects java program to the codeu.chat.database.
 * Description:
 * this is the codeu.chat.database connector you will need to use to communicate with
 * the databased hosted on a remote machine.
 */

/**
 * The conversation will remain, even though the creator has deleted the account;
 * same as the messages sent by the user.
 * In order to have different time for different messages for the same user, it will
 * be safe to store message every time when user inputs a message
 */

public class Connector {

  /* Database */
  private BasicDataSource dataSource;
  private static final String USER = "User";
  private static final String CONVERSATION = "Conversation";
  private static final String MESSAGE = "Message";
  private static final String HOST_NAME = "ec2-176-34-225-252.eu-west-1.compute.amazonaws.com";
  private static final String DB_NAME = "CodeU_2017DB";

  /* SQL User Queries */
  private static final String SQL_SELECT_USERS = String.format("SELECT * FROM %s", USER);
  private static final String SQL_SELECT_USER = String.format("SELECT * FROM %s WHERE Uuid = ?", USER);
  private static final String SQL_INSERT_ACCOUNT = String
      .format("INSERT INTO %s (username, password, salt, Uuid) VALUES(?,?,?,?)", USER);
  private static final String SQL_DROP_USER = String.format("TRUNCATE TABLE %s", USER);
  private static final String SQL_SELECT_PASSWORD = String
      .format("SELECT password FROM %s WHERE username = ?", USER);
  private static final String SQL_UPDATE = String
      .format("UPDATE %s SET password = ? WHERE username = ?", USER);
  private static final String SQL_DELETE_USER = String
      .format("DELETE FROM %s WHERE username = ?", USER);
  private static final String SQL_SELECT_SALT = String
      .format("SELECT salt FROM %s WHERE username = ?", USER);
  private static final String SQL_SELECT_ID = String
      .format("SELECT Uuid FROM %s WHERE username = ?", USER);
  private static final String SQL_SELECT_USER_CREATION_TIME = String.
      format("SELECT creation_time FROM %s WHERE Uuid = ?", USER);
  private static final String SQL_SELECT_USER_BY_NAME= String.format("SELECT * FROM %s WHERE username = ?", USER);

  /* SQL Conversation Queries */
  private static final String SQL_INSERT_CONVERSATON = String
      .format("INSERT INTO %s (Uuid, id_user, title) VALUES(?,?,?)", CONVERSATION);
  private static final String SQL_SELECT_CONVERSATIONS = String.
      format("SELECT * FROM %s ORDER BY creation_time ASC", CONVERSATION);
  private static final String SQL_SELECT_CONVERSATION = String.format("SELECT * FROM %s WHERE Uuid = ?", CONVERSATION);
  private static final String SQL_DELETE_CONVERSATION_BY_ID = String
      .format("DELETE FROM %s WHERE id_user= ?", CONVERSATION);
  private static final String SQL_DROP_CONVERSATION = String.format("TRUNCATE TABLE %s", CONVERSATION);
  private static final String SQL_SELECT_CONVERSATION_CREATION_TIME = String.
          format("SELECT creation_time FROM %s WHERE Uuid = ?", CONVERSATION);
  private static final String SQL_SELECT_CONVERSATION_BY_TITLE = String
          .format("SELECT * FROM %s WHERE title = ?", CONVERSATION);

  /* SQL Message Queries */
  private static final String SQL_SELECT_MESSAGE_CREATION_TIME = String.
          format("SELECT creation_time FROM %s WHERE Uuid = ?", MESSAGE);
  private static final String SQL_INSERT_MESSAGE = String
      .format("INSERT INTO %s (Uuid, content, id_user, id_conversation) VALUES(?,?,?,?)", MESSAGE);
  private static final String SQL_SELECT_MESSAGES = String.format(
      "SELECT * FROM %s WHERE id_conversation = ? ORDER BY creation_time ASC", MESSAGE);
  private static final String SQL_SELECT_MESSAGE = String.format(
          "SELECT * FROM %s WHERE id_conversation = ?", MESSAGE);
  private static final String SQL_DELETE_MESSGES_BY_ID = String
      .format("DELETE FROM %s WHERE id_user = ?", MESSAGE);
  private static final String SQL_DROP_MESSAGE = String.format("TRUNCATE TABLE %s", MESSAGE);

  /* Encryption */
  private static final Random rand = new SecureRandom();
  private static final char[] CHARS = "1234567890abcdefghijklmnopqrstuvwxyz-=1,./;'[]".toCharArray();
  private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
      'd', 'e', 'f'};

  /* Log*/
  private static final Logger LOGGER = Logger.getLogger( Connector.class.getName() );

  public Connector() {
    dataSource = new BasicDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    try (Scanner in = new Scanner(new FileReader("third_party/databaseInfo"))) {
      dataSource.setUrl(String.format("jdbc:mysql://%s:3306/%s", HOST_NAME, DB_NAME));
      dataSource.setUsername(in.next());
      dataSource.setPassword(in.next());
    }
    catch (FileNotFoundException e) {
      LOGGER.log( Level.SEVERE, "the file that contains account of datavase does not exist");
    }
  }

  /**
   * Add conversation into the table in codeu.chat.database
   * @param uuid id of the conversation
   * @param user_id user who owns the conversation
   * @param title the name of the conversation, unique
   * @return true if the conversation is added to the table
   */
  public synchronized boolean addConversation(String uuid, String user_id, String title){
    LOGGER.log(Level.WARNING, "Arguments: " + uuid + ", "+user_id+", "+title);
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement addConversation = conn.prepareStatement(SQL_INSERT_CONVERSATON)) {
        addConversation.setString(1,uuid);
        addConversation.setString(2,user_id);
        addConversation.setString(3,title);
        addConversation.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      LOGGER.log( Level.WARNING, "you might have stored a conversation with the same name");
      return false;
    }
  }

  /**
   * Insert message into the given conversation table
   * @param uuid id of message
   * @param user_id user who writes the message
   * @param conversation_id the conversation where the message resides
   * @param content the content of the message
   * @return true if the message is added
   */
  public synchronized boolean addMessage(String uuid, String user_id, String conversation_id, String content){

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement addMessage = conn.prepareStatement(SQL_INSERT_MESSAGE)) {
        addMessage.setString(1, uuid);
        addMessage.setString(2, content);
        addMessage.setString(3, user_id);
        addMessage.setString(4, conversation_id);
        addMessage.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      LOGGER.log( Level.FINE, "something wrong with the connection");
      return false;
    }
  }

  /**
   * AddAccount is to add the new account to the codeu.chat.database.
   * @param username name of the account that is being added
   * @param password password of the account made by user
   * @param uuid id assigned to each user
   * @return true if the insertion is successful and complete; false, if the insertion fails
   */
  public synchronized boolean addAccount(String username, String password, String uuid) {

    String salt = generateSalt();
    try {
      String codedPassword = encryptPassword(password, salt);

      try (Connection conn = dataSource.getConnection()) {
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
      LOGGER.log( Level.SEVERE, "the encryption for the password fails" );
      return false;
    }
    catch (SQLException e) {
      LOGGER.log( Level.FINE, "SQL exception is caught");
      return false;
    }
  }

  /**
   * Acquire all the conversations ordered by creation time from codeu.chat.database
   * @return List<String> a list of conversations
   */
  public synchronized List<ConversationFromDB> getConversations(){
    List<ConversationFromDB> conversationList = new ArrayList<>();
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement getConversation = conn.prepareStatement(SQL_SELECT_CONVERSATIONS)) {
        ResultSet conversation = getConversation.executeQuery();
        while (conversation.next()) {
          String title = conversation.getString("title");
          String uuid = conversation.getString("Uuid");
          String author = conversation.getString("id_user");
          Timestamp time = conversation.getTimestamp("creation_time");
          conversationList.add(new ConversationFromDB(uuid, author,title,Time.fromMs(time.getTime())));
        }
        return conversationList;
      }
    }
    catch (SQLException e) {
      return conversationList;
    }
  }

  /**
   * Acquire all the messages ordered by cretaion time in the conversation , given conversation id
   * @param conversation_id
   * @return List<String> a list of messages
   */
  public synchronized List<MessageFromDB> getMessages(String conversation_id){
    List<MessageFromDB> messageList = new ArrayList<>();
    try(Connection conn = dataSource.getConnection()) {
      try (PreparedStatement getMessage = conn.prepareStatement(SQL_SELECT_MESSAGES)) {
        getMessage.setString(1,conversation_id);
        ResultSet messages = getMessage.executeQuery();
        while (messages.next()) {
          String uuid = messages.getString("Uuid");
          String user = messages.getString("id_user");
          String conversation = messages.getString("id_conversation");
          String conetnt = messages.getString("content");
          Timestamp time = messages.getTimestamp("creation_time");
          messageList.add(new MessageFromDB(uuid,conversation,user,conetnt,Time.fromMs(time.getTime())));
        }
        return messageList;
      }
    }
    catch (SQLException e) {
        return messageList;
    }
  }

  /**
   * Print all the current usernames
   * @return a list of usernames
   */
  public synchronized List<UserFromDB> getAllUsers() {

    List<UserFromDB> userNames = new ArrayList<>();

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement getUsers = conn.prepareStatement(SQL_SELECT_USERS)) {
        try (ResultSet users = getUsers.executeQuery()) {
          while (users.next()) {
            String username = users.getString("username");
            String uuid = users.getString("Uuid");
            Timestamp time = users.getTimestamp("creation_time");
            userNames.add(new UserFromDB(uuid,username,Time.fromMs(time.getTime())));
          }
          return userNames;
        }
      }
    }
    catch (SQLException e){
      return userNames;
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
      int index = rand.nextInt(CHARS.length);
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
  private static String encryptPassword(String password, String salt) throws EncryptFailException{

    String formattedPassword = salt + password;
    StringBuilder code = new StringBuilder();
    try {

      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      byte[] hashedBytes = sha.digest(formattedPassword.getBytes());

      for (int i = 0; i < hashedBytes.length; i++) {
        byte b = hashedBytes[i];
        char c = DIGITS[(b & 0xf0) >> 4];
        code.append(c);
        code.append(DIGITS[b & 0x0f]);
      }
      return code.toString();
    }
    catch (NoSuchAlgorithmException e) {
      throw new EncryptFailException("the password is not encrypted");
    }
  }

  /**
   * Clean all the data inside the codeu.chat.database
   * @return true if the data has been cleaned
   */
  public synchronized boolean dropAllAccounts() {

    try (Connection conn = dataSource.getConnection()) {
      if(dropAllMessage() && dropAllConversation()){
        try (PreparedStatement dropAll = conn.prepareStatement(SQL_DROP_USER)) {
          dropAll.executeUpdate();
          return true;
        }
      }
      return false;
    }
    catch (SQLException e) {
      return false;
    }
  }

  /*
   * Drop all conversations
   */
  private synchronized boolean dropAllConversation() {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement dropAll = conn.prepareStatement(SQL_DROP_CONVERSATION)) {
        dropAll.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      return false;
    }
  }

  /*
   * Drop all messages
   */
  private synchronized boolean dropAllMessage() {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement dropAll = conn.prepareStatement(SQL_DROP_MESSAGE)) {
        dropAll.executeUpdate();
        return true;
      }
    }
    catch (SQLException e) {
      return false;
    }
  }

  /*
   * Drop all database content
   */
  private synchronized boolean dropEverything() {
    return dropAllAccounts() && dropAllConversation() && dropAllMessage();
  }

  /*
   * Acquire salt
   */
  private String acquireSalt(String username) throws UserNotFoundException,SaltCannotRetrieveException {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement selectSalt = conn.prepareStatement(SQL_SELECT_SALT)) {
        selectSalt.setString(1, username);
        try (ResultSet resultSalt = selectSalt.executeQuery()) {
          if (resultSalt.next()) {
            // Get the stored salt from codeu.chat.database
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
   * codeu.chat.database
   * @param username the username that is being verified
   * @param password the password that is used to compare to the one in codeu.chat.database
   * @return true if the account is verified, else, false, if the account is not valid
   */
  public synchronized boolean verifyAccount(String username, String password) {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement selectPassword = conn.prepareStatement(SQL_SELECT_PASSWORD)) {

        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()) {
            String passwordInDB = resultPassword.getString(1);
            try {
              String salt = acquireSalt(username);
              String codedPassword = encryptPassword(password, salt);
              if (!passwordInDB.equals(codedPassword)) {
                LOGGER.log( Level.SEVERE, "the password does not match" );
                return false;
              }
              return true;
            }
            catch (UserNotFoundException e) {
              LOGGER.log( Level.FINE, "The account does not exist");
              return false;
            }
            catch (SaltCannotRetrieveException e) {
              LOGGER.log( Level.FINE, "the password conversion does not succeed");
              return false;
            }
            catch (EncryptFailException e) {
              return false;
            }
          }
        }
      }
    } catch (SQLException e) {
      return false;
    }
    return false;
  }
  
  /**
   * Verify if the account username is in the codeu.chat.database
   * @param username the username that is being verified
   * @return true if the account is valid, if the account is not valid
   */
  public synchronized boolean accountExists(String username) {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement selectPassword = conn.prepareStatement(SQL_SELECT_PASSWORD)) {
        selectPassword.setString(1, username);
        try (ResultSet resultPassword = selectPassword.executeQuery()) {
          if (resultPassword.next()){
            return true;
          }
          else
            return false;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Delete message when the user account gets deleted
   */
  private boolean deleteMessage(String user_id) {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement deleteMessage = conn.prepareStatement(SQL_DELETE_MESSGES_BY_ID)) {
        deleteMessage.setString(1, user_id);
        deleteMessage.executeUpdate();
          return true;
      }
    } catch (SQLException e) {
      return false;
    }
  }

  /**
   * Delete conversation
   */
  private boolean deleteConversation(String user_id) {

    try (Connection conn = dataSource.getConnection()) {
      try(PreparedStatement deleteConversation = conn.prepareStatement(SQL_DELETE_CONVERSATION_BY_ID)){
        deleteConversation.setString(1,user_id);
        deleteConversation.executeUpdate();
          return true;
      }
    } catch (SQLException e){
      return false;
    }
  }

  /**
   * Delete the existing account; deletion requires the user to sign in first
   * Deletion of the account represents the deletion of all the activities the user involved
   * @param username the account name that is being dropped
   * @return false if the deletion fails; true if succeeds
   */
  public synchronized boolean deleteAccount(String username) {

    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement deleteAccount = conn.prepareStatement(SQL_DELETE_USER)) {
        deleteAccount.setString(1, username);
        if(deleteAccount.executeUpdate() > 0)
          return true;
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

    try (Connection conn = dataSource.getConnection()) {
      String assignedSalt = "";
      try (PreparedStatement selectSalt = conn.prepareStatement(SQL_SELECT_SALT)){
        selectSalt.setString(1,username);

        try (ResultSet salt = selectSalt.executeQuery()){
          if(salt.next()){
            assignedSalt = salt.getString(1);
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
      return false;
    }
  }

  /**
   * find the creation time of the conversation
   * @param id pass in conversation id to retrieve the time
   * @return time created
   */
  public Time getConv_creationTime(String id){
      try (Connection conn = dataSource.getConnection()) {
        PreparedStatement time = conn.prepareStatement(
            SQL_SELECT_CONVERSATION_CREATION_TIME);
        time.setString(1,id);
        ResultSet result = time.executeQuery();
        while (result.next()) {
          return Time.fromMs(result.getTimestamp("creation_time").getTime());
        }
      }
      catch (SQLException e) {
        LOGGER.log( Level.FINE, "error occurred when converting the time");
      }
      return null;
    }

  /**
   * find the creation time of the message
   * @param id pass in message id to retrieve the time
   * @return time created
   */
  public Time getMessage_creationTime(String id){
      try (Connection conn = dataSource.getConnection()) {
        PreparedStatement time = conn.prepareStatement(
            SQL_SELECT_MESSAGE_CREATION_TIME);
        time.setString(1,id);
        ResultSet result = time.executeQuery();
        while (result.next()) {
          return Time.fromMs(result.getTimestamp("creation_time").getTime());
        }
      }
      catch (SQLException e) {
        LOGGER.log( Level.FINE, "error occurred when converting the time");
      }
      return null;
    }

  /**
   * find the creation time for user
   * @param id pass in user id to retrieve time
   * @return time created
   */
  public Time getUser_creationTime(String id){
      try (Connection conn = dataSource.getConnection()) {
        PreparedStatement time = conn.prepareStatement(
            SQL_SELECT_CONVERSATION_CREATION_TIME);
        time.setString(1,id);
        ResultSet result = time.executeQuery();
        while (result.next()) {
          LOGGER.log(Level.WARNING, "THE TIME SHOULD NOT BE NULL");
          return Time.fromMs(result.getTimestamp("creation_time").getTime());
        }
      }
      catch (SQLException e) {
        LOGGER.log( Level.FINE, "error occurred when converting the time");
      }
      return null;
  }

  /**
   * return an UserFromDB object
   * @param uuid get user info from DB through user id
   * @return UserFromDB
   */
  public UserFromDB getUser(String uuid){
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement getConversation = conn.prepareStatement(SQL_SELECT_USER)) {
          getConversation.setString(1,uuid);
          ResultSet user = getConversation.executeQuery();
          while (user.next()) {
            String username = user.getString("username");
            Timestamp time = user.getTimestamp("creation_time");
            return new UserFromDB(uuid,username,Time.fromMs(time.getTime()));
          }
        }
      }
      catch (SQLException e) {
        return null;
      }
      return null;
  }

  /**
   * return an ConversationFromDB object
   * @param uuid get convo info from DB through convo id
   * @return ConversationFromDB
   */
  public ConversationFromDB getConversation(String uuid){
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement getConversation = conn.prepareStatement(SQL_SELECT_CONVERSATION)) {
          getConversation.setString(1,uuid);
          ResultSet conversation = getConversation.executeQuery();
          while (conversation.next()) {
            String title = conversation.getString("title");
            String author = conversation.getString("id_user");
            Timestamp time = conversation.getTimestamp("creation_time");
            return new ConversationFromDB(uuid, author, title, Time.fromMs(time.getTime()));
          }
          return null;
        }
      }
      catch (SQLException e) {
        return null;
      }
  }

  /**
   * return an MessageFromDB object
   * @param uuid get message info from DB through message id
   * @return MessageFromDB
   */
  public MessageFromDB getMessage(String uuid){
      try (Connection conn = dataSource.getConnection()) {
        try (PreparedStatement message = conn.prepareStatement(SQL_SELECT_CONVERSATION)) {
          message.setString(1,uuid);
          ResultSet getMessage = message.executeQuery();
          while (getMessage.next()) {
            String content = getMessage.getString("content");
            String author = getMessage.getString("id_user");
            String conversation = getMessage.getString("id_conversation");
            Timestamp time = getMessage.getTimestamp("creation_time");
            return new MessageFromDB(uuid,conversation, author, content, Time.fromMs(time.getTime()));
          }
        }
      }
      catch (SQLException e) {
        return null;
      }
      return null;
  }

  /**
   * return an UserFromDB object
   * @param username get user info from DB through username
   * @return UserFromDB
   */
  public UserFromDB getUserByName(String username){
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement getUser = conn.prepareStatement(SQL_SELECT_USER_BY_NAME)) {
        getUser.setString(1,username);
        ResultSet user = getUser.executeQuery();
        while (user.next()) {
          String userid = user.getString("Uuid");
          String name = user.getString("username");
          Timestamp time = user.getTimestamp("creation_time");
          System.out.println("CONNECTOR 745: Found User: "+name);
          return new UserFromDB(userid,name, Time.fromMs(time.getTime()));
        }
        System.out.println("CONNECTOR 748: RETURNING NULL");
        return null;
      }
    }
    catch (SQLException e) {
      System.out.println("CONNECTOR 753: RETURNING NULL");
      return null;
    }
  }

  /**
   * return an ConversationFromDB object
   * @param title get convo info from DB through convo title
   * @return ConversationFromDB
   */
  public ConversationFromDB getConversationByTitle(String title){
    try (Connection conn = dataSource.getConnection()) {
      try (PreparedStatement getConversation = conn.prepareStatement(SQL_SELECT_CONVERSATION_BY_TITLE)) {
        getConversation.setString(1,title);
        ResultSet conversation = getConversation.executeQuery();
        while (conversation.next()) {
          String conId = conversation.getString("Uuid");
          String author = conversation.getString("id_user");
          Timestamp time = conversation.getTimestamp("creation_time");
          return new ConversationFromDB(conId, author, title, Time.fromMs(time.getTime()));
        }
        return null;
      }
    }
    catch (SQLException e) {
      return null;
    }
  }

  /**
   * When all has been done with codeu.chat.database, call close to end the connection.
   * can restart by creating a new instance of connector
   */
  public synchronized void closeConnection() {
    try {
      dataSource.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
