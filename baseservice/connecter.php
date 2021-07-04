<?PHP

	FUNCTION CONECTER($JSON,$HOST,$USER,$PASS,$DB){
		
		echo $HOST;

		IF (!($LINK=MYSQLI_CONNECT("$HOST","$USER","$PASS")))  {
		  $JSON->ERROR = "CONNECT:FAILED TO CONNECT TO SERVER";
		  $JSON->SEND();
		  EXIT();
		}	
		 
		 IF(!(MYSQLI_SELECT_DB($LINK,"$DB") ))
		{
		 
		 $JSON->ERROR = "FAIL TO CONNECT TO DATABASE";
		 $JSON->SEND();
		 DIE("");
		}
						   
   RETURN $LINK;
}

?> 