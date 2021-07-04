<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

function getbalance($id ){
	
	
	
     $link = @Conection(new json());
     $sql  = "SELECT SUM(amount) as 'balance' FROM `transaction` WHERE toID = '$id' AND status = 'approved'";

     $dt =  @getElements($sql,new json());

	$dt->datatype = "balance";
   return $dt;
}

getbalance($_GET['id'])->send();
?>