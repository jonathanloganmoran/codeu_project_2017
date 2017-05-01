package codeu.chat.server;

import static org.junit.Assert.*;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import database.Connector;
import database.ConversationFromDB;
import database.MessageFromDB;
import database.UserFromDB;
import java.util.Random;
import java.util.UUID;
import org.junit.Test;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

// Created to test the implementation of the methods in database.Connector.java

/*
 *  It is important that the usernames and passwords added are alphanumeric only.
 *  Do not try to add an entire UUID as a username. It messes with the testing
 *  of the front-end. Leave them substringed to only numbers without dashes.
 */

public final class DatabaseTest {

  private static final Connector con = new database.Connector();
  private static final RandomUuidGenerator uuidGenerator = new RandomUuidGenerator(
      Uuids.NULL, System.currentTimeMillis());
  private static final String UUID_USER = "987654321";
  private static final String PASSWORD = "0986283158712";
  private static final String USERNAME = "shuaih";
  private static final String UUID_MESSAGE_ONE = "3012937";
  private static final String MESSAGE_ONE = "this is the first message";
  private static final String UUID_MESSAGE_TWO = "301293734";
  private static final String MESSAGE_TWO = "this is the second message";
  private static final String UUID_CONVERSATION_ONE = "31203810";
  private static final String CONVERSATION_TITLE = "the first conve";
  private static final String RANDOM_UN = UUID.randomUUID().toString().substring(0,6);
  private static final String RANDOM_PW = UUID.randomUUID().toString().substring(0,6);
  private static final String RANDOM_UID = uuidGenerator.make().toString();
  private static final int ITERATIONS_TO_TEST = 1;

  @Test
  public void test(){
    assertTrue(con.addAccount("shuai", "hs6898528", "1234"));
    assertTrue(con.verifyAccount("shuai", "hs6898528"));
    assertFalse(con.verifyAccount("shuai","hs"));
    assertFalse(con.verifyAccount("shuai", ""));
    assertFalse(con.verifyAccount("none","hs6898528"));

    assertTrue(con.updatePassword("shuai","newpass"));

    assertTrue(con.verifyAccount("shuai", "newpass"));
    assertFalse(con.verifyAccount("shuai","hs"));
    assertFalse(con.verifyAccount("shuai", ""));
    assertFalse(con.verifyAccount("none","newpass"));

    assertTrue(con.addConversation("0000","1234",CONVERSATION_TITLE));
    assertTrue(con.addMessage("1111","1234","0000",MESSAGE_ONE));
    assertTrue(con.getConversations().get(0).getTitle().equals(CONVERSATION_TITLE));
    assertTrue(con.getMessages("0000").get(0).getMessage().equals(MESSAGE_ONE));

    assertTrue(con.deleteAccount("shuai"));
    assertFalse(con.deleteAccount("none"));
    assertFalse(con.verifyAccount("shuai","newpass"));

  }

  @Test
  public void secondTest(){
    assertTrue(con.addAccount(USERNAME,PASSWORD,UUID_USER));
    assertTrue(con.addConversation(UUID_CONVERSATION_ONE,UUID_USER, CONVERSATION_TITLE));
    assertTrue(con.addMessage(UUID_MESSAGE_ONE, UUID_USER, UUID_CONVERSATION_ONE, MESSAGE_ONE));
    assertTrue(con.addMessage(UUID_MESSAGE_TWO, UUID_USER, UUID_CONVERSATION_ONE, MESSAGE_TWO));
    assertTrue(con.getMessages(UUID_CONVERSATION_ONE).get(0).equals(MESSAGE_ONE));
    assertTrue(con.getMessages(UUID_CONVERSATION_ONE).get(1).equals(MESSAGE_TWO));
    assertTrue(con.updatePassword(USERNAME, "1"));
    assertFalse(con.verifyAccount(USERNAME, PASSWORD));
    assertTrue(con.verifyAccount(USERNAME, "1"));
  }

  @Test
  public void testAddMessage(){
    //message added to the same table
    boolean success = con.addMessage("1234","1234","235", "later");
    assertTrue(success);
    success = con.addMessage("1155","1234","235", "later ");
    assertTrue(success);
  }

  @Test
  public void testGetMessage(){
    List<MessageFromDB> messageList = con.getMessages("235");
    for(MessageFromDB str : messageList){
      System.out.println(str.getMessage());
    }
    assertFalse(messageList.isEmpty());
  }

