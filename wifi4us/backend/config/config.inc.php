<?php 
    /*配置数据库名（可从管理中心查看到）*/
    define('DB_NAME','yYtFsorayeqrjIKUmGIQ');
    
	/*从环境变量里取出数据库连接需要的参数并配置*/
	$host = getenv('HTTP_BAE_ENV_ADDR_SQL_IP');
	$port = getenv('HTTP_BAE_ENV_ADDR_SQL_PORT');
	$user = getenv('HTTP_BAE_ENV_AK');
	$pwd = getenv('HTTP_BAE_ENV_SK');

	define('DB_HOST', $host . ':' . $port);
    define('DB_USERNAME', $user);
    define('DB_PASSWORD', $pwd);
/*
    define('DB_HOST', "localhost");
    define('DB_USERNAME', "root");
    define('DB_PASSWORD', "123456");
*/    
    
    /*配置函数库路径*/
    define('ROOT_PATH', '.');
    define('ROOT_LIBPATH', ROOT_PATH.'/lib');
    
    /*配置PHP时区*/
    date_default_timezone_set('Asia/Shanghai');

	/*设置超时间隔*/
	set_time_limit(5);
?>