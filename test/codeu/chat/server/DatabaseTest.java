package codeu.chat.server;

import static org.junit.Assert.*;

import database.Connector;
import java.util.UUID;
import org.junit.Test;


public final class DatabaseTest {

  private Connector con = new Connector();

  @Test
  public void testAddAndDeleteUser() {
    System.out.println("DATABASE TEST RUN");
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW));
    assertTrue(con.deleteAccount(randomUN));
    randomUN = UUID.randomUUID().toString();
    randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW));
    assertTrue(con.deleteAccount(randomUN));
  }

  @Test
  public void testAddVerifyAndDeleteUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW));
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
    assertFalse(con.verifyAccount(randomUN, randomPW + " "));
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
  }

  /*
  @Test
  public void testDeleteUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW);
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.addAccount(randomUN, randomPW));
  }

  @Test
  public void testUpdatePassword() {
    //
  }*/
}
