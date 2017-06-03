package codeu.chat.database;

import codeu.chat.common.Time;

/**
 * construct a message using the info retrieved from database
 */
public class MessageFromDB {

  String uuid;
  String id_conversation;
  String id_user;
  String message;
  Time time;

  MessageFromDB(String uuid, String id_conversation, String id_user, String message, Time time){
    this.uuid = uuid;
    this.id_conversation = id_conversation;
    this.id_user = id_user;
    this.message = message;
    this.time = time;
  }

  public String getId_conversation() {return id_conversation;}

  public String getId_user() {
    return id_user;
  }

  public String getMessage() {
    return message;
  }

  public String getUuid() {
    return uuid;
  }

  public Time getTime() {return time;}
}
