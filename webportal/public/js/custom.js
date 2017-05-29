/*
* Constants used in determining how often to refresh page contents.
*/
// Users will also be refreshed on relevant clicks
var INTERVAL_TO_REFRESH_USERS = 60000;
var INTERVAL_TO_REFRESH_CONVERSATIONS = 50000;
var INTERVAL_TO_REFRESH_MESSAGES = 40000;
var ACTIVE_CONVERSATION_ID;
// Current security risk, should be moved to server in PHP sessions once working
var ACTIVE_UID = -1;
// Moved to top for faster load
updateConversationList(true);
updateUserList();

/**
* Parse the URL parameter for anything after '?dc='
*/
function getParameterByName(name, url) {
  if (!url) url = window.location.href;
  name = name.replace(/[\[\]]/g, "\\$&");
  var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
  results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return '';
  return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
* Look for a sign-up event, display box if so
*/
function decideIfSignOn(){
  var dynamicContent = getParameterByName('dc');
  // If the URL indicates a sign-up, show sign-in box on start
  if(dynamicContent != null){
    if(dynamicContent.length > 9){
      if(dynamicContent.toLowerCase().substring(0,9) == "thank-you"){
        var msgbox = document.getElementById("sign-in-box");
        var message = document.getElementById("message-to-sign-in");
        var usernameInput = document.getElementById("username-sign-in-input");
        var passwordInput = document.getElementById("password-sign-in-input");
        msgbox.style.display = "block";
        usernameInput.value = dynamicContent.substring(10);
        /*
        * This will require users to still sign into the new account, since
        * the URL is not a secure way of authorizing users.
        */
        passwordInput.value = '';
        message.innerHTML = "Thanks for signing up, " + dynamicContent.substring(10) + "!";
      }
    }
  }
}

/**
* Filer for searching the username-input-box
*
* @param element - the element to grab the input text from
* @param div - the div which contains the 'links' or elements to show/hide
* @param link - the class name of each 'link' or element
*/
function filter(element, div, link) {
  var input, filter, ul, li, a, i;
  input = document.getElementById(element);
  filter = input.value.toUpperCase();
  ul = document.getElementById(div);
  li = ul.getElementsByClassName(link);
  for (i = 0; i < li.length; i++) {
    a = li[i];
    var attrval;
    if(element == 'messagebar-input') {
      // Use the message to filter.
      attrval = a.innerHTML;
    } else {
      // Use the hidden full conversation or user name to filter.
      attrval = $(a).attr('keyword');
    }
    attrval = attrval.toUpperCase();
    if (attrval.includes(filter)) {
      li[i].style.display = "";
    } else {
      li[i].style.display = "none";
    }
  }
}

/**
* Opens navigation bar
*/
function openNav() {
  document.getElementById("mySidenav").style.width = "250px";
}

/**
* Closes navigation bar
*/
function closeNav() {
  document.getElementById("mySidenav").style.width = "0";
}

/**
* Opens the sign-in-box for the user to enter details
*/
function signIn() {
  var msgbox = document.getElementById("sign-in-box");
  var usernameInput = document.getElementById("username-sign-in-input");
  var passwordInput = document.getElementById("password-sign-in-input");
  var message = document.getElementById("message-to-sign-in");
  // Use this.innerHTML to only display shortened version of username
  message.innerHTML = "Please enter your account details, " + this.innerHTML + "!";
  // Use keyword to input the full username into the sign-in box
  usernameInput.value = $(this).attr('keyword');
  msgbox.style.display = "block";
  closeNav();
  closeCreateIn();
  closeConversationIn();
}

/**
* Opens the create-in-box for the user to enter details
*/
function createIn(){
  var msgbox = document.getElementById("create-in-box");
  var usernameInput = document.getElementById("username-create-in-input");
  var passwordInput = document.getElementById("password-create-in-input");
  var message = document.getElementById("message-to-create-in");
  message.innerHTML = "Welcome! Please create your account!";
  usernameInput.value = "";
  passwordInput.value = "";
  msgbox.style.display = "block";
  closeNav();
  closeSignIn();
  closeConversationIn();
}

/**
* Opens the conversation-in-box for the user to enter details
*/
function conversationIn(){
  var msgbox = document.getElementById("conversation-in-box");
  var titleInput = document.getElementById("conversation-in-input");
  var message = document.getElementById("message-to-conversation-in");
  message.innerHTML = "Please enter your new topic:";
  titleInput.value = "";
  msgbox.style.display = "block";
  closeNav();
  closeSignIn();
  closeCreateIn();
}

/**
*  Only allow user to send a message that is valid.
*/
function checkForEnableSubmit(){
  // Can add more text validation here if necessary
  var size = document.getElementById("message-input").value.length;
  if(size > 0 && size <= 2000 && ACTIVE_UID != -1) {
    document.getElementById("message-input-button").disabled = false;
  } else {
    document.getElementById("message-input-button").disabled = true;
  }
}

/**
*  Only allow user to press the create account button if
*  the appropriate fields are filled out.
*/
function checkForCreateAccountSubmit(){
  // Can add more text validation here if necessary
  var userLength = document.getElementById("username-create-in-input").value.length;
  var passLength = document.getElementById("password-create-in-input").value.length;
  if(userLength > 0 && userLength <= 32 && passLength >= 4 && passLength <= 32) {
    document.getElementById("create-in-button").disabled = false;
  } else {
    document.getElementById("create-in-button").disabled = true;
  }
}

/**
*  Only allow user to press the create conversation button if
*  the appropriate fields are filled out.
*/
function checkForCreateConversationSubmit(){
  // Can add more text validation here if necessary
  var convLength = document.getElementById("conversation-in-input").value.length;
  if(convLength >= 2 && convLength <= 32) {
    document.getElementById("conversation-in-button").disabled = false;
  } else {
    document.getElementById("conversation-in-button").disabled = true;
  }
}

/**
*  Only allow user to press the sign-in button if
*  the appropriate fields are filled out.
*/
function checkForSignInSubmit(){
  // Can add more text validation here if necessary
  var userLength = document.getElementById("username-sign-in-input").value.length;
  var passLength = document.getElementById("password-sign-in-input").value.length;

  if(userLength > 0 && passLength >= 4) {
    document.getElementById("sign-in-button").disabled = false;
  } else {
    document.getElementById("sign-in-button").disabled = true;
  }
}

/**
*  Update the user list every x miliseconds.
*  Will on average have to wait x/2 seconds before
*  seeing the expected change.
*/
window.setInterval(function(){
  updateUserList();
}, INTERVAL_TO_REFRESH_USERS);

/**
*  Update the conversation list every x miliseconds.
*  Will on average have to wait x/2 seconds before
*  seeing the expected change.
*/
window.setInterval(function(){
  updateConversationList(false);
}, INTERVAL_TO_REFRESH_CONVERSATIONS);

/**
*  Update the conversation list every x miliseconds.
*  Will on average have to wait x/2 seconds before
*  seeing the expected change.
*/
window.setInterval(function(){
  updateMessageList();
}, INTERVAL_TO_REFRESH_MESSAGES);

/**
* Attempts to validate an account
*/
function attemptSignIn() {
  var message = document.getElementById("message-to-sign-in");
  message.innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  var msgbox = document.getElementById("sign-in-box");
  var usernameInput = document.getElementById("username-sign-in-input");
  var usernameStorage = usernameInput.value;
  var passwordInput = document.getElementById("password-sign-in-input");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "valid"){
        document.getElementById("navbars").innerHTML = "&#9776; " + shorten(usernameStorage, 10);
        ACTIVE_UID = usernameStorage;
        usernameInput.value = '';
        passwordInput.value = '';
        msgbox.style.display = "none";
        document.getElementById("create-conversation-link").style.display = "block";
        toggleSignOutCreateLinks(true);
        updateMessageList();
        checkForEnableSubmit();
      } else {
        passwordInput.value = '';
        message.innerHTML = "Invalid account details!";
        checkForSignInSubmit();
        toggleSignOutCreateLinks(false);
      }
    }
  };
  xmlhttp.open("GET", "authenticateAccountHandler.php?u=" + usernameStorage +"&p=" + passwordInput.value , true);
  xmlhttp.send();
}


