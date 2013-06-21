<?php
define("MYSQL_HOST", "localhost");
define("MYSQL_USER", "socialgreenhouse");
define("MYSQL_PASSWORD", "gr33nhous3");
define("MYSQL_DATABASE", "socialgreenhouse");

$conn = mysqli_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASSWORD);
mysqli_select_db($conn, MYSQL_DATABASE);

$query = "SELECT * FROM View_Tablet";
$result = mysqli_query($conn, $query);
$array = array();
while ($row = mysqli_fetch_assoc($result)) {
    $array[] = $row;
}

$json = json_encode($array);

echo $json;

//echo json_encode($array);

mysqli_close($conn);
?>