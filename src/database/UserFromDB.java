package database;

import codeu.chat.common.Time;

/**
 * Created by shuai9532 on 4/30/17.
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
