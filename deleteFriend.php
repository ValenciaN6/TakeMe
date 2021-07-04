
<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");

//`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude`, `Password`
function deleteFriend($id){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
 
  $sql = "DELETE FROM `friend` WHERE `friend`.`ID` = '$id'"; 

  $result = mysqli_query($link , $sql );  
	if($result){
		$json->result= "done"; 
				
	}else
		$json->error = "Can not delete Request ";  
  	
 
  
  
  
  
	

  $json->datatype = "deleteFriend";
  return $json;
}
//em=gogo@gmail.com&nm=friz&$sn,$pn,$ut,$la,$lo,$pw
deleteFriend($_GET['id'])->send();

?>

