<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function getallusers($id ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT `patient`.`Name`, `patient`.`Surname`, `patient`.`Email` ,`patient`.`ID` FROM `patient` WHERE `patient`.`ID` != '$id'";

     $dt =  @getElements($sql,new json());
//SELECT `patient`.`Name`, `patient`.`Surname`, `patient`.`Email` , `friend`.`ID` FROM `friend` , `patient` WHERE (`friend`.`IDA` = '$id' OR `friend`.`IDB` = '$id') AND ( IF( `friend`.`IDA` = '$id' , `patient`.`ID` = `friend`.`IDB` , `patient`.`ID` = `friend`.`IDA` )) and `friend`.`Status` = '2'
	$dt->datatype = "allusers";
   return $dt;
}

getallusers($_GET['id'])->send();
?>