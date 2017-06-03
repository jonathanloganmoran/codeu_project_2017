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

import codeu.chat.common.Uuids;
import codeu.chat.database.Connector;
import codeu.chat.database.ConversationFromDB;
import codeu.chat.database.MessageFromDB;
import codeu.chat.database.UserFromDB;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import codeu.chat.common.BasicView;
import codeu.chat.common.Conversation;
import codeu.chat.common.LogicalView;
import codeu.chat.common.Message;
import codeu.chat.common.SinglesView;
import codeu.chat.common.Time;
import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.util.Logger;

public final class View implements BasicView, LogicalView, SinglesView {

  private final static Logger.Log LOG = Logger.newLog(View.class);
  Connector connector= new Connector();

  public View() {}

  /**
   * Get all the users from the codeu.chat.database
   * @return collection of users
   */
  @Override
  public Collection<User> getUsers() {
    List<UserFromDB> list = connector.getAllUsers();
    Collection<User> userCollection = new ArrayList<>();
    for(UserFromDB user : list){
      User currentUser = new User(Uuids.fromString(user.getUuid()), user.getUsername(),user.getTime());
      userCollection.add(currentUser);
    }
    LOG.info("All the users have been retrieved");
    return userCollection;
  }

  /**
   * Get all the conversations from the codeu.chat.database
   * @return
   */
  @Override
  public Collection<Conversation> getConversations() {
    List<ConversationFromDB> list = connector.getConversations();
    Collection<Conversation> conversationCollection = new ArrayList<>();
    for(ConversationFromDB conversation : list){
      Conversation currentConversation = new Conversation(
          Uuids.fromString(conversation.getUuid()),
          Uuids.fromString(conversation.getAuthorid()),
          conversation.getTime(),
          conversation.getTitle());
      conversationCollection.add(currentConversation);
    }
    LOG.info("All the conversations have retrieved");
    return conversationCollection;
  }

  @Override
  public Uuid getUserGeneration() {
    return null;
  }

  /**
   * Get all the conversations that create within a time
   * @param start
   * @param end
   * @return
   */
  @Override
  public Collection<Conversation> getConversations(Time start, Time end) {
    Collection<Conversation> collection = getConversations();
    Collection<Conversation> target = new ArrayList<>();
    for(Conversation conversation: collection){
     if(conversation.creation.compareTo(start) == 1 && conversation.creation.compareTo(end) == 0){
       target.add(conversation);
     }
    }
    LOG.info("All the conversation start from  %s to %s has been retrieved ", start, end);
    return target;
  }

  /**
   * Get all the messages in the conversation
   * @param conversation
   * @return
   */
  @Override
  public Collection<Message> getMessages(Uuid conversation) {
    Collection<Message> messageCollection = new ArrayList<>();
    List<MessageFromDB> list = connector.getMessages(Uuids.toString(conversation));
    for (MessageFromDB message: list){
      Message currentMessage = new Message(
          Uuids.fromString(message.getUuid()),
          message.getTime(),
          Uuids.fromString(message.getId_user()),
          message.getMessage(),
          Uuids.fromString(message.getId_conversation()));
      messageCollection.add(currentMessage);
    }
    LOG.info("All the messages in conversation %s have retrieved", conversation.id());
    return null;
  }

  /**
   * Get messages in the conversation that happen during a specific time
   * @param conversation
   * @param start
   * @param end
   * @return
   */
  @Override
  public Collection<Message> getMessages(Uuid conversation, Time start, Time end) {
    Collection<Message> messageCollection = getMessages(conversation);
    Collection<Message> target = new ArrayList<>();
    for(Message message: messageCollection){
      if(message.creation.compareTo(start) == 1 && message.creation.compareTo(end) == 0){
        target.add(message);
      }
    }
    LOG.info("All the messages start from  %s to %s in conversation %s has been retrieved ", start, end, conversation.id());
    return target;
  }

  /**
   * Find  user through ID
   * @param id userid
   * @return User
   */
  @Override
  public User findUser(Uuid id) {
    UserFromDB user = connector.getUser(Uuids.toString(id));
    if (user != null) {
      return new User(id, user.getUsername(), user.getTime());
    }
    return null;
  }

  /**
   * Find conversation through id
   * @param id conversation id
   * @return conversation
   */
  @Override
  public Conversation findConversation(Uuid id) {
    ConversationFromDB  conversation= connector.getConversation(Uuids.toString(id));
    if(conversation  != null) {
      return new Conversation(
              id,
              Uuids.fromString(conversation.getAuthorid()),
              conversation.getTime(),
              conversation.getTitle());
    }
    return null;
  }

  /**
   * Find message through id
   * @param id message id
   * @return message
   */
  @Override
  public Message findMessage(Uuid id) {
    MessageFromDB message = connector.getMessage(Uuids.toString(id));
    if(message != null){
      return new Message(id,
              message.getTime(),
              Uuids.fromString(message.getId_user()),
              message.getMessage(),
              Uuids.fromString(message.getId_conversation()));
    }
    return null;
  }

  /**
   *  Get user by username
   * @param name name of the user
   * @return user
   */
  public User getUserByName(String name){
    System.out.println("VIEW LINE 323: ENTERED");
    UserFromDB userFromDB = connector.getUserByName(name);
    if(userFromDB != null){
      System.out.println("VIEW LINE 326: USER IS NOT NULL");
      return new User(Uuids.fromString(userFromDB.getUuid()), userFromDB.getUsername(), userFromDB.getTime());
    }
    System.out.println("VIEW LINE 329: USER IS NULL");
    return null;
  }

  /**
   * Get conversation by title
   * @param title conversation title
   * @return conversation
   */
  public Conversation getConversationByTitle(String title){
    ConversationFromDB conversationFromDB = connector.getConversationByTitle(title);
    if(conversationFromDB != null){
      return new Conversation(
              Uuids.fromString(conversationFromDB.getUuid()),
              Uuids.fromString(conversationFromDB.getAuthorid()),
              conversationFromDB.getTime(),
              title);
    }
    return  null;
  }

  /**
   * Verify the account
   * @param name user name
   * @param password user password
   * @return user the provided info is correct
   */
  public User verifyAccount(String name, String password){
    if(connector.verifyAccount(name,password)){
     return getUserByName(name);
    }
    return null;
  }
}
