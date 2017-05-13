<?php

$connection = new SQLConnector();
$title = $_REQUEST["c"];
$user = $_REQUEST["u"];
echo $connection->createConversation($title, $user);

?>
