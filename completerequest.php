
<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

function getnotification($id ){
	
	
	
     $link = @Conection(new json());

     $sql  = "SELECT DISTINCT `notification`.`ID` as 'NOTID' , `notification`.`FromID` as 'PID',`notification`.`ToID` as 'TOID', `notification`.`Type` as 'NTYPE' , `notification`.`Status` as 'NSTATUS', `notification`.`Date`,
	  `patient`.`Latitude` , `patient`.`Longitude`,
	 (SELECT `patient`.`Name` FROM `patient` WHERE `patient`.`ID` = `notification`.`FromID`) as 'PName', 
	 (SELECT `patient`.`Name` FROM `patient` WHERE `notification`.`Message` = `patient`.`ID` ) as 'RNAME' ,
	 (SELECT `patient`.`Name` FROM `patient` WHERE `patient`.`ID` = `notification`.`ToID`) as 'TNAME' ,
	 (SELECT `driver`.`Name` FROM `driver` WHERE `driver`.`ID` = `notification`.`ToID`) as 'DNAME' 
	 FROM `notification` , `patient` WHERE (`notification`.`ToID` = '$id' or `notification`.`ToID` = '-1' ) and `patient`.`ID` = `notification`.`FromID`" ;


     $dt =  @getElements($sql,new json());

	$dt->datatype = "notification";
   return $dt;
}


function complete($Id , $to){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";

		
	$sql = "UPDATE `notification` SET `Status` = 'Done' WHERE `notification`.`ID` =  '$Id'"; 

    $result = mysqli_query($link , $sql );  
	if($result){
	   	
     return  getnotification($to);
				
	}else
		$json->error = "Can not update driver request";  
		
   	   
	

  $json->datatype = "completerequest";
  return $json;
}

complete( $_GET['id'] , $_GET['to'])->send();

?>

