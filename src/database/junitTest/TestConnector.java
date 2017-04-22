package database.junitTest;
/**
 * Created by shuai9532 on 4/21/17.
 */
import database.Connector;
import database.ConversationDuplicateException;
import java.util.List;
import jdk.internal.cmm.SystemResourcePressureImpl;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
public class TestConnector {
  Connector conn = new Connector();

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
}