/**
* Attempts to create an account
*/
function attemptCreate() {
  var message = document.getElementById("message-to-create-in");
  message.innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  var msgbox = document.getElementById("create-in-box");
  var usernameInput = document.getElementById("username-create-in-input");
  var usernameStorage = usernameInput.value;
  var passwordInput = document.getElementById("password-create-in-input");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "created"){
        closeNav();
        document.getElementById("navbars").innerHTML = "&#9776; " + shorten(usernameStorage, 10);
        ACTIVE_UID = usernameStorage;
        usernameInput.value = '';
        passwordInput.value = '';
        msgbox.style.display = "none";
        updateUserList();
        document.getElementById("create-conversation-link").style.display = "block";
        toggleSignOutCreateLinks(true);
        updateMessageList();
        checkForEnableSubmit();
      } else {
        passwordInput.value = '';
        message.innerHTML = this.responseText;
        checkForCreateAccountSubmit();
        toggleSignOutCreateLinks(false);
      }
    }
  };
  xmlhttp.open("GET", "createAccountHandler.php?u=" + usernameInput.value +"&p=" + passwordInput.value , true);
  xmlhttp.send();
}

/**
* Attempts to create a message
*/
function attemptCreateMessage() {
  document.getElementById("message-input-button").disabled = true;
  var message = document.getElementById("message-input");
  var content = message.value;
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "created"){
        message.value = "";
        updateMessageList();
      } else {
        showErrorMessage(this.responseText);
      }
      checkForEnableSubmit();
    }
  };
  var conversation_id = document.getElementById(ACTIVE_CONVERSATION_ID).id;
  xmlhttp.open("GET","createMessageHandler.php?c="+content+"&u="+ACTIVE_UID+"&i="+conversation_id, true);
  xmlhttp.send();
}

