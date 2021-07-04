
<?php
include('../baseservice/json.php');
use \Simple\json;

include('../baseservice/baseservice.php');	
include("connecter.php");


/*INSERT INTO `transaction` (`id`, `fromID`, `toID`, `date`, `amount`, `pinCode`, `status`) VALUES (NULL, '1', '2', CURRENT_TIMESTAMP, '11', 'qsder342wassdxdsar', ''), ('2', '1', '3', CURRENT_TIMESTAMP, '.10', 'qsder342wassdxdsar', '');
*/

function addTransaction($from,$to,$amount,$pinCode,$us ,$profit){
  $json = new json();
  $link=@Conection($json);
  $json->result= "fail";
  
	$sql = "INSERT INTO `transaction` (`id`, `fromID`, `toID`, `date`, `amount`, `pinCode`, `status`) VALUES (NULL, '$from', '$to', CURRENT_TIMESTAMP, '$amount', '$pinCode', ''), (NULL, '$from', '$us', CURRENT_TIMESTAMP, '$profit', '$pinCode', '');";

	$result = mysqli_query($link , $sql );  
	if($result){
			 $json->result= "done"; 
				
	}else
		$json->error = "Can not add Transaction $pinCode";  

  $json->datatype = "addtra";
  return $json;
}

addTransaction($_GET['from'],$_GET['to'],$_GET['amount'],$_GET['pin'],$_GET['us'],$_GET['profit'])->send();

?>

