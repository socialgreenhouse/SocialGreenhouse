<?php
session_start();

$inlognaam = "123456";
$wachtwoord = "Admin";
$error = false;

$url = str_replace("/SocialGreenhouse/","", $_SERVER['REQUEST_URI']);

if(isset($_POST['inloggen']))
{
    if($_POST['inlognaam'] == $inlognaam && $_POST['wachtwoord'] == $wachtwoord)
    {
        $_SESSION['username'] = $inlognaam;
    }
    else
    {
        $error = true;
    }
}

if (!isset($_SESSION['username']) && $url == "/yourgreenhouse.php")
{
    header("location:yoursocialgreenhouse.php");
}

if (isset($_SESSION['username']) && $url == "/yoursocialgreenhouse.php")
{
    header("location:yourgreenhouse.php");
}
?>


<!DOCTYPE html>
<html bgcolor="#3d3d3c">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="style.css" type="text/css" />
        <title>Social Greenhouse</title>
    </head>
    <body>
        <div id="header">
            <div id="headercenter">
                <img src="img/logo.png" alt="logo" />
            </div>
        </div>
        <div id="menu">
            <div id="menucenter">
                <ul>
                    <li class="menuitem"><a href="index.php">Home</a></li>
                    <li class="menuitem"><a href="yoursocialgreenhouse.php">Uw Social Greenhouse</a></li>
                    <li class="menuitem"><a href="documentation.php">Documentatie</a></li>
                </ul>
                <div style="clear:both"></div>
            </div>
        </div>