/**
* Display an error message to the user
*/
function showErrorMessage(message) {
  var msgbox = document.getElementById("error-message-box");
  var msgcontent = document.getElementById("message-to-error-out");
  message = "Sorry about that. We had an error.<br><br>" + message;
  var time = message.length*20 + 300;
  msgcontent.innerHTML = message;
  msgbox.style.display = "block";
  window.setTimeout(function(){
    msgbox.style.display = "none";
    msgcontent.innerHTML = "";
  }, time);
}

/**
* A method that will regenerate the list of users in the side panel.
*/
function updateUserList() {
  var users_div = document.getElementById("users-div");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      users_div.innerHTML = this.responseText;
      addUNLinks();
      filter('username-input-box','users-div','username-link');
    }
  };
  xmlhttp.open("GET", "updateUserListHandler.php", true);
  xmlhttp.send();
}

/**
* A method that will regenerate the list of conversations.
*/
function updateConversationList(firstLoad) {
  var conv_div = document.getElementById("conversation-list");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      conv_div.innerHTML = this.responseText;
      filter('conversation-input-box','conversation-list','conversation-link');
      addConversationLinks();
      if(firstLoad){
        allConversationLinks = document.getElementsByClassName("conversation-link");
        ACTIVE_CONVERSATION_ID = (allConversationLinks[0].id);
        updateMessageList();
      }
    }
  };
  xmlhttp.open("GET", "updateConversationListHandler.php", true);
  xmlhttp.send();
}

/**
* Shortens username to maximum number of characters,
* as defined by @param max.
*/
function shorten(username, max) {
  if(username.length > max){
    return username.substring(0,max-3)+"...";
  } else {
    return username;
  }
}

/**
* Closes the sign-in-box for the user to enter details
*/
function closeSignIn() {
  var msgbox = document.getElementById("sign-in-box");
  var usernameInput = document.getElementById("username-sign-in-input");
  var passwordInput = document.getElementById("password-sign-in-input");
  usernameInput.value = '';
  passwordInput.value = '';
  msgbox.style.display = "none";
}

/**
* Closes the create-in-box for the user to enter details
*/
function closeCreateIn() {
  var msgbox = document.getElementById("create-in-box");
  var usernameInput = document.getElementById("username-create-in-input");
  var passwordInput = document.getElementById("password-create-in-input");
  usernameInput.value = '';
  passwordInput.value = '';
  msgbox.style.display = "none";
}

/**
* Closes the conversation-in-box for the user to enter conversation name
*/
function closeConversationIn() {
  var msgbox = document.getElementById("conversation-in-box");
  var titleInput = document.getElementById("conversation-in-input");
  titleInput.value = '';
  msgbox.style.display = "none";
}

/**
* Removes CodeU logo upon full page load
*/
$(window).on('load',function() {
  decideIfSignOn();
  setTimeout(function() {
    $("#message-panel").animate({ scrollTop: $('#message-panel').prop("scrollHeight")}, 1);
  }, 2);
  setTimeout(function(){
    // Animate loader off screen
    $(".se-pre-con").fadeOut("fast");
    $("body").removeClass("preload");
  }, 1);
});

/**
* Makes user links clickable by full username, not shortened display
*/
function addUNLinks(){
  allUNLinks = document.getElementsByClassName("username-link");
  for (i = 0; i < allUNLinks.length; i++) {
    var fullUsername = $(allUNLinks[i]).attr('keyword');
    allUNLinks[i].addEventListener("click", signIn);
    allUNLinks[i].addEventListener("click", checkForSignInSubmit);
  }
}

/**
* Makes conversation links clickable by full conversation name, not shortened display
*/
function addConversationLinks(){
  allConversationLinks = document.getElementsByClassName("conversation-link");
  for (i = 0; i < allConversationLinks.length; i++) {
    allConversationLinks[i].addEventListener("click", updateMessageList);
  }
}

