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

import java.util.ArrayList;
import java.util.Collection;

import codeu.chat.common.*;
import codeu.chat.util.Logger;
import codeu.chat.util.Serializers;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;

// VIEW
//
// This is the view component of the Model-View-Controller pattern used by the
// the client to reterive readonly data from the server. All methods are blocking
// calls.
public final class View implements BasicView, LogicalView, SinglesView {

  private final static Logger.Log LOG = Logger.newLog(View.class);

  private final ConnectionSource source;

  public View(ConnectionSource source) {
    this.source = source;
  }

  /* Get all the users from the codeu.chat.database*/
  @Override
  public Collection<User> getUsers() {

    final Collection<User> users = new ArrayList<>();
    try (final Connection connection = source.connect()) {
      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_ALL_USERS_REQUEST);
     // Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);
      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_ALL_USERS_RESPONSE) {
        users.addAll(Serializers.collection(User.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return users;
  }

  /* getAll Conversation*/
  public Collection<Conversation> getConversations() {
    final Collection<Conversation> conversations = new ArrayList<>();
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_REQUEST);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return conversations;
  }
/*
  @Override
  public Collection<Message> getMessages() {
    return null;
  }
  */
/*
  @Override
  public Collection<Conversation> getConversations() {

    final Collection<Conversation> conversations = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST);
      Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return conversations;
  }
*/
  @Override
  public Collection<Conversation> getConversations(Time start, Time end) {

    final Collection<Conversation> conversations = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_TIME_REQUEST);
      Time.SERIALIZER.write(connection.out(), start);
      Time.SERIALIZER.write(connection.out(), end);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_TIME_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return conversations;
  }


  @Override
  public Collection<Message> getMessages(Uuid id) {

    final Collection<Message> messages = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_CONVERSATION_REQUEST);
      Serializers.STRING.write(connection.out(), Uuids.toString(id));

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_MESSAGES_BY_CONVERSATION_RESPONSE) {
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return messages;
  }

  @Override
  public Collection<Message> getMessages(Uuid conversation, Time start, Time end) {

    final Collection<Message> messages = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_TIME_REQUEST);
      Time.SERIALIZER.write(connection.out(), start);
      Time.SERIALIZER.write(connection.out(), end);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_MESSAGES_BY_TIME_RESPONSE) {
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return messages;
  }

  @Override
  public Uuid getUserGeneration() {

    Uuid generation = Uuids.NULL;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USER_GENERATION_REQUEST);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USER_GENERATION_RESPONSE) {
        generation = Uuids.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return generation;
  }

  @Override
  public User findUser(Uuid id) {
    User response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USER_BY_ID_REQUEST);
      Uuids.SERIALIZER.write(connection.out(), id);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USER_BY_ID_RESPONSE) {
        response  = User.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return response;
  }

  public User getUserByName(String name) {
    User response = null;
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USER_BY_NAME_REQUEST);
      Serializers.STRING.write(connection.out(), name);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USER_BY_NAME_RESPONSE) {
        response  = User.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return response;
  }

  @Override
  public Conversation findConversation(Uuid id) {
    Conversation response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATION_BY_ID_REQUEST);
      Uuids.SERIALIZER.write(connection.out(), id);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATION_BY_ID_RESPONSE) {
        response  = Conversation.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return response;
  }

  public Conversation getConversationByTitle(String title){
    Conversation response = null;
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATION_BY_TITLE_REQUEST);
      Serializers.STRING.write(connection.out(), title);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATION_BY_TITLE_RESPONSE) {
        response  = Conversation.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return response;
  }

  @Override
  public Message findMessage(Uuid id) {
    Message response = null;

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGE_BY_ID_REQUEST);
      Uuids.SERIALIZER.write(connection.out(), id);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_MESSAGE_BY_ID_RESPONSE) {
        response  = Message.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }
    return response;
  }
  public User AccountExists(String name, String password){
    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.VERIFY_ACCOUNT_REQUEST);
      Serializers.STRING.write(connection.out(), name);
      Serializers.STRING.write(connection.out(), password);
      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.VERIFY_ACCOUNT_RESPONSE) {
        return User.SERIALIZER.read(connection.in());
      } else {
        LOG.error("Response from server failed.");
        return null;
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
      return null;
    }
  }

/*
  @Override
  public Collection<User> getUsers(Collection<Uuid> ids) {

    final Collection<User> users = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USERS_BY_ID_REQUEST);
      Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USERS_BY_ID_RESPONSE) {
        users.addAll(Serializers.collection(User.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return users;
  }
*/
 /* public Collection<Conversation> getConversations(Collection<Uuid> ids) {

    final Collection<Conversation> conversations = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_ID_REQUEST);
      Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return conversations;
  }*/

/*
  @Override
  public Collection<Conversation> getConversations(String filter) {

    final Collection<Conversation> conversations = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_CONVERSATIONS_BY_TITLE_REQUEST);
      Serializers.STRING.write(connection.out(), filter);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_TITLE_RESPONSE) {
        conversations.addAll(Serializers.collection(Conversation.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return conversations;
  }
*/


  /*@Override
  public Collection<Message> getMessages(Collection<Uuid> ids) {

    final Collection<Message> messages = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_ID_REQUEST);
      Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_CONVERSATIONS_BY_ID_RESPONSE) {
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return messages;
  }*/

/*
  @Override
  public Collection<Message> getMessages(Uuid rootMessage, int range) {

    final Collection<Message> messages = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_MESSAGES_BY_RANGE_REQUEST);
      Uuids.SERIALIZER.write(connection.out(), rootMessage);
      Serializers.INTEGER.write(connection.out(), range);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_MESSAGES_BY_RANGE_RESPONSE) {
        messages.addAll(Serializers.collection(Message.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }

    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return messages;
  }
*/

/*
  @Override
  public Collection<User> getUsersExcluding(Collection<Uuid> ids) {

    final Collection<User> users = new ArrayList<>();

    try (final Connection connection = source.connect()) {

      Serializers.INTEGER.write(connection.out(), NetworkCode.GET_USERS_EXCLUDING_REQUEST);
      Serializers.collection(Uuids.SERIALIZER).write(connection.out(), ids);

      if (Serializers.INTEGER.read(connection.in()) == NetworkCode.GET_USERS_EXCLUDING_RESPONSE) {
        users.addAll(Serializers.collection(User.SERIALIZER).read(connection.in()));
      } else {
        LOG.error("Response from server failed.");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during call on server. Check log for details.");
      LOG.error(ex, "Exception during call on server.");
    }

    return users;
  }
  */
}

