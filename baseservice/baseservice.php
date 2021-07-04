<?php

function getElements($sql,$json){

		$link = @Conection($json);

		$result = mysqli_query($link , $sql );  
		
		$json->result="fail";
		if(!$result)
		$json->msg = mysqli_error($link);
	
		while ($row = mysqli_fetch_array($result))
		{
			$json->data[] = $row;
			$json->result="done";
		 
		}
		mysqli_close($link);
		return $json;
	}
	
function baseConection($host,$user,$pass,$db){
	
		
	if (!($link=mysqli_connect($host,$user,$pass,)))  {
		$json->error = "connect:failed to connect to server";
		$json->send();
		exit();
	}
		 
	if(!(mysqli_select_db($link,$db) ))
	{
	 
		$json->error = "fail to connect to database";
		$json->send();
		die("");
	}
							   
   return $link;
}

function canFind($sql,$json){
   return (getElements($sql,$json)->result == "done");
}
?>
