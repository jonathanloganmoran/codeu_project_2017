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

// Created to test the implementation of the methods in database.Connector.java
public final class DatabaseTest {

  private static final database.Connector CONNECTOR = new database.Connector();
  private static final RandomUuidGenerator RANDOM_UUID_GENERATOR = new RandomUuidGenerator(
      Uuids.NULL, System.currentTimeMillis());
  /*
  *  It is important that the usernames and passwords added are alphanumeric only.
  *  Do not try to add an entire UUID as a username. It messes with the testing
  *  of the front-end. Leave them substringed to only numbers without dashes.
  */
  private static final String RANDOM_UN = UUID.randomUUID().toString().substring(0,6);
  private static final String RANDOM_PW = UUID.randomUUID().toString().substring(0,6);
  private static final String RANDOM_UID = RANDOM_UUID_GENERATOR.make().toString();
  private static final int ITERATIONS_TO_TEST = 1;

    @Test
    public void testAddAndDeleteUser() {
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        assertTrue(CONNECTOR.addAccount(RANDOM_UN +i, RANDOM_PW, RANDOM_UID +i));
      }
      assertFalse(CONNECTOR.addAccount((RANDOM_UN +(ITERATIONS_TO_TEST -1)),
          RANDOM_PW, RANDOM_UID +(ITERATIONS_TO_TEST -1)));
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        assertTrue(CONNECTOR.deleteAccount(RANDOM_UN +i));
      }
    }

    @Test
    public void testAddVerifyAndDeleteUser() {
      assertTrue(CONNECTOR.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID));
      assertTrue(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW));
      assertFalse(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW + "1"));
      assertFalse(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW + " "));
      assertTrue(CONNECTOR.deleteAccount(RANDOM_UN));
      assertFalse(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW));
      assertFalse(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW + "1"));
    }

    @Test
    public void testUserExists() {
      CONNECTOR.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID);
      assertTrue(CONNECTOR.accountExists(RANDOM_UN));
      assertTrue(CONNECTOR.deleteAccount(RANDOM_UN));
    }

    @Test
    public void testGetAllUsers(){
      List<String> addedUserNames = new LinkedList<>();
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        addedUserNames.add(RANDOM_UN + i);
        assertTrue(CONNECTOR.addAccount(RANDOM_UN + i, RANDOM_PW, RANDOM_UID + i));
      }
      List<String> allUsers = CONNECTOR.getAllUsers();
      assertTrue(allUsers.containsAll(addedUserNames));
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        assertTrue(CONNECTOR.deleteAccount(RANDOM_UN + i));
      }
    }

    @Test
    public void testUpdatePassword() {
      assertTrue(CONNECTOR.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID));
      assertTrue(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW));
      assertTrue(CONNECTOR.updatePassword(RANDOM_UN, RANDOM_PW +"1"));
      assertFalse(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW));
      assertTrue(CONNECTOR.verifyAccount(RANDOM_UN, RANDOM_PW +"1"));
      assertTrue(CONNECTOR.deleteAccount(RANDOM_UN));
    }
}
