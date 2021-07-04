<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function login($Email ,$pass, $typy){
	
     $link = @Conection(new json());
	 if($typy == "com")
	 $sql  = "SELECT * FROM `company` WHERE `UserName` = '$Email' and `Password` = '$pass'";
     else
     $sql  = "SELECT * FROM `$typy` WHERE `Email` = '$Email' and `Password` = '$pass'";
	 
     $dt =  @getElements($sql,new json());

	$dt->datatype = "login";
   return $dt;
}

login($_GET['em'],$_GET['ps'],$_GET['tp'])->send();
?>