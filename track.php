<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function track($pid, $did){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  $link = @Conection(new json());
  

 	  $sql = "SELECT `patient`.`Latitude` as 'lat', `patient`.`Longitude` as 'lon' , `driver`.`Latitude` , `driver`.`Longitude` FROM `patient`, `driver` 
	          WHERE `patient`.`ID` = '$pid' AND `driver`.`ID` = '$did'";
			  
		$json = @getElements($sql,new json()); 
	  
	   $json->datatype = "track";
	  
  return  $json;
}


track($_GET['pid'],$_GET['did'])->send();
?>