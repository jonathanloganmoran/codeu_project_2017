<?php

  $connection = new SQLConnector();
  $conversation = $_REQUEST["c"];
  echo $connection->getMessages();

?>
