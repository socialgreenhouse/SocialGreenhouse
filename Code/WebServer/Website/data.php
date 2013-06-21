<?

include('includes/connect.php');
$sqlTemp = "SELECT Time, Value FROM `State` WHERE `ModuleID` = '10000002' ORDER BY `Time` DESC LIMIT 1";
$queryTemp = mysql_query($sqlTemp);
$resultTemp = mysql_fetch_assoc($queryTemp);
$temp = $resultTemp['Value'];
$stamp1 = $resultTemp['Time'];
$time1 = date("d-m-y H:i",$stamp1);
                  
$sqlGround = "SELECT Time, Value FROM `State` WHERE `ModuleID` = '10000001' ORDER BY `Time` DESC LIMIT 1";
$queryGround = mysql_query($sqlGround);
$resultGround = mysql_fetch_assoc($queryGround);
$humidity = $resultGround['Value'];
$stamp2 = $resultGround['Time'];
$time2 = date("d-m-y H:i",$stamp2);
/*
$sqlTime = "SELECT Time FROM `State` ORDER BY `Time` DESC LIMIT 1";
$queryTime = mysql_query($sqlTime);
$resultTime = mysql_fetch_assoc($queryTime);
$stamp = $resultTime['Time'];
$time = date("d-m-y H:i",$stamp);
*/
echo $time1."|".$time2."|".$temp."|".$humidity;

?>