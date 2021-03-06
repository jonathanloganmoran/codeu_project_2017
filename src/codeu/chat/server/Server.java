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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;

import codeu.chat.common.Conversation;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.LinearUuidGenerator;
import codeu.chat.common.Message;
import codeu.chat.common.NetworkCode;
import codeu.chat.common.Relay;
import codeu.chat.common.Time;
import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializer;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;

import javax.print.DocFlavor;

public final class Server {

  private final static Logger.Log LOG = Logger.newLog(Server.class);

  private final Uuid id;
  private final byte[] secret;

  //private final Model model = new Model();

  //private final View view = new View(model);
  private final View view = new View();
  private final Controller controller;

  private final Relay relay;
  private Uuid lastSeen = Uuids.NULL;

  public Server(Uuid id, byte[] secret, Relay relay) {

    this.id = id;
    this.secret = Arrays.copyOf(secret, secret.length);

    this.controller = new Controller(id);
    this.relay = relay;
  }

  public void syncWithRelay(int maxReadSize) throws Exception {
    for (final Relay.Bundle bundle :  relay.read(id, secret, lastSeen, maxReadSize)) {
      onBundle(bundle);
      lastSeen = bundle.id();
    }
  }

  public boolean handleConnection(Connection connection) throws Exception {

    LOG.info("Handling new connection...");

    return onMessage(connection.in(), connection.out());
  }

