<?php 
include ('includes/connect.php');
include ('includes/header.php');

?>
<script src="http://code.jquery.com/jquery-1.9.0.js"></script>
<script>
//Load data every 30 seconds
setInterval(update, 30000);

function update(){
  $.ajax({
    url: "data.php",
    cache: false
  }).done(function( html ) {
    var test = html.split("|");
    $("#tijdTemp").html(test[0]);
    $("#tijdGrond").html(test[1]);
    $("#temp").html(test[2]);
    $("#humidity").html(test[3]);
  });
}

//Load data for the first time
update();
</script>
        <div id="contentcontainer">
            
            <!--
            <div id="content">
                U dient in te loggen om deze pagina te bekijken.<br /><br />
                
                <form action="yoursocialgreenhouse.php" method="post">
                    <input type="text" class="input" name="inlognaam" placeholder="Uw inlognaam"/> Inlognaam<br />
                    <input type="password" class="input" name="wachtwoord" placeholder="Uw wachtwoord"/> Wachtwoord<br /><br />
                    <input type="submit" class="submit" name="inloggen" value="Inloggen"/>
                    <?php
                    /*
                        if($error == true)
                        {
                            echo "<br />Uw inlognaam en wachtwoord komen niet overeen.";
                        }
                     */
                     
                    ?>
                </form>
                
                
            </div>
            
            -->
            
            <div id="content">
                
                <div id="leftcontent">
                    <?php
                    /*
                    $sql = "SELECT * FROM State WHERE id IN ( SELECT MAX( ID ) FROM State GROUP BY ModuleID )ORDER BY  `State`.`Time` DESC ";
                    $query = mysql_query($sql);
                    
                    $stamp = 0;
                    while($result = mysql_fetch_assoc($query))
                    {
                        if($result['Time'] > $stamp)
                        {
                            $stamp = $result['Time'];
                        }

                        if($result['ModuleID'] == "A0A01")
                        {
                            $humidity = $result['Value'];
                        }
                        else if($result['ModuleID'] == "A0B01")
                        {
                            $temp = $result['Value'];
                        }                 
                    }
                    $time = date("d-m-y H:i",$stamp); */  
                                       /*
                    $sqlTemp = "SELECT * FROM `State` WHERE `ModuleID` = 'A0B01' ORDER BY `Time` DESC LIMIT 1";
                    $queryTemp = mysql_query($sqlTemp);
                    $resultTemp = mysql_fetch_assoc($queryTemp);
                    $temp = $resultTemp['Value'];
                                      
                    $sqlGround = "SELECT * FROM `State` WHERE `ModuleID` = 'A0A01' ORDER BY `Time` DESC LIMIT 1";
                    $queryGround = mysql_query($sqlGround);
                    $resultGround = mysql_fetch_assoc($queryGround);
                    $humidity = $resultGround['Value'];
                    
                    $sqlTime = "SELECT Time FROM `State` ORDER BY `Time` DESC LIMIT 1";
                    $queryTime = mysql_query($sqlTime);
                    $resultTime = mysql_fetch_assoc($queryTime);
                    $stamp = $resultTime['Time'];
                    $time = date("d-m-y H:i",$stamp);
                    */
                    ?>
                
                    <p style="float:right; font-size: 10px;">
                        Laatste update grondvochtigheid: <span id="tijdGrond"></span><br /><br />
                        Laatste update temperatuur: <span id="tijdTemp"></span>
                        
                    </p>
                    <p>
                        Grondvochtigheid: <span id="humidity"></span><br />
                        Temperatuur : <span id="temp"></span>
                        <br /><br />
                        > 89.0 = Nat<br />
                        70.0 - 89.0 = Goed<br />
                        < 70.0 = Droog
                    </p>
                </div>
                
                <div id="rightcontent">
                    <a class="twitter-timeline"  width="250" height="350" href="https://twitter.com/inf2c"  data-widget-id="334624265339023360">Tweets van @inf2c</a>
                    <script>
                            !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';
                            if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";
                                fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");      
                    </script>
                    
                </div>
                <div class="clear"></div>
            </div>
            
            
        </div>
<?php 
include ('includes/footer.php');
?>