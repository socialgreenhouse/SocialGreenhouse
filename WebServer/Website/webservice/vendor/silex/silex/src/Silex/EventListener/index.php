<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <?php
       include 'include/db.php';
    ?>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<title>Houkes Bewindvoerders -- Snipperkaart-Online</title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="style.css" rel="stylesheet" type="text/css" media="screen" />
</head>
<body>
<div id="wrapper">
	<div id="logo">
		<h1>Houkes Bewindvoerders</h1>
		<p><em>Snipperkaart Online</em></p>
	</div>
	<hr />
	<!-- end #logo -->
	<div id="header">
		<div id="menu">
			<ul>
				<li class="current_page_item"><a href="index.php" class="first">Inloggen</a></li>
                                <li><a href="secure/medewerker/index.php">Medewerkerportaal</a></li>
			</ul>
		</div>
		<!-- end #menu -->
		<!-- end #search -->
	</div>
	<!-- end #header -->
	<!-- end #header-wrapper -->
	<div id="page">
	<div id="page-bgtop">
    
		<div id="content">
			<div class="post">
			<?php
                        if(isset($_POST['submit']))
                        {
                            //check strings -->
                            if(strlen($_POST['gebruikersnaam']) == 0)
                            {
                                echo "Het veld gebruikersnaam is niet ingevuld";
                            }
                            elseif(strlen($_POST['wachtwoord']) == 0)
                            {
                                echo "Het veld wachtwoord is niet ingevuld.";
                            }
                            else
                            {
                                $wachtwoord = md5($_POST['wachtwoord']);
                                $query = "SELECT * FROM gebruikers WHERE gebruikersnaam = '".$_POST['gebruikersnaam']."' && wachtwoord = '".$wachtwoord."'";
                                $result = mysql_query($query);
                                $rows = mysql_num_rows($result);
                                if($rows)
                                {
                                    echo "U bent succesvol ingelogd. De pagina vernieuwd zich in 3 seconden.";
                                    $_SESSION['logged'] = 1;
                                    $_SESSION['ingelogd'] = time();
                                    header("Refresh: 3;url=secure/index.php");
                                }
                                else
                                {
                                    echo "Uw wachtwoord en gebruikersnaam komen niet overeen.";
                                }
                            }
                            //<--Einde string checks
                        }
                        else
                        {
                        ?>
                        <center>
                            <form method="post" action="index.php">
                                <table width="200px">
                                    <tr>
                                        <td colspan="2" width="200px"><b>Login</b></td>
                                    </tr>
                                    <tr>
                                        <td width="100px">Gebruikersnaam:</td>
                                        <td width="100px"><input type="text" name="gebruikersnaam" /></td>
                                    </tr>
                                    <tr>
                                        <td width="100px">Wachtwoord:</td>
                                        <td width="100px"><input type="password" name="wachtwoord" /></td>
                                    </tr>
                                    <tr>
                                        <td width="200px" colspan="2"><input type="submit" name="submit" value="login" /></td>
                                    </tr>    
                                </table>
                            </form>
                        </center>
                        <?php
                        }
                        ?>	
			</div>
</div>
		<!-- end #content -->
		
		<div style="clear: both;">&nbsp;</div>
	</div>
	</div>
	<!-- end #page -->
	<div id="footer-bgcontent">
	<div id="footer">
		<p>Snipperkaart-Online &copy; 2011 -- Licentie verleend aan Houkes Bewindvoerders BV.</p>
	</div>
	</div>
        
	<!-- end #footer -->
</div>
</body>
</html>

