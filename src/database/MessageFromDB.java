package database;

/**
 * Created by shuai9532 on 4/30/17.
 */
public class MessageFromDB {

  String uuid;
  String id_conversation;
  String id_user;
  String message;

  MessageFromDB(String uuid, String id_conversation, String id_user, String message){
    this.uuid = uuid;
    this.id_conversation = id_conversation;
    this.id_user = id_user;
    this.message = message;
  }

  public String getId_conversation() {
    return id_conversation;
  }

  public String getId_user() {
    return id_user;
  }

  public String getMessage() {
    return message;
  }

  public String getUuid() {
    return uuid;
  }
}
