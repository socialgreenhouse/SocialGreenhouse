<?php


define("MYSQL_HOST", "it-server.nl");
define("MYSQL_USER", "socialgreenhouse");
define("MYSQL_PASSWORD", "gr33nhous3");
define("MYSQL_DATABASE", "socialgreenhouse");

$headers = http_get_request_body();

$handle = fopen("log.txt", 'w');

fwrite($handle, $headers);
fclose($handle);



function checkIfExistsInArray($array, $serialno)
{
    foreach ($array as $item) {
        if ($item['SerialNo'] == $serialno)
            return true;
    }
    return false;
}


if(!isset($_POST['data']))
{
    die();
}

if(strlen($_POST['data']) < 2)
{
    die();
}

$conn = mysqli_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASSWORD);
mysqli_select_db($conn, MYSQL_DATABASE);

$data = json_decode($_POST['data']);

for($i = 0; $i < sizeof($data); $i++)
{
    if(isset($data[$i]["SerialNo"]) && isset($data[$i]["Name"]) && isset($data[$i]["Sensor"]))
    {
        $result = mysqli_query($conn, "SELECT * FROM Module WHERE SerialNo = '".$data[$i]["SerialNo"]."'");
        if(mysqli_num_rows($result))
        {
            mysqli_query($conn, "UPDATE Module SET Name = '".$data[$i]["Name"]."', Sensor = '".$data[$i]["Sensor"]."' WHERE SerialNo = '".$data[$i]["SerialNo"]."'");
        }
        else
        {
            mysqli_query($conn, "INSERT INTO Module VALUES ('".$data[$i]["SerialNo"]."', '".$data[$i]["Name"]."', '".$data[$i]["Sensor"]."')");
        }
    }
    
    $result = mysqli_query($conn, "SELECT * FROM Module");
    if(sizeof($data) > 0)
    {
        while($row = mysqli_fetch_assoc($result))
        {
            if(!checkIfExistsInArray($data, $row["SerialNo"]))
            {
                mysqli_query($conn, "DELETE FROM Module WHERE SerialNo = '".$row["SerialNo"]."'");
            }
        }
    }
    else
    {
        mysqli_query($conn, "TRUNCATE TABLE Module");
    }
}

mysqli_close($conn);

?>
