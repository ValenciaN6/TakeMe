<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function login($Email ,$pass){
	
     $link = @Conection(new json());
	 
	    $sql = "INSERT INTO `driver` (`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude`, `Password`) VALUES (-1, '-1', '', '', '', '0', '-1', '-1', '')";
   $result = mysqli_query($link , $sql ); 
	 
     $sql  = "SELECT * FROM `patient` WHERE `Email` = '$Email' and `Password` = '$pass'";
 
     $dt =  @getElements($sql,new json());
	 if($dt->result == "done"){
		 $dt->datatype = "login";
		 return $dt;
	 }
	 
	 
     $sql  = "SELECT * FROM `company` WHERE `UserName` = '$Email' and `Password` = '$pass'";
     $dt =  @getElements($sql,new json());
	 if($dt->result == "done"){
		 $dt->datatype = "login";
		 return $dt;
	 }
	 
	 
	 
	 $sql  = "SELECT * FROM `driver` WHERE `Email` = '$Email' and `Password` = '$pass'";
	 
	 $dt =  @getElements($sql,new json());
	 if($dt->result == "done"){
		 $dt->datatype = "login";
		
	 }
   return $dt;
}

login($_GET['em'],$_GET['ps'])->send();
?>