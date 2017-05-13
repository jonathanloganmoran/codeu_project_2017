<?php

$connection = new SQLConnector();
$id = $_REQUEST["c"];
$title = $_REQUEST["n"];
echo $connection->getMessages($id,$title);

?>
