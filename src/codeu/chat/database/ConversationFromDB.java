package codeu.chat.database;

import codeu.chat.common.Time;

/**
 * Created by shuai9532 on 4/30/17.
 */
public class ConversationFromDB {

  String uuid;
  String authorid;
  String title;
  Time time;
  ConversationFromDB(String uuid, String authorUuid, String title, Time time){
    this.uuid = uuid;
    this.authorid = authorUuid;
    this.title = title;
    this.time = time;
  }

  public String getUuid(){return uuid;}

  public String getAuthorid(){
    return authorid;
  }

  public String getTitle(){
    return title;
  }

  public Time getTime() {
    return time;
  }
}
