package database;

/**
 * Created by shuai9532 on 4/30/17.
 */
public class UserFromDB {
  String uuid;
  String username;
  public UserFromDB(String uuid, String username){
    this.uuid = uuid;
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public String getUuid() {
    return uuid;
  }

}
