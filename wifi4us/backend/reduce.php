<?php
    require_once "./config/config.inc.php";
    require_once "./lib/http_code.lib.php";
    require_once "./lib/util.lib.php";
    require_once "./lib/xml.lib.php";
    require_once "./lib/dbhelper.lib.php";
    require_once "BaeLog.class.php";
    
    $logger = BaeLog::getInstance();

    $params = $_GET;
    if (Util::check_reduce_input($params) == false) {
        $logger->logFatal("Input error : ".serialize($params));
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $account_reduced = Util::compute_account_reduced($type, $qq, $qb);
    
    $tables['user'] = 'user_account_info';
    $add_datas['user']['account'] = $account_reduced;
    $set_datas['user']['update_time'] = time();
    $where_fields['user']['user_id'] = $params['userid'];
    $where_fields['user']['imei'] = $params['imei']; 
    
    
    $tables['qb'] = 'qb_pay_list';
    $insert_datas['qb']['qq'] = $params['qq'];
    $insert_datas['qb']['user_id'] = $params['userid'];
    $insert_datas['qb']['imei'] = $params['imei'];
    $insert_datas['qb']['qb'] = $params['qb'];
    $insert_datas['qb']['paid'] = false;
    $insert_datas['qb']['create_time'] = time();
    $insert_datas['qb']['update_time'] = $insert_data['qb']['create_time'];
    $where_fields['qb']['qq'] = $params['qq'];
    $add_datas['qb']['qb'] = $params['qb'];
    $set_datas['qb']['update_time'] = time();
    
    $db_helper = new DBHelper();
    $result = $db_helper->reduce_data_in_user_account_info($tables, $where_fields, $add_datas, $set_datas, $insert_datas);
    
    
    if ($result === false) {
        $logger->logFatal("reduce_data_in_user_account_info failed.");
        http_response($HTTP_CODE['QUERY FAILED']);
    } else if ($result === null) {
        $logger->logFatal("reduce_data_in_user_account_info return empty.");
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $xml_data['account'] = $result[0]['account'];
    $xml_writer = new xml();
    $xml_writer->make_xml($xml_data);  
        
    http_response($HTTP_CODE['SUCCESS']);
?>