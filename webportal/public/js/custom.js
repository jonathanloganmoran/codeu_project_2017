/*
* Constants used in determining how often to refresh page contents.
*/
var INTERVAL_TO_REFRESH_USERS = 15000;
var INTERVAL_TO_REFRESH_CONVERSATIONS = 1000;
var INTERVAL_TO_REFRESH_MESSAGES = 500;

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
  //document.getElementById("navbars").style.display ="none";
}

/**
* Closes navigation bar
*/
function closeNav() {
  document.getElementById("mySidenav").style.width = "0";
  //document.getElementById("navbars").style.display ="block";
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
}

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
}

/**
* Makes user links clickable by full username, not shortened display
*/
function addUNLinks(){
  allUNLinks = document.getElementsByClassName("username-link");
  for (i = 0; i < allUNLinks.length; i++) {
    var fullUsername = $(allUNLinks[i]).attr('keyword');
    allUNLinks[i].addEventListener("click", signIn);
  }
}

/**
*  Only allow user to send a message that is valid.
*/
function checkForEnableSubmit(){
  // Can add more text validation here if necessary
  if(document.getElementById("message-input").value.length > 0) {
    document.getElementById("message-input-button").disabled = false;
  } else {
    document.getElementById("message-input-button").disabled = true;
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
  updateConversationList();
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
  var msgbox = document.getElementById("sign-in-box");
  var usernameInput = document.getElementById("username-sign-in-input");
  var passwordInput = document.getElementById("password-sign-in-input");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "valid"){
        document.getElementById("welcome-message").innerHTML = "Hello,<br>" + shorten(usernameInput.value, 10);
        usernameInput.value = '';
        passwordInput.value = '';
        msgbox.style.display = "none";
      } else {
        passwordInput.value = '';
        var message = document.getElementById("message-to-sign-in");
        message.innerHTML = "Invalid account details!";
      }
    }
  };
  xmlhttp.open("GET", "authenticateAccountHandler.php?u=" + usernameInput.value +"&p=" + passwordInput.value , true);
  xmlhttp.send();
}


/**
* Attempts to create an account
*/
function attemptCreate() {
  var msgbox = document.getElementById("create-in-box");
  var usernameInput = document.getElementById("username-create-in-input");
  var passwordInput = document.getElementById("password-create-in-input");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      if(this.responseText == "created"){
        closeNav();
        document.getElementById("welcome-message").innerHTML = "Hello,<br>" + shorten(usernameInput.value, 10);
        usernameInput.value = '';
        passwordInput.value = '';
        msgbox.style.display = "none";
      } else {
        passwordInput.value = '';
        var message = document.getElementById("message-to-create-in");
        message.innerHTML = this.responseText;
      }
    }
  };
  xmlhttp.open("GET", "createAccountHandler.php?u=" + usernameInput.value +"&p=" + passwordInput.value , true);
  xmlhttp.send();
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
function updateConversationList() {
  var conv_div = document.getElementById("conversation-list");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      conv_div.innerHTML = this.responseText;
      filter('conversation-input-box','conversation-list','conversation-link');
      // Will need to regenerate href paths for convs here
    }
  };
  xmlhttp.open("GET", "updateConversationListHandler.php", true);
  xmlhttp.send();
}

/**
* A method that will regenerate the list of messages.
*/
function updateMessageList() {
  var mess_div = document.getElementById("messages-div");
  var xmlhttp = new XMLHttpRequest();
  xmlhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      mess_div.innerHTML = this.responseText;
      filter('messagebar-input','messages-div','message-link');
    }
  };
  xmlhttp.open("GET", "updateMessagesHandler.php?c="+"testConversation", true);
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

// Add listeners
addUNLinks();
checkForEnableSubmit();
document.getElementById("cancel-sign-in-button").addEventListener("click", closeSignIn);
document.getElementById("cancel-create-in-button").addEventListener("click", closeCreateIn);
document.getElementById("create-account-link").addEventListener("click", createIn);
document.getElementById("sign-in-button").addEventListener("click", attemptSignIn);
document.getElementById("create-in-button").addEventListener("click", attemptCreate);
$('#message-input').bind('input', checkForEnableSubmit);
