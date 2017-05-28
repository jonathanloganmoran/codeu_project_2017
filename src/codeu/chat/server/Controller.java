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

package codeu.chat.server;

import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.database.Connector;
import codeu.chat.common.BasicController;
import codeu.chat.common.RawController;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  /* Model is teh codeu.chat.database so it is not necessary here to put the model as the codeu.chat.database exists*/
  //private final Model model;

  private final Uuid.Generator uuidGenerator;
  private Connector conn = new Connector();

  public Controller(Uuid serverId) {
    //this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
  }

  /* Add new message to the codeu.chat.database*/
  @Override
  public Message newMessage(Uuid author, Uuid conversation, String body) {
    Uuid newID = createId();
    if(conn.addMessage(Uuids.toString(newID),Uuids.toString(author), Uuids.toString(conversation), body)){
      return newMessage(newID,author,conversation,body);
    }
    return null;
  }

  /* Add new user to the codeu.chat.database*/
  @Override
  public User newUser(String name, String password) {
    Uuid newID = createId();
    if(conn.addAccount(name, password,Uuids.toString(newID))){
      return  newUser(newID, name);
    }
    return  null;
  }

  /* Add new conversation to the databse*/
  @Override
  public Conversation newConversation(String title, Uuid owner) {
    Uuid newID = createId();
    if(conn.addConversation(Uuids.toString(newID),Uuids.toString(owner), title)){
      return newConversation(newID, title, owner);
    }
    return null;
  }

  private Uuid createId() {
    return uuidGenerator.make();
  }

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body) {
    return new Message(id,conn.getMessage_creationTime(Uuids.toString(id)),author,body,conversation);

  }

  @Override
  public User newUser(Uuid id, String name) {
    return  new User(id, name, conn.getUser_creationTime(Uuids.toString(id)));
  }

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner) {
    return new Conversation(id,owner,conn.getConv_creationTime(Uuids.toString(id)),title);
  }

/*

  @Override
  public boolean newMessage(Uuid id, Uuid author, Uuid conversation, String body) {
    return false;
  }

  /*
    @Override
    public boolean newMessage(Uuid author, Uuid conversation, String body) {
      Uuid newID = createId();
      while(!isIdFree(newID)){
        newID = createId();
      }
      return conn.addMessage(Uuids.toString(id), Uuids.toString(author), body, Uuids.toString(conversation));

      if (foundUser != null && foundConversation != null && isIdFree(id)) {

        message = new Message(id,creationTime, author,body, conversation);
        model.add(message);
        LOG.info("Message added: %s", message.id);

        // Find and update the previous "last" message so that it's "next" value
        // will point to the new message.

        if (Uuids.equals(foundConversation.lastMessage, Uuids.NULL)) {

          // The conversation has no messages in it, that's why the last message is NULL (the first
          // message should be NULL too. Since there is no last message, then it is not possible
          // to update the last message's "next" value.

        } else {
          final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
          lastMessage.next = message.id;
        }

        // If the first message points to NULL it means that the conversation was empty and that
        // the first message should be set to the new message. Otherwise the message should
        // not change.

        foundConversation.firstMessage =
            Uuids.equals(foundConversation.firstMessage, Uuids.NULL) ?
            message.id :
            foundConversation.firstMessage;

        // Update the conversation to point to the new last message as it has changed.

        foundConversation.lastMessage = message.id;

        if (!foundConversation.users.contains(foundUser)) {
          foundConversation.users.add(foundUser.id);
        }
      }

      return message;
    }
  */
/*
  @Override
  public boolean  newUser(Uuid id, String name, Time creationTime) {

    User user = null;

    if (isIdFree(id)) {

      user = new User(id, name, creationTime);
      model.add(user);

      LOG.info(
          "newUser success (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);

    } else {

      LOG.info(
          "newUser fail - id in use (user.id=%s user.name=%s user.time=%s)",
          id,
          name,
          creationTime);
    }

    return user;
  }

  @Override
  public boolean newConversation(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);

    Conversation conversation = null;

    if (isIdFree(id)) {
      conversation = new Conversation(id, owner, creationTime, title);
      model.add(conversation);

      if(foundOwner != null) {
        LOG.info("Statup conversation added: " + conversation.id);
      } else {
        LOG.info("Conversation added: " + conversation.id);
      }
    }

    return conversation;
  }
*/
/*
  private Uuid createId() {
    return uuidGenerator.make();
    //Uuid candidate;
    for (candidate = uuidGenerator.make();
         //isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

     // Assuming that "randomUuid" is actually well implemented, this
     // loop should never be needed, but just incase make sure that the
     // Uuid is not actually in use before returning it.

    //}

    //return candidate;
  }
/*
  private boolean isIdInUse(Uuid id) {
    return model.messageById().first(id) != null ||
           model.conversationById().first(id) != null ||
           model.userById().first(id) != null;
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }
*/
}