  private boolean onMessage(InputStream in, OutputStream out) throws IOException {

    final int type = Serializers.INTEGER.read(in);

    if (type == NetworkCode.NEW_MESSAGE_REQUEST) {
      final Uuid author = Uuids.SERIALIZER.read(in);
      final Uuid conversation = Uuids.SERIALIZER.read(in);
      final String content = Serializers.STRING.read(in);
      final Message message = controller.newMessage(author, conversation, content);
      Serializers.INTEGER.write(out, NetworkCode.NEW_MESSAGE_RESPONSE);
      Serializers.nullable(Message.SERIALIZER).write(out, message);
      // Unlike the other calls - we need to send something the result of this
      // call to the relay. Waiting until after the server has written back to
      // the client allows the client to get the response, but the network
      // connection has not been closed. However to wait after until the client-server
      // connection was closed before sending would be very complicated.
      sendToRelay(author, conversation, message.id);

    } else if (type == NetworkCode.NEW_USER_REQUEST) {
      final String name = Serializers.STRING.read(in);
      final String password =  Serializers.STRING.read(in);
      final User user = controller.newUser(name,password);
      Serializers.INTEGER.write(out, NetworkCode.NEW_USER_RESPONSE);
      Serializers.nullable(User.SERIALIZER).write(out, user);

    } else if (type == NetworkCode.NEW_CONVERSATION_REQUEST) {
      final String title = Serializers.STRING.read(in);
      final Uuid owner = Uuids.SERIALIZER.read(in);
      final Conversation conversation = controller.newConversation(title, owner);
      Serializers.INTEGER.write(out, NetworkCode.NEW_CONVERSATION_RESPONSE);
      Serializers.nullable(Conversation.SERIALIZER).write(out, conversation);

    } else if (type == NetworkCode.GET_USER_BY_ID_REQUEST) {

      final Uuid id = Uuids.SERIALIZER.read(in);
      final User user = view.findUser(id);

      Serializers.INTEGER.write(out, NetworkCode.GET_USER_BY_ID_RESPONSE);
      User.SERIALIZER.write(out, user);

    } else if (type == NetworkCode.GET_USER_BY_NAME_REQUEST) {

    final String name= Serializers.STRING.read(in);
    final User user = view.getUserByName(name);

    Serializers.INTEGER.write(out, NetworkCode.GET_USER_BY_NAME_RESPONSE);
    User.SERIALIZER.write(out, user);

  }
    else if (type == NetworkCode.GET_CONVERSATION_BY_ID_REQUEST) {

      final Uuid id = Uuids.SERIALIZER.read(in);
      final Conversation conversation = view.findConversation(id);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATION_BY_ID_RESPONSE);
      Conversation.SERIALIZER.write(out, conversation);

    }
    else if (type == NetworkCode.GET_CONVERSATION_BY_TITLE_REQUEST) {

      final String title = Serializers.STRING.read(in);
      final Conversation conversation = view.getConversationByTitle(title);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATION_BY_TITLE_RESPONSE);
      Conversation.SERIALIZER.write(out, conversation);

    } else if (type == NetworkCode.GET_MESSAGE_BY_ID_REQUEST) {

      final Uuid id = Uuids.SERIALIZER.read(in);
      Message message = view.findMessage(id);
      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGE_BY_ID_RESPONSE);
      Message.SERIALIZER.write(out, message);
    }
    else if(type == NetworkCode.VERIFY_ACCOUNT_REQUEST){
      final String name = Serializers.STRING.read(in);
      final String password = Serializers.STRING.read(in);
      final User user = view.verifyAccount(name,password);
      Serializers.INTEGER.write(out, NetworkCode.VERIFY_ACCOUNT_RESPONSE);
      User.SERIALIZER.write(out, user);
    }

    else if (type == NetworkCode.GET_MESSAGES_BY_CONVERSATION_REQUEST) {

      final Uuid id = Uuids.fromString(Serializers.STRING.read(in));
      final Collection<Message> messages = view.getMessages(id);

      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_CONVERSATION_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);
    }

    else if (type == NetworkCode.GET_MESSAGES_BY_TIME_REQUEST) {

      final Uuid conversation = Uuids.SERIALIZER.read(in);
      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Message> messages = view.getMessages(conversation, startTime, endTime);
      Serializers.INTEGER.write(out, NetworkCode.GET_MESSAGES_BY_TIME_RESPONSE);
      Serializers.collection(Message.SERIALIZER).write(out, messages);
    }

    else if (type == NetworkCode.GET_ALL_USERS_REQUEST) {

      final Collection<User> users = view.getUsers();
      Serializers.INTEGER.write(out, NetworkCode.GET_ALL_USERS_RESPONSE);

      Serializers.collection(User.SERIALIZER).write(out, users);
    }

    else if (type == NetworkCode.GET_USER_GENERATION_REQUEST) {

      Serializers.INTEGER.write(out, NetworkCode.GET_USER_GENERATION_RESPONSE);
      Uuids.SERIALIZER.write(out, view.getUserGeneration());
    }

    else if (type == NetworkCode.GET_CONVERSATIONS_BY_TIME_REQUEST) {

      final Time startTime = Time.SERIALIZER.read(in);
      final Time endTime = Time.SERIALIZER.read(in);

      final Collection<Conversation> conversations = view.getConversations(startTime, endTime);

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_BY_TIME_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    }
    else if (type == NetworkCode.GET_CONVERSATIONS_REQUEST) {

      //final String filter = Serializers.STRING.read(in);
      final Collection<Conversation> conversations = view.getConversations();

      Serializers.INTEGER.write(out, NetworkCode.GET_CONVERSATIONS_RESPONSE);
      Serializers.collection(Conversation.SERIALIZER).write(out, conversations);

    }
    else {

      // In the case that the message was not handled make a dummy message with
      // the type "NO_MESSAGE" so that the client still gets something.
      Serializers.INTEGER.write(out, NetworkCode.NO_MESSAGE);

    }

    return true;
  }

  private void onBundle(Relay.Bundle bundle) {

    final Relay.Bundle.Component relayUser = bundle.user();
    final Relay.Bundle.Component relayConversation = bundle.conversation();
    final Relay.Bundle.Component relayMessage = bundle.user();

    User user = view.findUser(relayUser.id());

    if (user == null) {
      user = controller.newUser(relayUser.id(), relayUser.text());
    }

    Conversation conversation = view.findConversation(relayConversation.id());

    if (conversation == null) {

      // As the relay does not tell us who made the conversation - the first person who
      // has a message in the conversation will get ownership over this server's copy
      // of the conversation.
      conversation = controller.newConversation(relayConversation.id(),
                                                relayConversation.text(),
                                                user.id
                                                );
    }
    Message message = view.findMessage(relayMessage.id());
    if (message == null) {
      message = controller.newMessage(relayMessage.id(),
                                      user.id,
                                      conversation.id,
                                      relayMessage.text());
    }
  }

  private void sendToRelay(Uuid userId, Uuid conversationId, Uuid messageId) {

    final User user = view.findUser(userId);
    final Conversation conversation = view.findConversation(conversationId);
    final Message message = view.findMessage(messageId);

    relay.write(id,
                secret,
                relay.pack(user.id, user.name, user.creation),
                relay.pack(conversation.id, conversation.title, conversation.creation),
                relay.pack(message.id, message.content, message.creation));
  }
}
