<?php
    require_once "./config/config.inc.php";
    require_once "./lib/http_code.lib.php";
    require_once "./lib/util.lib.php";
    require_once "./lib/xml.lib.php";
    require_once "./lib/dbhelper.lib.php";
    require_once "BaeLog.class.php";
    
    $logger = BaeLog::getInstance();
             
    $params = $_GET;
    if (Util::check_getpaylist_input($params) == false) {
        $logger->logFatal("Input error : ".serialize($params));
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $table = 'qb_pay_list';  
    $half_hour = 1800;
    $end_time = $params['time'] + $half_hour;
    $between_params['field'] = 'update_time';
    $between_params['start'] = $params['time'];
    $between_params['end'] = $end_time;
    $where_fields['paid'] = false;
    
    $db_helper = new DBHelper();
    $result = $db_helper->get_unpaid_list_in_qb_pay_list($table, $between_params, $where_fields); 
    
    if ($result === false) {
        $logger->logFatal("get_unpaid_list_in_qb_pay_list failed.");
        http_response($HTTP_CODE['QUERY FAILED']);
    }
    
    $xml_data = array();
    foreach ($result as $entry) {
        $xml_data[] = array('qq' => $entry['qq'], 'qb' => $entry['qb']);
    }
    $xml_writer = new xml();
    $xml_writer->make_xml($xml_data);  
    
    http_response($HTTP_CODE['SUCCESS']);
?>