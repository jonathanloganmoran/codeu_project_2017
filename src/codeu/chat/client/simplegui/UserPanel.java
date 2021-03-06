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

package codeu.chat.client.simplegui;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.util.TextValidator;
import codeu.chat.util.Logger;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

// NOTE: JPanel is serializable, but there is no need to serialize UserPanel
// without the @SuppressWarnings, the compiler will complain of no override for serialVersionUID
@SuppressWarnings("serial")
public final class UserPanel extends JPanel {

  private final ClientContext clientContext;
  final Logger.Log LOG = Logger.newLog(UserPanel.class);

  public UserPanel(ClientContext clientContext) {
    super(new GridBagLayout());
    this.clientContext = clientContext;
    initialize();

  }

  private void initialize() {

    // This panel contains from top to bottom; a title bar, a list of users,
    // information about the current (selected) user, and a button bar.

    // Title bar - also includes name of currently signed-in user.
    final JPanel titlePanel = new JPanel(new GridBagLayout());
    final GridBagConstraints titlePanelC = new GridBagConstraints();

    final JLabel titleLabel = new JLabel("Users", JLabel.LEFT);
    final GridBagConstraints titleLabelC = new GridBagConstraints();
    titleLabelC.gridx = 0;
    titleLabelC.gridy = 0;
    titleLabelC.anchor = GridBagConstraints.PAGE_START;

    final GridBagConstraints titleGapC = new GridBagConstraints();
    titleGapC.gridx = 1;
    titleGapC.gridy = 0;
    titleGapC.fill = GridBagConstraints.HORIZONTAL;
    titleGapC.weightx = 0.9;

    final JLabel userSignedInLabel = new JLabel("not signed in", JLabel.RIGHT);
    final GridBagConstraints titleUserC = new GridBagConstraints();
    titleUserC.gridx = 2;
    titleUserC.gridy = 0;
    titleUserC.anchor = GridBagConstraints.LINE_END;

    titlePanel.add(titleLabel, titleLabelC);
    titlePanel.add(Box.createHorizontalGlue(), titleGapC);
    titlePanel.add(userSignedInLabel, titleUserC);
    titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // User List panel.
    final JPanel listShowPanel = new JPanel();
    final GridBagConstraints listPanelC = new GridBagConstraints();

    final DefaultListModel<String> listModel = new DefaultListModel<>();
    final JList<String> userList = new JList<>(listModel);
    userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    userList.setVisibleRowCount(10);
    userList.setSelectedIndex(-1);

    final JScrollPane userListScrollPane = new JScrollPane(userList);
    listShowPanel.add(userListScrollPane);
    userListScrollPane.setPreferredSize(new Dimension(150, 150));

    // Current User panel.
    final JPanel currentPanel = new JPanel();
    final GridBagConstraints currentPanelC = new GridBagConstraints();

    final JTextArea userInfoPanel = new JTextArea();
    final JScrollPane userInfoScrollPane = new JScrollPane(userInfoPanel);
    currentPanel.add(userInfoScrollPane);
    userInfoScrollPane.setPreferredSize(new Dimension(245, 85));

    // Button bar.
    final JPanel buttonPanel = new JPanel();
    final GridBagConstraints buttonPanelC = new GridBagConstraints();

    final JButton userUpdateButton = new JButton("Update");
    final JButton userSignInButton = new JButton("Sign In");
    final JButton userAddButton = new JButton("Add");

    buttonPanel.add(userUpdateButton);
    buttonPanel.add(userSignInButton);
    buttonPanel.add(userAddButton);

    // Placement of title, list panel, buttons, and current user panel.
    titlePanelC.gridx = 0;
    titlePanelC.gridy = 0;
    titlePanelC.gridwidth = 10;
    titlePanelC.gridheight = 1;
    titlePanelC.fill = GridBagConstraints.HORIZONTAL;
    titlePanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    listPanelC.gridx = 0;
    listPanelC.gridy = 1;
    listPanelC.gridwidth = 10;
    listPanelC.gridheight = 8;
    listPanelC.fill = GridBagConstraints.BOTH;
    listPanelC.anchor = GridBagConstraints.FIRST_LINE_START;
    listPanelC.weighty = 0.8;

    currentPanelC.gridx = 0;
    currentPanelC.gridy = 9;
    currentPanelC.gridwidth = 10;
    currentPanelC.gridheight = 3;
    currentPanelC.fill = GridBagConstraints.HORIZONTAL;
    currentPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    buttonPanelC.gridx = 0;
    buttonPanelC.gridy = 12;
    buttonPanelC.gridwidth = 10;
    buttonPanelC.gridheight = 1;
    buttonPanelC.fill = GridBagConstraints.HORIZONTAL;
    buttonPanelC.anchor = GridBagConstraints.FIRST_LINE_START;

    this.add(titlePanel, titlePanelC);
    this.add(listShowPanel, listPanelC);
    this.add(buttonPanel, buttonPanelC);
    this.add(currentPanel, currentPanelC);

    // populate listModel with a list of users: done
    UserPanel.this.getAllUsers(listModel);
    userUpdateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        UserPanel.this.getAllUsers(listModel);
      }
    });

    // Updated to verify password with codeu.chat.database as well.
    userSignInButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (userList.getSelectedIndex() != -1) {
          final String data = userList.getSelectedValue();
          String[] response = getInput("Sign-In",data);
          if (response != null) {
            // Needs to be valid format.
            if (TextValidator.isValidUserName(response[0]) && TextValidator.isValidPassword(response[1])) {

              if(clientContext.user.signInUser(response[0],response[1])){
                userSignedInLabel.setText("Hello " + response[0]);
              }
              else {
                JOptionPane.showMessageDialog(UserPanel.this,
                        "Not able to sign in. Invalid field for username or password.",
                        "Failure to Authenticate", JOptionPane.ERROR_MESSAGE);
              }
            }
            else {
              JOptionPane.showMessageDialog(UserPanel.this,
                      "Not able to sign in. Invalid format of username or password entered.\n"
                      + "Please try again.", "Failure to Authenticate", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      }
    });

    // Updated to add to codeu.chat.database as well.
    userAddButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String[] response = getInput("Sign-In","");
        if (response != null) {

          System.out.println("Response Not Null");
          if (TextValidator.isValidUserName(response[0]) && TextValidator.isValidPassword(response[1])) {
            // Account added to program and to codeu.chat.database in clientContext.user.
            System.out.println("Boolean not yet run");
            boolean added = clientContext.user.addUser(response[0], response[1]);

            System.out.println("Boolean run");
            if (added) {

              System.out.println("Added Boolean True");
              listModel.addElement(response[0]);

              System.out.println("addElement Complete");
              UserPanel.this.getAllUsers(listModel);
            }
            else {
              JOptionPane.showMessageDialog(UserPanel.this,
                  "User not created. User already exists. Please choose different name.",
                  "User Not Created",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
          else {
            JOptionPane.showMessageDialog(UserPanel.this,
                "User not created. Alphanumeric characters only, with no spaces.\nPlease try again.",
                "User Not Created",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });

    userList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (userList.getSelectedIndex() != -1){
          final String data = userList.getSelectedValue();
          userInfoPanel.setText(clientContext.user.showUserInfo(data));
        }
      }
    });
    getAllUsers(listModel);
  }

  // Swing UI: populate ListModel object - updates display objects.
  private void getAllUsers(DefaultListModel<String> usersList) {
    //clientContext.user.updateUsers();
    usersList.clear();
    for (final User u : clientContext.user.getUsers()) {
      usersList.addElement(u.name);
    }
  }

  // Pop-up for entering account details
  private static String[] getInput(String prompt, String usernamePrefill) {
    JTextField username = new JTextField(usernamePrefill);
    JTextField password = new JPasswordField();
    // Must be an object array, holds both Strings and JTextFields
    Object[] message = {
        "Username:", username,
        "Password:", password
    };
    final int option = JOptionPane
        .showConfirmDialog(null, message, prompt, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
    if(option == JOptionPane.OK_OPTION) {
      String[] response = new String[2];
      response[0] = username.getText();
      response[1] = password.getText();
      return response;
    } else {
      return null;
    }
  }
}
