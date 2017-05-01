<?php

$connection = new SQLConnector();
$username = $_REQUEST["u"];
$password = $_REQUEST["p"];
echo $connection->authenticateAccount($username, $password);

?>