/**
* A method that will regenerate the list of messages.
*/
function updateMessageList() {
  var mess_div = document.getElementById("messages-div");
  var title = this.innerHTML;
  var id = $(this).attr('i');
  if(!title){
    id = document.getElementById(ACTIVE_CONVERSATION_ID).id;
    title = document.getElementById(ACTIVE_CONVERSATION_ID).innerHTML;
  } else {
    mess_div.innerHTML = "<img class='loadingbig' src='img/loading.gif'></img>";
    ACTIVE_CONVERSATION_ID = id;
  }
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      mess_div.innerHTML = this.responseText;
      filter('messagebar-input','messages-div','message-link');
    }
  };
  xmlhttp.open("GET", "updateMessagesHandler.php?c="+ id + "&n=" + title + "&d=" + ACTIVE_UID, true);
  xmlhttp.send();
}

/**
* Attempts to create a conversation
*/
function attemptConversationCreate() {
  var message = document.getElementById("message-to-conversation-in");
  message.innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  var msgbox = document.getElementById("conversation-in-box");
  var titleInput = document.getElementById("conversation-in-input");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "created"){
        closeNav();
        titleInput.value = '';
        msgbox.style.display = "none";
        updateConversationList(false);
      } else {
        message.innerHTML = this.responseText;
        checkForCreateConversationSubmit();
      }
    }
  };
  xmlhttp.open("GET", "createConversationHandler.php?c=" + titleInput.value +"&u=" + ACTIVE_UID, true);
  xmlhttp.send();
}

/**
* Improves UI by preloading images
*/
function preloadImage(url) {
  var img = new Image();
  img.src=url;
}

/*
* Shows the sign out button if true
* Shows the create account button if false
*/
function toggleSignOutCreateLinks(signout) {
  var sign = document.getElementById("sign-out-link");
  var create = document.getElementById("create-account-link");
  if(signout){
    sign.style.display = "inline";
    create.style.display = "none";
  } else {
    sign.style.display = "none";
    create.style.display = "inline";
  }
}

/*
*
*
*/
function signOut(){
  var mess_div = document.getElementById("messages-div");
  mess_div.innerHTML = "<img class='loadingbig' src='img/loading.gif'></img>";
  ACTIVE_UID = -1;
  updateMessageList();
  toggleSignOutCreateLinks(false);
  document.getElementById("navbars").innerHTML = "&#9776; Sign-In";
  document.getElementById("create-conversation-link").style.display = "none";
  checkForEnableSubmit();
  closeNav();
}

/*
* Sets up the initial options of the page
*/
function setUpListenersAndStartingOptions(){
  preloadImage('img/loading.gif');
  document.getElementById("message-to-create-in").innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  document.getElementById("message-to-conversation-in").innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  document.getElementById("message-to-sign-in").innerHTML = "<img class='loading' src='img/loading.gif'></img>";
  addUNLinks();
  checkForEnableSubmit();
  checkForCreateAccountSubmit();
  checkForCreateConversationSubmit();
  checkForSignInSubmit();
  addConversationLinks();
  toggleSignOutCreateLinks(false);
  document.getElementById("create-conversation-link").style.display = "none";
  document.getElementById("cancel-sign-in-button").addEventListener("click", closeSignIn);
  document.getElementById("cancel-create-in-button").addEventListener("click", closeCreateIn);
  document.getElementById("cancel-conversation-in-button").addEventListener("click", closeConversationIn);
  document.getElementById("create-conversation-link").addEventListener("click", conversationIn);
  document.getElementById("create-account-link").addEventListener("click", createIn);
  document.getElementById("sign-in-button").addEventListener("click", attemptSignIn);
  document.getElementById("create-in-button").addEventListener("click", attemptCreate);
  document.getElementById("conversation-in-button").addEventListener("click", attemptConversationCreate);
  document.getElementById("message-input-button").addEventListener("click", attemptCreateMessage);
  document.getElementById("create-account-link").addEventListener("click", checkForCreateAccountSubmit);
  document.getElementById("create-conversation-link").addEventListener("click", checkForCreateConversationSubmit);
  document.getElementById("navbars").addEventListener("click", updateUserList);
    document.getElementById("sign-out-link").addEventListener("click", signOut);
  $('#username-create-in-input').bind('input', checkForCreateAccountSubmit);
  $('#password-create-in-input').bind('input', checkForCreateAccountSubmit);
  $('#conversation-in-input').bind('input', checkForCreateConversationSubmit);
  $('#username-sign-in-input').bind('input', checkForSignInSubmit);
  $('#password-sign-in-input').bind('input', checkForSignInSubmit);
  $('#message-input').bind('input', checkForEnableSubmit);
}
setUpListenersAndStartingOptions();
