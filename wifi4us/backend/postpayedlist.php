<?php
    require_once "./config/config.inc.php";
    require_once "./lib/http_code.lib.php";
    require_once "./lib/util.lib.php";
    require_once "./lib/dbhelper.lib.php";
    require_once "BaeLog.class.php";
    
    $logger = BaeLog::getInstance();
        
    $params = $_GET;
    $qq_list = $_POST['qqlist'];
    if (Util::check_postpayedlist_input($params, $qq_list) == false) {
        $logger->logFatal("Input error : time[".$params['time']."], qq_list[".$qq_list."].");        
        http_response($HTTP_CODE['INPUT ERROR']);
    }
    
    $table = 'qb_pay_list';
    $set_data['paid'] = 1;
    $set_data['update_time'] = time();
    $in_field = 'qq';
    $in_values = explode(',', $qq_list);
    
    $half_hour = 1800;
    $end_time = $params['time'] + $half_hour;
    $between_params['field'] = 'update_time';
    $between_params['start'] = $params['time'];
    $between_params['end'] = $end_time;
    
    $db_helper = new DBHelper();
    $result = $db_helper->update_unpaid_list_in_qb_pay_list($table, $set_data, $in_field, $in_values, $between_params);    
    
    if ($result === false) {
        $logger->logFatal("update_unpaid_list_in_qb_pay_list failed.");
        http_response($HTTP_CODE['QUERY FAILED']);
    }
    
    http_response($HTTP_CODE['SUCCESS']);    
?>