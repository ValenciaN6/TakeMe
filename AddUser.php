
<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");

//`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude`, `Password`
function AddUser($em,$nm,$sn,$pn,$ut,$la,$lo,$pw , $cr , $dc, $nnm , $nc){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  
  $table = "patient";
  
  if($ut == "2")
	$table = "driver";
	  
  
  $link = @Conection(new json());
  $sql  = "SELECT * FROM `$table` WHERE `Email` = '$em'";
  
  
  
  $dt =  @getElements($sql,new json()); 
  if($dt->result == "done"){
	$json->error = "Email exist";
  }
  else{
	  
	$sql = "INSERT INTO `$table` (`ID`, `Email`, `Name`, `Surname`, `PhoneNumber`, `UserType`, `Latitude`, `Longitude` , `Password` , `NumberPlate` , `Description` , `NextOfKinName` , `NextOfKinCell`) 
	VALUES (NULL, '$em', '$nm', '$sn', '$pn', '$ut', '$la', '$lo', '$pw' , '$cr' ,'$dc' , '$nnm' , '$nc');";

	$result = mysqli_query($link , $sql );  
	if($result){
		$json->result= "done"; 
				
	}else
		$json->error = "Can not add user` $em";  
  	
 }
  
  
  
  
	

  $json->datatype = "AddUser";
  return $json;
}
//em=gogo@gmail.com&nm=friz&$sn,$pn,$ut,$la,$lo,$pw
AddUser($_GET['em'],$_GET['nm'],$_GET['sn'],$_GET['pn'],$_GET['ut'],$_GET['la'],$_GET['lo'],$_GET['pw'],$_GET['cr'] ,$_GET['dc'],$_GET['nnm'] ,$_GET['nc'] )->send();

?>

