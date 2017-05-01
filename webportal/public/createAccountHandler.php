<?php

$connection = new SQLConnector();
$username = $_REQUEST["u"];
$password = $_REQUEST["p"];
echo $connection->createAccount($username, $password);

?>
