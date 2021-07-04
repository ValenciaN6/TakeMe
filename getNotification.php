<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function getnotification($id ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT DISTINCT `notification`.`ID` as 'NOTID' , `notification`.`FromID` as 'PID', `notification`.`Type` as 'NTYPE' , `notification`.`Status` as 'NSTATUS', `notification`.`Date`, (SELECT `patient`.`Name` FROM `patient` WHERE `patient`.`ID` = `notification`.`FromID`) as 'PName', (SELECT `patient`.`Name` FROM `patient` WHERE `notification`.`Message` = `patient`.`ID` ) as 'RNAME' , (SELECT `driver`.`Name` FROM `driver` WHERE `driver`.`ID` = `notification`.`ToID`) as 'DNAME' FROM `notification` WHERE `notification`.`FromID` = '$id'";

     $dt =  @getElements($sql,new json());

	$dt->datatype = "notification";
   return $dt;
}

getnotification($_GET['id'])->send();
?>