  @Test
    public void testAddAndDeleteUser(){
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        assertTrue(con.addAccount(RANDOM_UN +i, RANDOM_PW, RANDOM_UID +i));
      }
      assertFalse(con.addAccount((RANDOM_UN +(ITERATIONS_TO_TEST -1)),
          RANDOM_PW, RANDOM_UID +(ITERATIONS_TO_TEST -1)));
      for(int i=0; i< ITERATIONS_TO_TEST; i++) {
        assertTrue(con.deleteAccount(RANDOM_UN +i));
      }
    assertTrue(con.addAccount("user1", "haha", "1234"));
    assertTrue(con.addAccount("user3", "haha2", "1236"));
    assertTrue(con.addAccount("user4", "haha3", "1237"));
    assertTrue(con.addAccount("user5", "haha4", "1238"));
    assertTrue(con.addAccount("user6", "haha5", "1239"));
    assertFalse(con.addAccount("user1","das","12345"));

    String randomUN = UUID.randomUUID().toString().substring(0,6);
    String randomPW = UUID.randomUUID().toString().substring(0,6);
    assertTrue(con.addAccount(randomUN, randomPW, uuidGenerator.make().toString()));
    assertTrue(con.addAccount(randomUN+"1", randomPW, uuidGenerator.make().toString()));
    assertTrue(con.addAccount(randomUN+"2", randomPW, uuidGenerator.make().toString()));
    assertTrue(con.addAccount(randomUN+"3", randomPW, uuidGenerator.make().toString()));
    assertTrue(con.addAccount(randomUN+"4", randomPW, uuidGenerator.make().toString()));

    assertTrue(con.deleteAccount(randomUN));
    assertTrue(con.deleteAccount(randomUN+"1"));
    assertTrue(con.deleteAccount(randomUN+"2"));
    assertTrue(con.deleteAccount(randomUN+"3"));
    assertTrue(con.deleteAccount(randomUN+"4"));
    }

  @Test
  public void testAddVerifyAndDeleteUser() {
    assertTrue(con.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID));
    assertTrue(con.verifyAccount(RANDOM_UN, RANDOM_PW));
    assertFalse(con.verifyAccount(RANDOM_UN, RANDOM_PW + "1"));
    assertFalse(con.verifyAccount(RANDOM_UN, RANDOM_PW + " "));
    assertTrue(con.deleteAccount(RANDOM_UN));
    assertFalse(con.verifyAccount(RANDOM_UN, RANDOM_PW));
    assertFalse(con.verifyAccount(RANDOM_UN, RANDOM_PW + "1"));
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW,uuidGenerator.make().toString()));
    assertTrue(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
    assertFalse(con.verifyAccount(randomUN, randomPW + " "));
    assertTrue(con.deleteAccount(randomUN));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertFalse(con.verifyAccount(randomUN, randomPW + "1"));
  }

  @Test
  public void testUserExists() {
    con.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID);
    assertFalse(con.addAccount(RANDOM_UN,RANDOM_PW,RANDOM_UID));
    assertTrue(con.deleteAccount(RANDOM_UN));
  }


  @Test
  public void testGetConversation(){
    con.addConversation("323","1238","conversation3");
    List<ConversationFromDB> conversationList = con.getConversations();
    for(ConversationFromDB str : conversationList){
      System.out.println(str.getTitle());
    }
    assertFalse(conversationList.isEmpty());
  }

  @Test
  public void testUpdatePassword() {
    String password = "hello";
    String username = "shuaill";
    String uuid = "12346";
    assertTrue(con.addAccount(username, password, uuid));
    assertTrue(con.verifyAccount(username, password));
    assertTrue(con.updatePassword(username, password+1));
    assertTrue(con.verifyAccount(username, password+1));
    String randomUN = UUID.randomUUID().toString();
    String randomPW = UUID.randomUUID().toString();
    assertTrue(con.addAccount(randomUN, randomPW,uuidGenerator.make().toString()));
    assertTrue(con.verifyAccount(randomUN, randomPW));assertTrue(con.updatePassword(randomUN, randomPW+"1"));
    assertFalse(con.verifyAccount(randomUN, randomPW));
    assertTrue(con.verifyAccount(randomUN,randomPW+"1"));
    assertTrue(con.deleteAccount(randomUN));

    assertTrue(con.addAccount(RANDOM_UN, RANDOM_PW, RANDOM_UID));
    assertTrue(con.verifyAccount(RANDOM_UN, RANDOM_PW));
    assertTrue(con.updatePassword(RANDOM_UN, RANDOM_PW +"1"));
    assertFalse(con.verifyAccount(RANDOM_UN, RANDOM_PW));
    assertTrue(con.verifyAccount(RANDOM_UN, RANDOM_PW +"1"));
    assertTrue(con.deleteAccount(RANDOM_UN));
  }

  @Test
  public  void testDrop(){
    assertTrue(con.dropAllAccounts());
  }
}
