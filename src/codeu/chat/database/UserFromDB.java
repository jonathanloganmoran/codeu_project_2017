package codeu.chat.database;

import codeu.chat.common.Time;

/**
 * construct an user using the info retrieved from database
 */
public class UserFromDB {

  String uuid;
  String username;
  Time time;

  public UserFromDB(String uuid, String username, Time time){
    this.uuid = uuid;
    this.username = username;
    this.time = time;
  }

  public String getUsername() {
    return username;
  }

  public String getUuid() {
    return uuid;
  }

  public Time getTime() { return time;}
}
