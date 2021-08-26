
<?php
include('baseservice/json.php');
use \Simple\json;

include('baseservice/baseservice.php');	
include("connecter.php");


function updatelocation($lg,$lt,$id,$tp){
	
  if($tp == "1")
   $tp = "patient";	
  else
   $tp = "driver";	  

  $link=@Conection(new json());
  $sql = "UPDATE `$tp` SET Latitude = '$lt' , Longitude = '$lg' WHERE id = '$id'";
 $result = mysqli_query($link , $sql );  
 
 if($result)
 {
	 $sql = "SELECT * FROM `$tp` WHERE `ID` = '$id' ";
	 $dt =  @getElements($sql,new json());
	 if($dt->result == "done"){
		 $dt->datatype = "updatelocation";
		 $dt->send();
		 return $dt;
	 }
 }
 else 
  echo "fail";	 

return;
}

updatelocation($_GET['lon'],$_GET['lat'],$_GET['id'],$_GET['tp']);

?>

