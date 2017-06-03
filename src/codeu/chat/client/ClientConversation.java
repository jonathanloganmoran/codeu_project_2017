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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import codeu.chat.database.Connector;
import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.Uuid;
import codeu.chat.util.Logger;
import codeu.chat.util.Method;

public final class ClientConversation {

  private final static Logger.Log LOG = Logger.newLog(ClientConversation.class);

  private final Controller controller;
  private final View view;
  private static final Connector connector = new Connector();

  private Conversation currentConversation = null;

  private final ClientUser userContext;
  private ClientMessage messageContext = null;

  // This is the set of conversations known to the server.
  private final Map<Uuid, Conversation> conversationsMap = new HashMap<>();


  public ClientConversation(Controller controller, View view, ClientUser userContext) {
    this.controller = controller;
    this.view = view;
    this.userContext = userContext;
  }

  public void setMessageContext(ClientMessage messageContext) {
    this.messageContext = messageContext;
  }

  // Validate the title of the conversation
  static public boolean isValidTitle(String title) {
    boolean clean = true;
    if ((title.length() <= 0) || (title.length() > 64)) {
      clean = false;
    } else {

      // TODO: check for invalid characters

    }
    return clean;
  }

  public boolean hasCurrent() {
    return (currentConversation != null);
  }

  public Conversation getCurrent() {
    return currentConversation;
  }

  public Uuid getCurrentId() { return (currentConversation != null) ? currentConversation.id : null; }

  public void showCurrent() {
    printConversation(currentConversation, userContext);
  }

  public Conversation startConversation(String title, Uuid owner) {
    final boolean validInputs = isValidTitle(title);

    final Conversation conv = (validInputs) ? controller.newConversation(title, owner) : null;
    System.out.println("CC92: Conversation tried");
    if (conv == null) {
      System.out.println("CC94: Conversation is null");
      System.out.format("Error: conversation not created - %s.\n",
          (validInputs) ? "server failure" : "bad input value");
      return conv;
    } else {
      currentConversation = conv;
      LOG.info("New conversation: Title= \"%s\" UUID= %s", conv.title, conv.id);
      return conv;

    }
  }

  public void setCurrent(Conversation conv) { currentConversation = conv; }

  public void showAllConversations() {
    updateAllConversations(false);

    for (final Conversation c : view.getConversations()) {
      printConversation(c, userContext);
    }
  }

  // Get a single conversation from the server.
  public Conversation getConversation(Uuid conversationId) {
    return view.findConversation(conversationId);
  }

  private void joinConversation(String match) {
    Method.notImplemented();
  }

  private void leaveCurrentConversation() {
    Method.notImplemented();
  }

  public Collection<Conversation> getConversations() {
    return view.getConversations();
  }

  // Update the list of known Conversations.
  // If the input currentChanged is true, then re-establish the state of
  // the current Conversation, including its messages.
  public void updateAllConversations(boolean currentChanged) {

    for (final Conversation cs : view.getConversations()) {
      conversationsMap.put(cs.id,cs);
    }

  }

  // Print Conversation.  User context is used to map from owner UUID to name.
  public static void printConversation(Conversation c, ClientUser userContext) {
    if (c == null) {
      System.out.println("Null conversation");
    } else {
      final String name = (userContext == null) ? null : userContext.getName(c.owner);
      final String ownerName = (name == null) ? "" : String.format(" (%s)", name);
      System.out.format(" Title: %s\n", c.title);
      System.out.format("    Id: %s owner: %s%s created %s\n", c.id, c.owner, ownerName, c.creation);
    }
  }

  // Print Conversation outside of User context.
  public static void printConversation(Conversation c) {
    printConversation(c, null);
  }
}
