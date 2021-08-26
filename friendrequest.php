<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function request($from, $email){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  $link = @Conection(new json());
  

 	  $sql = "SELECT `ID` FROM `patient` WHERE `Email` = '$email'";
	  
	  $dt =  @getElements($sql,new json()); 
	  if($dt->result == "fail"){
		$json->error = "Cann't find a user with this email '$email'";
		return $json;
	  }else
   
	   {
		  $to = $dt->data[0]['ID'];
		  
		  $type = "Friend request";
		  $sql = "SELECT * FROM `notification` WHERE `Type` = '$type' AND `FromID` = '$from' AND `ToID` = '$to'  ";
		  
		  $dt =  @getElements($sql,new json()); 
		  
		  if($dt->result == "done"){
			$json->error = "Request already exist";
		  }
		  else{
	  
			$sql = "INSERT INTO `notification` (`ID`, `Type`, `FromID`, `ToID`, `Status`, `Message`, `Date`) VALUES (NULL, '$type', '$from', '$to', '0', '$from', CURRENT_TIMESTAMP);";

			$result = mysqli_query($link , $sql );  
			if($result){
					 $json->result= "done"; 
						
			}else
				$json->error = "failled to request";  
		  }
	   }

  $json->datatype = "friendrequest";
  return $json;
}


request($_GET['fm'],$_GET['em'])->send();
?>