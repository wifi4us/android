<?php
    require_once "./config/config.inc.php";
        
    dbConnect();
    $data['user_id'] = 2;
    $data['account'] = 8888888;
    $data['create_time'] = time();
    $sql = dbInsertQuery('user_account_info', $data);
    dbExecute($sql);
    
    $result = dbQuery("select * from user_account_info;");
	print_r($result);
    
    dbDisconnect();
?>