<?php

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

require_once __DIR__ . '/vendor/autoload.php';

$app = new Silex\Application();

$app['exception_handler']->disable();
//error_reporting(-1);


/*
*
*/
$app->before(function (Request $request) {
            if (0 === strpos($request->headers->get('Content-Type'), 'application/json')) {
		file_put_contents('lox.txt', $request->getContent());
                $data = json_decode($request->getContent(), true);
                $request->request->replace(is_array($data) ? $data : array());
            }
        });

$app['database'] = $app->share(function () {
            $database = new PDO('mysql:host=localhost;dbname=socialgreenhouse', 'socialgreenhouse', 'gr33nhous3');
            $database->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            return $database;
        });

$app->get('/modules', function () use ($app) {
    $query = "SELECT * FROM View_Tablet";
    $result = $app['database']->query($query)->fetchAll(PDO::FETCH_ASSOC);
    foreach($result as $key => $value)
    {
	$result[$key]['SerialNo'] = (int) $value['SerialNo'];	
    }
    return $app->json($result, 200);
});

$app->post('/modules', function (Request $request) use ($app) {
    $name = $request->request->get('Name');
    $serialNo = $request->request->get('SerialNo');
    $sensor = $request->request->get('SensorType');
	$triggerData = $request->request->get('TriggerData');

    $stmt = $app['database']->prepare("SELECT * FROM Module WHERE SerialNo = ?");
    $stmt->execute(array($serialNo));
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (sizeof($rows) == 1) {
        $stmt = $app['database']->prepare("UPDATE `Module` SET `Name` = ?, `TriggerData` = ?  WHERE `SerialNo` = ?");
        $stmt->execute(array($name, $triggerData, $serialNo));
    } else {
        $stmt = $app['database']->prepare("INSERT INTO `Module` (`SerialNo`, `Name`, `Sensor`) VALUES (?, ?, ?)");
        $stmt->execute(array($serialNo, $name, $sensor));
    }
    return new Response('', 201);
});

$app->delete("/modules/{serialNo}", function($serialNo) use ($app)
{
    $stmt = $app['database']->prepare("DELETE FROM `Module` WHERE SerialNo = ?");
    $stmt->execute(array($serialNo));
    return new Response('', 204);
});

$app->get("/settings", function() use($app)
{
    $query = "SELECT * FROM Settings";
    $result = $app['database']->query($query)->fetchAll(PDO::FETCH_ASSOC);
    $returnArray = array();
    foreach($result as $key => $value)
    {
	$returnArray[$value['Name']] = $value['Value'];
    }
    return $app->json($returnArray, 200);
});

$app->post("/triggers", function(Request $request) use ($app){
	$serialNo = $request->request->get('SerialNo');
	$triggerdata = $request->request->get('TriggerData');
	
	$stmt = $app['database']->prepare("UPDATE `Module` SET `TriggerData` = ? WHERE `SerialNo` = ?");
	$stmt->execute(array($triggerdata, $serialNo));
	return new Response('', 201);
	
});


$app->post("/settings", function (Request $request) use ($app){
    $token = $request->request->get('AccessToken');
    $secret= $request->request->get('AccessTokenSecret');

    $stmt = $app['database']->prepare("UPDATE `Settings` SET `Value` = ? WHERE `Name` = 'AccessToken'");
    $stmt->execute(array($token));

    $stmt = $app['database']->prepare("UPDATE `Settings` SET `Value` = ? WHERE `Name` = 'AccessTokenSecret'");
    $stmt->execute(array($secret));
    return new Response('', 204);
});

$app->run();

?>