<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function login($Email ,$pass){
	
     $link = @Conection(new json());
     $sql  = "SELECT * FROM `user` WHERE `Email` = '$Email' and `Password` = '$pass'";
	 
     $dt =  @getElements($sql,new json());

	$dt->datatype = "login";
   return $dt;
}

login($_GET['em'],$_GET['ps'])->send();
?>