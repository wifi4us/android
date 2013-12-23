<?php
    require_once "./config/config.inc.php";
    require_once "./lib/http_code.lib.php";
    require_once "./lib/util.lib.php";
    require_once "./lib/xml.lib.php";
    require_once "./lib/dbhelper.lib.php";
    require_once "BaeLog.class.php";
    
    $logger = BaeLog::getInstance();
    
    $params = $_GET;
    if (Util::check_add_input($params) == false) {
        //$logger->logFatal("Input error : userid[".$userid."], imei[".$imei."], traffic[".$traffic."], duration[".$duration."].");
        $logger->logFatal("Input error : ".serialize($params));
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $account_added = Util::compute_account_added($params['traffic'], $params['duration']);
    
    $table = 'user_account_info';
    $add_data['account'] = $account_added;
    $add_data['total_account'] = $account_added;
    $add_data['total_traffic'] = $params['traffic'];
    $add_data['total_time'] = $params['duration'];
    $set_data['update_time'] = time();
    
    $where_fields['user_id'] = $params['userid'];
    $where_fields['imei'] = $params['imei']; 
    
    $db_helper = new DBHelper();
    $result = $db_helper->add_data_in_user_account_info($table, $where_fields, $add_data, $set_data);

    if ($result === false) {
        $logger->logFatal("add_data_in_user_account_info failed.");
        http_response($HTTP_CODE['QUERY FAILED']);
    } else if ($result === null) {
        $logger->logFatal("add_data_in_user_account_info return empty.");
        http_response($HTTP_CODE['INPUT ERROR']);
    }
        
    $xml_data['account'] = $result[0]['account'];
    $xml_writer = new xml();
    $xml_writer->make_xml($xml_data);  
        
    http_response($HTTP_CODE['SUCCESS']);
?>