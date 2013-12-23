<?php

    require_once "./config/config.inc.php";
	require_once "./lib/xml.lib.php";
    require_once "./lib/http_code.lib.php";
	//baexhprof_start(XHPROF_FLAGS_NO_BUILTINS);
    $aarray['a']['b'] = 'hi';
    $aarray['a']['c'] = 'ni hao';
//    $xml_writer = new xml();
//$xml_writer->make_xml($aarray);  
    foreach($aarray['a'] as $key => $value) {
        echo "key = ".$key;
    }
    
    /*
    require_once "BaeLog.class.php";
    $logger = BaeLog::getInstance();
    $logger->logDebug("this is for debug log print ");
    $logger->logFatal("this is for fatal log print ");
    */
    //set_time_limit(5);
    //sleep(6);
	baexhprof_end();
	echo "Welcome to Baidu Cloud";
/*
require_once dirname(__FILE__).'/BaeLog.class.php';

$secret = array("user"=>"Hs7CbIwRnyAEVOL710LNNKUq","passwd"=>"eC4vPVFyiPl1h0HjqG2ejGvRNt2IyqDx" );
$log = BaeLog::getInstance($secret);
if(NULL !=  $log)
{
   $log->setLogLevel(16);
   for($i=0;$i<3;$i++)
   {
       $ret = $log->Fatal("lelllllllllllllll");
        if(false === $ret)
        {
            $code = $log->getResultCode();
            echo "$code<br/>";
        }else{
            echo "Success<br/>";
        }
   }
}
*/
?>