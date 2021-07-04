
<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

//`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude`, `Password`
function deleteRequest($id){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
 
  $sql = "DELETE FROM `notification` WHERE `notification`.`ID` = '$id'"; 

  $result = mysqli_query($link , $sql );  
	if($result){
		$json->result= "done"; 
				
	}else
		$json->error = "Can not delete Request ";  
  	
 
  
  
  
  
	

  $json->datatype = "deleteRequest";
  return $json;
}
//em=gogo@gmail.com&nm=friz&$sn,$pn,$ut,$la,$lo,$pw
deleteRequest($_GET['id'])->send();

?>

