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

import codeu.chat.common.User;
import codeu.chat.common.Uuid;
import codeu.chat.common.Uuids;
import codeu.chat.util.Logger;
import codeu.chat.util.TextValidator;
import codeu.chat.util.store.Store;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ClientUser {

  private final static Logger.Log LOG = Logger.newLog(ClientUser.class);

  //private static final Collection<Uuid> EMPTY = Arrays.asList(new Uuid[0]);
  private final Controller controller;
  private final View view;

  private User current = null;

  //private final Map<Uuid, User> usersById = new HashMap<>();
 // private static final codeu.chat.database.Connector con = new codeu.chat.database.Connector();

  // This is the set of users known to the server, sorted by name.
 //private Store<String, User> usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);

  public ClientUser(Controller controller, View view) {
    this.controller = controller;
    this.view = view;
  }

  // Validate the username string
  static public boolean isValidName(String userName) {
    return TextValidator.isValidUserName(userName);
  }

  public boolean hasCurrent() {
    return (current != null);
  }

  public User getCurrent() {
    return current;
  }

  public boolean signInUser(String name, String password) {
  //  updateUsers();
    final User prev = current;
    if (name != null) {
        User newCurrent = view.AccountExists(name,password);
      if ( newCurrent != null) {
        current = newCurrent;
      }
    }
    return (prev != current);
  }

  public boolean signOutUser() {
    boolean hadCurrent = hasCurrent();
    current = null;
    return hadCurrent;
  }

  public void showCurrent() {
    printUser(current);
  }
/*
  public void addUser(String name, String password) {
    final boolean validInputs = isValidName(name);

    final User user = (validInputs) ? controller.newUser(name,password) : null;

    if (user == null) {
      System.out.format("Error: user not created - %s.\n",
          (validInputs) ? "server failure" : "bad input value");
    } else {
      LOG.info("New user complete, Name= \"%s\" UUID=%s", user.name, user.id);
      updateUsers();
    }
  }
*/
  public boolean addUser(String name, String password) {
      final boolean validInputs = isValidName(name);
      final User user = (validInputs) ? controller.newUser(name, password) : null;

      if (user == null) {
          System.out.format("Error: user not created - %s.\n",
                  (validInputs) ? "server failure" : "bad input value");
          return false;
      } else {
          LOG.info("New user complete, Name= \"%s\" UUID=%s", user.name, user.id);
         // updateUsers();
          return true;
      }
  }

/*
  public void populateList(List<UserFromDB> list){
   Collection<User>collection =  view.getUsersExcluding(null);

  }
*/

  public void showAllUsers() {
    //updateUsers();
    for (final User u : getUsers()) {
      printUser(u);
    }
  }

  public User lookup(Uuid id) {
    return view.findUser(id);
  }

  public String getName(Uuid id) {
    final User user = lookup(id);
    if (user == null) {
      LOG.warning("userContext.lookup() failed on ID: %s", id);
      return null;
    } else {
      return user.name;
    }
  }

  public Collection<User> getUsers() {
    return view.getUsers(); //usersByName.all();
  }
/*
  public void updateUsers() {
    usersById.clear();
    //usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);

    for (final User user : view.getUsers()) {
      usersById.put(user.id, user);
      //usersByName.insert(user.name, user);
    }
  }
*/
  public static String getUserInfoString(User user) {
    return (user == null) ? "Null user" :
        String.format(" User: %s\n   Id: %s\n", user.name, user.id); // removed "    created: %s"
  }

  public String showUserInfo(String username) {
    return getUserInfoString(view.getUserByName(username));
  }

  // Move to User's toString()
  public static void printUser(User user) {
    System.out.println(getUserInfoString(user));
  }
}
