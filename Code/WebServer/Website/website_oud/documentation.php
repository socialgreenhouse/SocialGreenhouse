<?php 
include ('includes/header.php');
?>
        <div id="contentcontainer">
            <div id="content">
                Hieronder staat alle documentatie met betrekking tot het project Social Greenhouse.<br /><br />
                <ul>
                    <?php
                    $dir    = getcwd() . '/../documents';
                    $files = scandir($dir);
                    foreach($files as $file)
                    {
                        if($file == "." || $file == "..")
                        {}
                        else
                        {
                            echo "<li><a href='documents/$file'>$file</a></li>";
                        }
                    }
                    ?>
                </ul>
            </div>
        </div>
<?php 
include ('includes/footer.php');
?>