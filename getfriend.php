<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function getfriend($id ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT `patient`.`Email` , `patient`.`ID` FROM `friend` , `patient` WHERE `friend`.`IDA` = '$id' AND `patient`.`ID` = `friend`.`IDB` and `friend`.`Status` = '2' ";

     $dt =  @getElements($sql,new json());

	$dt->datatype = "friend";
   return $dt;
}

getfriend($_GET['id'])->send();
?>