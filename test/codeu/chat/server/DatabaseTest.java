package codeu.chat.server;

import static org.junit.Assert.*;

import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import database.Connector;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;


public final class DatabaseTest {

  private static final database.Connector con = new database.Connector();
  private static final RandomUuidGenerator uuidGenerator = new RandomUuidGenerator(
      Uuids.NULL, System.currentTimeMillis());
  @Test
  public void testAddUser() {
    System.out.println("DATABASE TEST RUN");
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
  public void testVerifyUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW,uuidGenerator.make().toString());
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
  }

  @Test
  public void testDeleteUser() {
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    con.addAccount(randomUN, randomPW,uuidGenerator.make().toString());
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.addAccount(randomUN, randomPW,uuidGenerator.make().toString()));
  }

  /*@Test
  public void testUpdatePassword() {
    //
  }*/
}
