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
* Makes user links clickable by full username, not shortened display
*/
allUNLinks = document.getElementsByClassName("username-link");
for (i = 0; i < allUNLinks.length; i++) {
  var fullUsername = $(allUNLinks[i]).attr('keyword');
  allUNLinks[i].addEventListener("click", signIn);
}
document.getElementById("cancel-button").addEventListener("click", closeSignIn);

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
