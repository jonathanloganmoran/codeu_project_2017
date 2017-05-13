<?php

class SQLConnector {

  protected static $connection;

  function __construct() {
  }

  /**
  * Connect to the database
  *
  * @return bool false on failure / mysqli MySQLi object instance on success
  */
  public function connect() {
    // Try and connect to the database
    if(!isset(self::$connection)) {
      // Load configuration as an array.
      $config = parse_ini_file('config.ini');
      self::$connection = new mysqli($config['dbhost'],$config['username'],$config['password'],$config['dbname']);
    }
    // If connection was not successful, handle the error
    if(self::$connection === false) {
      // Handle error - notify administrator, log to a file, show an error screen, etc.
      return false;
    }
    return self::$connection;
  }

  /**
  * Query the database
  *
  * @param $query The query string
  * @return mixed The result of the mysqli::query() function
  */
  public function query($query) {
    // Connect to the database
    $connection = $this -> connect();
    // Query the database
    $result = $connection -> query($query);
    return $result;
  }

  /**
  * Fetch rows from the database (SELECT query)
  *
  * @param $query The query string
  * @return bool False on failure / array Database rows on success
  */
  public function select($query) {
    $rows = array();
    $result = $this -> query($query);
    if($result === false) {
      return false;
    }
    while ($row = $result -> fetch_assoc()) {
      $rows[] = $row;
    }
    return $rows;
  }

  /**
  * Fetch the last error from the database
  *
  * @return string Database error message
  */
  public function error() {
    $connection = $this -> connect();
    return $connection -> error;
  }

  /**
  * Quote and escape value for use in a database query
  *
  * @param string $value The value to be quoted and escaped
  * @return string The quoted and escaped string
  */
  public function quote($value) {
    $connection = $this -> connect();
    return "'" . $connection -> real_escape_string($value) . "'";
  }

  /**
  * Returns "valid" if account username and password match,
  * "invalid" otherwise.
  * Should be completely functional, however SQLI attacks
  * not yet protected against.
  */
  public function authenticateAccount($username, $password) {
    $SQL_SELECT_PASSWORD = "SELECT password FROM User WHERE username = '" . $username ."'";
    $salt = $this->acquireSalt($username);
    $input = $salt . $password;
    $hash = hash("sha256", utf8_encode($input));
    $result = $this -> query($SQL_SELECT_PASSWORD);
    $rows = array();
    $users = "";
    if($result === false) {
      return "invalid";
    }
    $row = $result -> fetch_assoc();
    $passwordEncrypted = $row["password"];
    if($hash === $passwordEncrypted){
      return "valid";
    }
    return "invalid";
  }

  /*
  * Private helper method to acquire salt
  * to be used in decryption of password.
  */
  private function acquireSalt($username) {
    $SQL_SELECT_SALT = "SELECT salt FROM User WHERE username = '" . $username . "'";
    $result = $this -> query($SQL_SELECT_SALT);
    $rows = array();
    $users = "";
    if($result === false) {
      return false;
    }
    $row = $result -> fetch_assoc();
    return $row["salt"];
  }

  /**
  * Returns the formatted usernames of all users.
  */
  public function getUsers() {
    $SQL_SELECT_ALL = "SELECT username FROM User";
    $rows = array();
    $users = "";
    $result = $this -> query($SQL_SELECT_ALL);
    if($result === false) {
      return false;
    }
    while ($row = $result -> fetch_assoc()) {
      $fullun = $row["username"];
      $shortun = $fullun;
      // Truncate username to 18 characters max
      if(strlen($shortun) > 25){
        $shortun = substr($shortun,0,22) . "...";
      }
      $users .= "<a keyword='" . $fullun . "' class='username-link'>" . $shortun . "</a>";
    }
    return $users;
  }

