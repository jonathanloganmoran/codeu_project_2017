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

import codeu.chat.common.*;
import codeu.chat.database.Connector;
import codeu.chat.util.Logger;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);
  private final Uuid.Generator uuidGenerator;
  private Connector conn = new Connector();

  public Controller(Uuid serverId) {
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

  /*Create an id for message/user/conversation*/
  private Uuid createId() {
    return uuidGenerator.make();
  }

  /*Create a message object*/
  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, String body) {
    return new Message(id,conn.getMessage_creationTime(Uuids.toString(id)),author,body,conversation);
  }

  /*Create a new user*/
  @Override
  public User newUser(Uuid id, String name) {
    LOG.error("The newly created ID is "+Uuids.toString(id));
    Time creation_time = conn.getUser_creationTime(Uuids.toString(id));
    if(creation_time == null){
      LOG.error("creation time is still null");
    }
    return  new User(id, name, creation_time);
  }

  /*Create a new converstion*/
  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner) {
    return new Conversation(id,owner,conn.getConv_creationTime(Uuids.toString(id)),title);
  }

}
