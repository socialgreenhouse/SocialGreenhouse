<?php 
include ('includes/header.php');
?>
    <div id="contentcontainer">
        <div id="content">
            <p>
                <b>Requirements:</b><br />
                •  	Microsoft Windows XP of hoger / Linux<br />
                •	Een Webserver met PHP  (Geen IIS i.v.m. htaccess)<br />
                •	Java JRE<br />
                •	VertX <a href="http://vertx.io/install.html" target="_blank">(http://vertx.io/install.html)</a> <br />
                •	MySQL Server<br /><br />

                <b>Stappen: </b><br />
                1.	Verbind met je MySQL server.<br />
                2.	Maak een database met user speciaal voor je Social Greenhouse<br />
                3.	Laad het <a href="#" target="_blank">database bestand</a> naar de net gemaakte database.<br /><br />

                4.	Plaats de webservice bestanden in een gewenste locatie binnen je wwwroot/httpdocs map.<br />
                5.	Pas in index.php de database gegevens aan naar de net aangemaakte database.<br />
                6.	Sla het bestand op.<br />
                7.	Test de verbinding door, naar http://(Locatie webservice)/index.php/modules te gaan.<br /><br />

                8.	Plaats de SocialGreenhouseServer bestanden op de gewenste locatie op de server.<br />
                9.	Pas in src/com/socialgreenhouse/server/Server.java de database gegevens aan naar de net aangemaakte database.<br />
                10.	Sla het bestand op.<br />
                11.	Start nu de server door doormiddel van het programma run.bat(windows) of run.sh(linux).<br /><br />

                Gefeliciteerd je hebt nu je Social Greenhouse server draaien.

            </p>
        </div>
    </div>
<?php 
include ('includes/footer.php');
?>
