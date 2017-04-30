package database.junitTest;
/**
 * Created by shuai9532 on 4/21/17.
 */
import database.Connector;
import java.util.List;
import javax.print.DocFlavor.STRING;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class TestConnector {
  Connector conn = new Connector();
  private static final String UUID_USER = "987654321";
  private static final String PASSWORD = "0986283158712";
  private static final String USERNAME = "shuaih";
  private static final String UUID_MESSAGE_ONE = "3012937";
  private static final String MESSAGE_ONE = "this is the first message";
  private static final String UUID_MESSAGE_TWO = "301293734";
  private static final String MESSAGE_TWO = "this is the second message";
  private static final String UUID_CONVERSATION_ONE = "31203810";
  private static final String CONVERSATION_TITLE = "the first conve";

  @Test
  public void test(){
    assertTrue(conn.addAccount("shuai", "hs6898528", "1234"));
    assertTrue(conn.verifyAccount("shuai", "hs6898528"));
    assertFalse(conn.verifyAccount("shuai","hs"));
    assertFalse(conn.verifyAccount("shuai", ""));
    assertFalse(conn.verifyAccount("none","hs6898528"));

    assertTrue(conn.updatePassword("shuai","newpass"));

    assertTrue(conn.verifyAccount("shuai", "newpass"));
    assertFalse(conn.verifyAccount("shuai","hs"));
    assertFalse(conn.verifyAccount("shuai", ""));
    assertFalse(conn.verifyAccount("none","newpass"));

    assertTrue(conn.addConversation("0000","1234",CONVERSATION_TITLE));
    assertTrue(conn.addMessage("1111","1234","0000",MESSAGE_ONE));
    assertTrue(conn.getConversations().get(0).equals(CONVERSATION_TITLE));
    assertTrue(conn.getMessages("0000").get(0).equals(MESSAGE_ONE));

    assertTrue(conn.deleteAccount("shuai"));
    assertFalse(conn.deleteAccount("none"));
    assertFalse(conn.verifyAccount("shuai","newpass"));

  }

  @Test
  public void secondTest(){
    assertTrue(conn.addAccount(USERNAME,PASSWORD,UUID_USER));
    assertTrue(conn.addConversation(UUID_CONVERSATION_ONE,UUID_USER, CONVERSATION_TITLE));
    assertTrue(conn.addMessage(UUID_MESSAGE_ONE, UUID_USER, UUID_CONVERSATION_ONE, MESSAGE_ONE));
    assertTrue(conn.addMessage(UUID_MESSAGE_TWO, UUID_USER, UUID_CONVERSATION_ONE, MESSAGE_TWO));
    assertTrue(conn.getMessages(UUID_CONVERSATION_ONE).get(0).equals(MESSAGE_ONE));
    assertTrue(conn.getMessages(UUID_CONVERSATION_ONE).get(1).equals(MESSAGE_TWO));
    assertTrue(conn.updatePassword(USERNAME, "1"));
    assertFalse(conn.verifyAccount(USERNAME, PASSWORD));
    assertTrue(conn.verifyAccount(USERNAME, "1"));
  }

  @Test
  public void testAddMessage(){
    //message added to the same table
    boolean success = conn.addMessage("1234","1234","235", "later");
    assertTrue(success);
    success = conn.addMessage("1155","1234","235", "later ");
    assertTrue(success);
  }

  @Test
  public void testGetMessage(){
    List<String> messageList = conn.getMessages("235");
    for(String str : messageList){
      System.out.println(str);
    }
    assertFalse(messageList.isEmpty());
  }
  
  @Test
  public void testGetConversation(){
    conn.addConversation("323","1238","conversation3");
    List<String> conversationList = conn.getConversations();
    for(String str : conversationList){
      System.out.println(str);
    }
    assertFalse(conversationList.isEmpty());
  }

  @Test
  public  void testDrop(){
    assertTrue(conn.dropAllAccounts());
  }
}
