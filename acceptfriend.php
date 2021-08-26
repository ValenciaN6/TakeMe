
<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

//`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude`, `Password`
function accept($id){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  
  
  $sql = "SELECT `FromID` , `ToID` FROM `notification` WHERE `notification`.`ID` = '$id'";
  $dt =  @getElements($sql,new json());
   if($dt->result == "done"){
		
	$sql = "UPDATE `notification` SET `Status` = '1' WHERE `notification`.`ID` = '$id'"; 

    $result = mysqli_query($link , $sql );  
	if($result){
	   	
	 $sql =	"SELECT `FromID`,`ToID` FROM `notification` WHERE `ID` = '$id'";
	 $notification =  @getElements($sql,new json());
	 
	 $from = $notification->data[0]['FromID'];
	 $to   = $notification->data[0]['ToID'];
	 
	  $sql =  "INSERT INTO `friend` (`ID`, `IDA`, `IDB`, `Date`, `Status`) VALUES (NULL, '$from', '$to', CURRENT_TIMESTAMP, '2');";
	  $resul = mysqli_query($link , $sql );  
	  
 
	  if($resul){
		 $json->result = "done";
	  }else{
		  $json->error = "Can not add a friend "; 
		  $sql = "UPDATE `notification` SET `Status` = 'Waiting' WHERE `notification`.`ID` = '$id'"; 

          $result = mysqli_query($link , $sql );
		  
	  }
	  
				
	}else
		$json->error = "Can not delete Request ";  
		
   }else{
      $json->error = "Can not find Request ";
   }	   
 
 
  	
 
  
  
  
  
	

  $json->datatype = "acceptfriend";
  return $json;
}
//em=gogo@gmail.com&nm=friz&$sn,$pn,$ut,$la,$lo,$pw
accept($_GET['id'])->send();

?>

