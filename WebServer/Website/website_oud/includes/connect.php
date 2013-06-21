<?php
// verbinding met de server maken

$Connect = mysql_connect("127.0.0.1", "socialgreenhouse", "gr33nhous3");
if ($Connect === FALSE)
{
    echo("<p>Error code " . mysql_errno() . ": " . mysql_error() . "</p>\n");
}
// database selecteren
$DBName = "socialgreenhouse";
mysql_select_db($DBName, $Connect);

?>