<?php
    require_once "./config/config.inc.php";
    require_once "./lib/http_code.lib.php";
    require_once "./lib/util.lib.php";
    require_once "./lib/xml.lib.php";
    require_once "./lib/dbhelper.lib.php";
    require_once "BaeLog.class.php";
    
    $logger = BaeLog::getInstance();
        
    $params = $_GET;
    if (Util::check_get_input($params) == false) {
        $logger->logFatal("Input error : ".serialize($params));
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $table = 'user_account_info';
    $where_fields['user_id'] = $params['userid'];
    $where_fields['imei'] = $params['imei']; 
    
    $db_helper = new DBHelper();
    $result = $db_helper->get_data_in_user_account_info($table, $where_fields); 
    
    if ($result === false) {
        $logger->logFatal("get_data_in_user_account_info failed.");
        http_response($HTTP_CODE['QUERY FAILED']);
    }
    if ($result === null) {
        $logger->logFatal("get_data_in_user_account_info return empty.");
        http_response($HTTP_CODE['INPUT ERROR']);
    }   
    
    $xml_data['account'] = $result[0];
    $xml_writer = new xml();
    $xml_writer->make_xml($xml_data);  
    
    http_response($HTTP_CODE['SUCCESS']);
?>