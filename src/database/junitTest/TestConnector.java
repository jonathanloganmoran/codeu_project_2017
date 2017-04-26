package database.junitTest;
/**
 * Created by shuai9532 on 4/21/17.
 */
import database.Connector;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 * Add a couple round-trip tests. That is, I want to see you
 * have some private static final UUID = ... and one for USER_ID
 * and MESSAGE_TITLE and so on such that you re-use them through
 * all tests. Then, in a few tests at the end, make sure you can
 * addConversation and then retrieve that very same conversation,
 * checking for String.equals().
 */



public class TestConnector {
  Connector conn = new Connector();


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
    assertTrue(conn.addConversation("0000","1234","first conv"));
    assertTrue(conn.addMessage("1111","1234","0000","first message"));
    assertTrue(conn.deleteAccount("shuai"));
    assertFalse(conn.deleteAccount("none"));
    assertFalse(conn.verifyAccount("shuai","newpass"));
  }

  @Test
  public void testAddConversation(){
    assertFalse(conn.addConversation("235","1234","conversation2"));
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
