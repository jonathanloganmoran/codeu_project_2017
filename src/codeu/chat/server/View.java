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

  //in this case, we have codeu.chat.database
  //private final Model model;

  //codeu.chat.database:
  Connector connector= new Connector();

  public View(/*Model model*/) {
   // this.model = model;
  }

/*
  public Collection<User> getUsers(Collection<Uuid> ids) {
    return intersect(model.userById(), ids);
  }
*/

  /**
   * Get all the users from the codeu.chat.database
   * @return
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
/*
  @Override
  public Collection<ConversationSummary> getAllConversations() {

    final Collection<ConversationSummary> summaries = new ArrayList<>();

    for (final Conversation conversation : model.conversationById().all()) {
        summaries.add(conversation.summary);
    }

    return summaries;

  }
*/

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
/*
  @Override
  public Collection<Conversation> getConversations(Collection<Uuid> ids) {
    return intersect(model.conversationById(), ids);
  }

  @Override
  public Collection<Message> getMessages(Collection<Uuid> ids) {
    return intersect(model.messageById(), ids);
  }

  @Override
  public Uuid getUserGeneration() {
    return model.userGeneration();
  }

  @Override
  public Collection<User> getUsersExcluding(Collection<Uuid> ids) {

    final Set<User> blacklist = new HashSet<>(intersect(model.userById(), ids));
    final Set<User> users = new HashSet<>();

    for (final User user : model.userById().all()) {
      if (!blacklist.contains(user)) {
        users.add(user);
      }
    }

    return users;
  }

  @Override
  public Collection<Conversation> getConversations(Time start, Time end) {

    final Collection<Conversation> conversations = new ArrayList<>();

    for (final Conversation conversation : model.conversationByTime().range(start, end)) {
      conversations.add(conversation);
    }

    return conversations;

  }

  @Override
  public Collection<Conversation> getConversations(String filter) {

    final Collection<Conversation> found = new ArrayList<>();

    for (final Conversation conversation : model.conversationByText().all()) {
      if (Pattern.matches(filter, conversation.title)) {
        found.add(conversation);
      }
    }

    return found;
  }

  @Override
  public Collection<Message> getMessages(Uuid conversation, Time start, Time end) {

    final Conversation foundConversation = model.conversationById().first(conversation);

    final List<Message> foundMessages = new ArrayList<>();

    Message current = (foundConversation == null) ?
        null :
        model.messageById().first(foundConversation.firstMessage);

    while (current != null && current.creation.compareTo(start) < 0) {
      current = model.messageById().first(current.next);
    }

    while (current != null && current.creation.compareTo(end) <= 0) {
      foundMessages.add(current);
      current = model.messageById().first(current.next);
    }

    return foundMessages;
  }

  @Override
  public Collection<Message> getMessages(Uuid rootMessage, int range) {

    int remaining = Math.abs(range);
    LOG.info("in getMessage: UUID=%s range=%d", rootMessage, range);

    // We want to return the messages in order. If the range was negative
    // the messages would be backwards. Use a linked list as it supports
    // adding at the front and adding at the end.

    final LinkedList<Message> found = new LinkedList<>();

    // i <= remaining : must be "<=" and not just "<" or else "range = 0" would
    // return nothing and we want it to return just the root because the description
    // is that the function will return "range" around the root. Zero messages
    // around the root means that it should just return the root.

    Message current = model.messageById().first(rootMessage);

    if (range > 0) {
      for (int i = 0; i <= remaining && current != null; i++) {
        found.addLast(current);
        current = model.messageById().first(current.next);
      }
    } else {
      for (int i = 0; i <= remaining && current != null; i++) {
        found.addFirst(current);
        current = model.messageById().first(current.previous);
      }
    }

    return found;
  }
*/
  @Override
  public User findUser(Uuid id) {
    UserFromDB user = connector.getUser(Uuids.toString(id));
    if (user != null) {
      return new User(id, user.getUsername(), user.getTime());
    }
    return null;
  }

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

  public User getUserByName(String name){
    UserFromDB userFromDB = connector.getUserByName(name);
    if(userFromDB != null){
      return new User(Uuids.fromString(userFromDB.getUuid()), userFromDB.getUsername(), userFromDB.getTime());
    }
    return null;
  }

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

  public User verifyAccount(String name, String password){
    if(connector.verifyAccount(name,password)){
     return getUserByName(name);
    }
    return null;
  }

/*
  private static <T> Collection<T> intersect(StoreAccessor<Uuid, T> store, Collection<Uuid> ids) {

    // Use a set to hold the found users as this will prevent duplicate ids from
    // yielding duplicates in the result.

    final Collection<T> found = new HashSet<>();

    for (final Uuid id : ids) {

      final T t = store.first(id);

      if (t == null) {
        LOG.warning("Unmapped id %s", id);
      } else if (found.add(t)) {
        // do nothing
      } else {
        LOG.warning("Duplicate id %s", id);
      }
    }

    return found;
  }
 */
}
