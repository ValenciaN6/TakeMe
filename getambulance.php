<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function getambulance( ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT `driver`.`Name` , `driver`.`Latitude` , `driver`.`Longitude` , `ambulance`.`NumberPlate` , `ambulance`.`Description` FROM `driver` , `ambulance` WHERE `driver`.`ID` = `ambulance`.`DriverID`";

     $dt =  @getElements($sql,new json());

	$dt->datatype = "ambulance";
   return $dt;
}

getambulance()->send();
?>