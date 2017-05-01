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

  /**
  * Returns the formatted list of all conversations.
  * Not currently functional -- just for viewing purposes.
  */
  public function getConversations() {
    $conversations = "";
    for ($x = 0; $x <= 150; $x++) {
      $convname = "Conversation " . $x . " Test";
      $fullconvname = $convname;
      // Truncate conversation name to 28 characters max
      if(strlen($convname) > 28){
        $convname = substr($convname,0,25) . "...";
      }
      $conversations .= "<a keyword='" . $fullconvname . "' class='conversation-link'>" . $convname . "</a>";
    }
    return $conversations;
  }

  /**
  * Returns the formatted list of all messages.
  * Not currently functional -- just for viewing purposes.
  */
  public function getMessages() {
    $messages = "";
    for ($x = 0; $x <= 50; $x++) {
      if($x & 1) {
        //can add for profile pictures: <img class='profile-picture' src='img/no-text.png'></img>
        $messages .= "<div class='message-link bubble'>" . "<span class='author-link'>David:</span> Lorem ipsum dolor sit amet, consectetur adipiscing elit " . $x . ".</div><br>";
      } else {
        $messages .= "<div class='message-link bubble bubble--alt'>" . "Ut enim ad minim veniam, quis nostrud " . $x . ".</div><br>";
      }
    }
    return $messages;
  }

  /**
  * Returns "valid" if account username and password match,
  * "invalid" otherwise.
  * Not currently functional.
  */
  public function authenticateAccount($username, $password) {
    if($password === "password"){
      return "valid";
    }
    return "invalid";
  }

  /**
  * Returns "created" if account was able to be created,
  * "Account not created: <REASON>" otherwise.
  * Not currently functional.
  */
  public function createAccount($username, $password) {
    if($password === "password"){
      return "created";
    }
    return "Account not created: method not yet implemented.";
  }
}

?>
