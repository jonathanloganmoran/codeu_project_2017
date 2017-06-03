// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.client;

import java.util.*;

import codeu.chat.database.Connector;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;
import codeu.chat.util.Method;
import codeu.chat.util.TextValidator;

public final class ClientMessage {

  private final static Logger.Log LOG = Logger.newLog(ClientMessage.class);
  private final static int MESSAGE_MAX_COUNT = 100;
  private final static int MESSAGE_FETCH_COUNT = 5;

  private final Controller controller;
  private final View view;

  private Message current = null;
    
  private Conversation conversationHead;
  private final List<Message> conversationContents = new ArrayList<>();

  private final ClientUser userContext;
  private final ClientConversation conversationContext;

  public ClientMessage(Controller controller, View view, ClientUser userContext,
                       ClientConversation conversationContext) {
    this.controller = controller;
    this.view = view;
    this.userContext = userContext;
    this.conversationContext = conversationContext;
    this.conversationContext.setMessageContext(this);
  }

  // Validate the message body.
  public static boolean isValidBody(String body) {
    boolean clean = true;
    if ((body.length() <= 0) || (body.length() > 1024)) {
      clean = false;
    }
    else {
      if (!TextValidator.isValidMessage(body)) {
        clean = false;
      }
    } // TODO: check for invalid characters
    return clean;
  }

  public boolean hasCurrent() {
    return (current != null);
  }

  public Message getCurrent() {
    return current;
  }

  public void showCurrent() {
    printMessage(current, userContext);
  }

  public void resetCurrent(boolean replaceAll) {
    updateMessages(replaceAll);
  }
    
  public List<Message> getConversationContents(Conversation summary){
      
      if(summary == null) {return conversationContents;}
      Collection<Message> messageList = view.getMessages(summary.id);
    if(messageList != null) {
      for (Message message : messageList) {
        conversationContents.add(message);
      }
    }
    if (conversationHead == null || summary == null || !conversationHead.id.equals(summary.id)) {
      updateMessages(summary, true);
    }
    return conversationContents;
  }

  // For m-add command.
  public Message addMessage(Uuid author, Uuid conversation, String body) {
    final boolean validInputs = isValidBody(body) && (author != null) && (conversation != null);
    final Message message = (validInputs) ? controller.newMessage(author, conversation, body) : null;
    if (message == null) {
      System.out.format("Error: message not created - %s.\n",
          (validInputs) ? "server error" : "bad input value");
    } else {
      LOG.info("New message:, Author= %s UUID= %s", author, message.id);
    }
    return message;
  }

  // For m-list-all command.
  // Show all messages attached to the current conversation. This will balk if the conversation
  // has too many messages (use m-next and m-show instead).
  public void showAllMessages() {
    if (conversationContents.size() == 0) {
      System.out.println(" Current Conversation has no messages");
    } else {
      for (final Message m : conversationContents) {
        printMessage(m, userContext);
      }
    }
  }

  // For m-next command.
  // Accept an index (within the current stream) that indicates the next message to show.
  // Message 1 is the head of the Conversation's message chain.
  // Message -1 is the tail of the Conversation's message chain.
  public void selectMessage(int index) {
    Method.notImplemented();
  }

  // Processing for m-show command.
  // Accept an int for number of messages to attempt to show (1 by default).
  // Negative values go from newest to oldest.
  public void showMessages(int count) {
    for (final Message m : conversationContents) {
      printMessage(m, userContext);
    }
  }

  public void updateMessages(boolean replaceAll) {
    updateMessages(conversationContext.getCurrent(), replaceAll);
  }

  // Update the list of messages for the given conversation.
  // Currently rereads the entire message chain.
  public void updateMessages(Conversation conversation, boolean replaceAll) {
    if (conversation == null) {
      LOG.error("conversation argument is null - do nothing.");
      return;
    }
    else {
      LOG.info("Conversation: Title=\"%s\" UUID=%s first=%s last=%s\n",
          conversation.title, conversation.id);
 
        for (final Message msg : view.getMessages(conversation.id)) {
          conversationContents.add(msg);
      }
      LOG.info("Retrieved %d messages for conversation %s (%s).\n",
          conversationContents.size(), conversationHead.id, conversationHead.title);

      // Set current to first message of conversation.
      current = (conversationContents.size() > 0) ? conversationContents.get(0) : null;
    }
  }

  // Print Message.  User context is used to map from author UUID to name.
  public static void printMessage(Message m, ClientUser userContext) {
    if (m == null) {
      System.out.println("Null message.");
    } else {

      // Display author name if available.  Otherwise display the author UUID.
      final String authorName = (userContext == null) ? null : userContext.getName(m.author);

      System.out.format(" Author: %s   Id: %s created: %s\n   Body: %s\n",
          (authorName == null) ? m.author : authorName, m.id, m.creation, m.content);
    }
  }

  // Print Message outside of user context.
  public static void printMessage(Message m) {
    printMessage(m, null);
  }
}
