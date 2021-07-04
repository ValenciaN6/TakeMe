
<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");




function request($from,$to,$type){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  
  
 
   
      $link = @Conection(new json());
	  $sql = "SELECT * FROM `notification` WHERE `Type` = '$type' AND `FromID` = '$from' AND `Status` = 'waiting'";
	  
	  $dt =  @getElements($sql,new json()); 
	  if($dt->result == "done"){
		$json->error = "Request already exist";
	  }
	  else{
  
		$sql = "INSERT INTO `notification` (`ID`, `Type`, `FromID`, `ToID`, `Status`, `Message`, `Date`) VALUES (NULL, '$type', '$from', '-1', 'Waiting', '$to', CURRENT_TIMESTAMP);";

		$result = mysqli_query($link , $sql );  
		if($result){
				 $json->result= "done"; 
					
		}else
			$json->error = "failled to request";  
	  }

  $json->datatype = "request";
  return $json;
}

request($_GET['fm'],$_GET['to'],$_GET['tp'])->send();

?>

