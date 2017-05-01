package database;

/**
 * Created by shuai9532 on 4/30/17.
 */
public class ConversationFromDB {

  String uuid;
  String authorid;
  String title;
  ConversationFromDB(String uuid, String authorUuid, String title){
    this.uuid = uuid;
    this.authorid = authorUuid;
    this.title = title;
  }

  public String getUuid(){
    return uuid;
  }

  public String getAuthorid(){
    return authorid;
  }

  public String getTitle(){
    return title;
  }
}