  /*
  * Returns the username of user with UUID $uuid
  * Returns null if no user was found
  */
  private function getUsernameFromUUID($uuid){
    $SQL_UUID_LOOKUP = "SELECT username FROM User WHERE Uuid = '" . $uuid . "'";
    $rows = array();
    $users = "";
    $result = $this -> query($SQL_UUID_LOOKUP);
    if($result === false) {
      return NULL;
    }
    $row = $result -> fetch_assoc();
    return $row["username"];
  }

  /*
  * Returns the uuid of user with username $username
  * Returns null if no user was found
  */
  private function getUUIDFromUsername($username){
    $SQL_USERNAME_LOOKUP = "SELECT * FROM User WHERE username = '" . $username . "'";
    $rows = array();
    $users = "";
    $result = $this -> query($SQL_USERNAME_LOOKUP);
    if($result === false) {
      return NULL;
    }
    $row = $result -> fetch_assoc();
    return $row["Uuid"];
  }

  /**
  * Returns the formatted list of all conversations.
  *
  */
  public function getConversations() {
    $SQL_SELECT_CONV = "SELECT * FROM Conversation ORDER BY creation_time ASC";
    $rows = array();
    $result = $this -> query($SQL_SELECT_CONV);
    if($result === false) {
      return false;
    }
    $conversations = "";
    while ($row = $result -> fetch_assoc()) {
      $convname = $row["title"];
      $uuid = $row["Uuid"];
      $iduser = $row["id_user"];
      $fullconvname = $convname;
      // Truncate conversation name to 28 characters max
      if(strlen($convname) > 28){
        $convname = substr($convname,0,25) . "...";
      }
      $conversations .= "<a i='" . $uuid . "'" . " o='" . $this->getUsernameFromUUID($iduser) . "'" . " keyword='" . $fullconvname . "' class='conversation-link' id='" . $uuid . "'>" . $convname . "</a>";
    }
    return $conversations;
  }

  /**
  * Returns the formatted list of all messages.
  * Not currently functional -- just for viewing purposes.
  */
  public function getMessages($conversationid, $conversationtitle) {
    $messages = "<div class='more-padded-below'><i class='begin-conversation'>- Beginning of Conversation " . $conversationtitle . " -</i></div>";
    for ($x = 0; $x <= strlen($conversationtitle); $x++) {
      if($x & 1) {
        //can add for profile pictures: <img class='profile-picture' src='img/no-text.png'></img>
        $messages .= "<div class='message-link bubble'>" . "<span class='author-link'>David:</span> Lorem ipsum dolor sit amet, consectetur adipiscing elit " . $x . ".</div>";
      } else {
        $messages .= "<div class='message-link bubble bubble--alt'>" . "Ut enim ad minim veniam, quis nostrud " . $x . ".</div>";
      }
    }
    return $messages;
  }

  public function getFooter(){
    $footer = "<footer id='footer'><div class='copyright'>Copyright &copy; 2017 <a target='_blank' href='https://developers.google.com/'>CodeU</a></div></footer>";
    return $footer;
  }


  /**
  * Returns "valid" if conversation is valid string format
  * Returns "invalid" if conversation is not
  */
  public function validateConversationFormat($string) {
    if(!(preg_match("/^[a-zA-Z0-9]+$/", $string) == 1)){
      return "invalid";
    }
    if(strlen($string) < 2){
      return "invalid";
    }
    return "valid";
  }


  /**
  * Returns "valid" if username is valid string format
  * Returns "invalid" if username is not
  */
  public function validateUsernameFormat($string) {
    if(!(preg_match("/^[a-zA-Z0-9]+$/", $string) == 1)){
      return "invalid";
    }
    if(strlen($string) < 1){
      return "invalid";
    }
    return "valid";
  }

  /**
  * Returns "valid" if password is valid string format
  * Returns "invalid" if password is not
  */
  public function validatePasswordFormat($string) {
    if(strlen($string) < 4){
      return "invalid";
    }
    return "valid";
  }

