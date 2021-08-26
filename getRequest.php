<?php

//
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function getRequest($id ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT `notification`.`ID`, `notification`.`Status` , `patient`.`Name` as 'NumberPlate' , `patient`.`Email` as 'Description' , `patient`.`Latitude`, `patient`.`Longitude`
			FROM 
			`patient` , `notification` 
			WHERE  
			`notification`.`Type` = 'Ambulance request' AND (`notification`.`ToID` = '$id' OR `notification`.`ToID` = '-1') AND `patient`.`ID` = `notification`.`FromID`";

     $dt =  @getElements($sql,new json());

	$dt->datatype = "ambulance";
   return $dt;
}

getRequest($_GET['id'])->send();

?>