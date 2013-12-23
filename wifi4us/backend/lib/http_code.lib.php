<?php
    
    $HTTP_CODE = array(
        'SUCCESS'        => 200,
        'TIMEOUT'        => 601,
        'INPUT ERROR'    => 602,
        'SERVER ERROR'   => 603,
        'CONNECT FAILED' => 604,
        'QUERY FAILED'   => 605
    );
    
    function http_response($code = NULL) {
        
        if ($code !== NULL) {
            switch ($code) {
                case 200: $text = 'Success'; break;
                case 601: $text = 'Timeout'; break;
              	case 602: $text = 'Input error'; break;
                case 603: $text = 'Server error'; break;
                case 604: $text = 'Connect failed'; break;
                case 605: $text = 'Query failed'; break;
                default:
                    exit('Unknown http status code "' . htmlentities($code) . '"');
                break;
            }
            $protocol = (isset($_SERVER['SERVER_PROTOCOL']) ? $_SERVER['SERVER_PROTOCOL'] : 'HTTP/1.0');
            
            header($protocol . ' ' . $code . ' ' . $text);
            
            if ($code !== 200) {
                exit(0);
            }
        } else {
            $protocol = (isset($_SERVER['SERVER_PROTOCOL']) ? $_SERVER['SERVER_PROTOCOL'] : 'HTTP/1.0');
            
            header($protocol . ' 200 success');

        }
    }
    
    function return_callback($buffer){
        global $HTTP_CODE;
        if (strpos($buffer, 'Maximum execution') !== false) {
            http_response($HTTP_CODE['TIMEOUT']);
        }
        return $buffer;
    }
    ob_start("return_callback");
?>