  /**
  * Returns "present" if username is already in database
  * Returns "absent" if username is not
  */
  private function checkIfUsernameExistsInDB($username) {
    $SQL_SELECT_USER = "SELECT username FROM User WHERE username = '" . $username . "'";
    $result = $this -> query($SQL_SELECT_USER);
    $rows = array();
    if($result === false) {
      return "absent";
    }
    $row = $result -> fetch_assoc();
    if($row === NULL){
      return "absent";
    }
    return "present";
  }

  /**
  * Adds an account into the database.
  * Does not check inputs: not to be called directly.
  * Returns "invalid" if not added
  * Returns "valid" if added
  */
  private function addAccount($username, $password) {
    $salt = $this->generateSalt();
    $input = $salt . $password;
    $encryptedPassword = hash("sha256", utf8_encode($input));
    $uuid = $this->generateUuid();
    $SQL_ADD_ACCOUNT = "INSERT INTO User (username, password, salt, Uuid) VALUES ('";
    $SQL_ADD_ACCOUNT = $SQL_ADD_ACCOUNT . $username . "','" . $encryptedPassword;
    $SQL_ADD_ACCOUNT = $SQL_ADD_ACCOUNT . "','" . $salt . "','" . $uuid . "')";
    $result = $this -> query($SQL_ADD_ACCOUNT);
    if($result === false) {
      return "invalid";
    }
    return "valid";
  }

  /*
  * Generates a UUID
  */
  private function generateUuid(){
    $num = "100.101." . $this->randomNumber();
    // to-do: make sure ID is not already taken
    $id = "[UUID:" . $num . "]";
    return $id;
  }

  /*
  * Generates a random number of length 9
  */
  private function randomNumber() {
    $length = 9;
    $result = '';
    for($i = 0; $i < $length; $i++) {
      $result .= mt_rand(0, 9);
    }
    return $result;
  }

  /*
  * Generates a random salt string for encryption
  */
  private function generateSalt(){
    $length = 8;
    $string = "";
    for($i=0; $i < $length; $i++){
      $x = mt_rand(0, 2);
      switch($x){
        case 0: $string.= chr(mt_rand(97,122));break;
        case 1: $string.= chr(mt_rand(65,90));break;
        case 2: $string.= chr(mt_rand(48,57));break;
      }
    }
    return $string;
  }

  /**
  * Adds a conversation into the database.
  * Does not check inputs: not to be called directly.
  * Returns "invalid" if not added
  * Returns "valid" if added
  */
  private function addConversation($username, $title){
    $id_user = $this->getUUIDFromUsername($username);
    $uuid = $this->generateUuid();
    $SQL_INSERT_CON = "INSERT INTO Conversation (Uuid, id_user, title) VALUES";
    $SQL_INSERT_CON = $SQL_INSERT_CON . "('" . $uuid . "','" . $id_user . "','" . $title . "')";
    $result = $this -> query($SQL_INSERT_CON);
    if($result === false) {
      return "invalid";
    }
    return "valid";
  }

  /**
  * Returns "created" if conversation was able to be created,
  * "Conversation not created: <REASON>" otherwise.
  */
  public function createConversation($conversation, $user){
    if($this->validateConversationFormat($conversation) === "invalid"){
      return "Conversation not created. Format of title invalid.";
    }
    $results = $this->addConversation($user,$conversation);
    if($results === "valid"){
      return "created";
    }
    return "Conversation already exists. Please try a new title.";
  }

  /**
  * Returns "created" if account was able to be created,
  * "Account not created: <REASON>" otherwise.
  */
  public function createAccount($username, $password) {
    if($this->validateUsernameFormat($username) === "invalid"){
      return "Account not created. Format of username invalid.";
    }
    if($this->validatePasswordFormat($password) === "invalid"){
      return "Account not created. Format of password invalid.";
    }
    if($this->checkIfUsernameExistsInDB($username) === "present"){
      return "Account not created. Username already in use.";
    }
    $results =  $this->addAccount($username, $password);
    if($results === "valid"){
      return "created";
    }
    return "Account not created. Please try again later.";
  }
}

?>
