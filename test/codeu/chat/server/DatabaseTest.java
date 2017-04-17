package codeu.chat.server;

import static org.junit.Assert.*;

import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import database.Connector;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


//Created to test the implementation of the methods in database.Connector.java
public final class DatabaseTest {


  private static final database.Connector con = new database.Connector();
  private static final RandomUuidGenerator uuidGenerator = new RandomUuidGenerator(
      Uuids.NULL, System.currentTimeMillis());

  @Test
  public void testAddAndDeleteUser() {
    Connector con = new Connector();
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(
        con.addAccount(randomUN, randomPW, uuidGenerator.make().toString())
        && con.addAccount(
            UUID.randomUUID().toString(),UUID.randomUUID().toString(), uuidGenerator.make().toString())
        && con.addAccount(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), uuidGenerator.make().toString())
        && con.addAccount(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), uuidGenerator.make().toString())
        && con.addAccount(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), uuidGenerator.make().toString())
        );

  }

  @Test
  public void testAddVerifyAndDeleteUser() {
    Connector con = new Connector();
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW,uuidGenerator.make().toString());
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
    assertFalse(con.verifyAccount(randomUN, randomPW + " "));
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
    con.closeConnection();
  }

  @Test
  public void testUserExists() {
    Connector con = new Connector();
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW,uuidGenerator.make().toString());
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.addAccount(randomUN, randomPW,uuidGenerator.make().toString()));
  }


  @Test
  public void testGetAllUsers(){
    Connector con = new Connector();
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    List<String> addedUserNames = new LinkedList<>();
    int iterationsToTest = 2;
    for(int i=0; i<iterationsToTest; i++) {
      addedUserNames.add(randomUN + i);
      assertTrue(con.addAccount(randomUN + i, randomPW));
    }
    List<String> allUsers = con.getAllUsers();
    assertTrue(allUsers.containsAll(addedUserNames));
    for(int i=0; i<iterationsToTest; i++) {
      assertTrue(con.deleteAccount(randomUN + i));
    }
    con.closeConnection();
  }

  @Test
  public void testUpdatePassword() {
    Connector con = new Connector();
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW));
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.updatePassword(randomUN, randomPW+"1"));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.verifyAccount(randomUN, randomPW+"1"));
    assertTrue(con.deleteAccount(randomUN));
    con.closeConnection();
  }
}
