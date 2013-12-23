<?php
    require_once "BaeLog.class.php";
    class Util{
        private static $logger;
        
        public static function initialize() {
            self::$logger = BaeLog::getInstance();
        } 
        
        public static function check_imei($imei) {
            if (preg_match("/\d{15,15}/", $imei)) {
                return true;
            } else{
                return false;
            }
        }
        
        public static function check_integer($num) {
            if (preg_match("/\d+/", $num)) {
                return true;
            } else{
                return false;
            }
        }
        
        public static function check_double($num) {
            if (preg_match("/^(-?\d+)(\.\d+)?$/", $num)) {
                return true;
            } else{
                return false;
            }
        }
        
        public static function check_register_input($params) {
            //self::$logger->logDebug("Check register.php input.");
            
            if (!isset($params['imei']) || !Util::check_imei($params['imei'])) {
                return false;
            }
            
            return true;
        }
        
        public static function check_get_input($params) {
            //self::$logger->logDebug("Check get.php input.");
            
            if (!isset($params['userid']) || !isset($params['imei'])) {
                return false;
            }
            if (!Util::check_imei($params['imei']) || !Util::check_integer($params['userid'])) {
                return false;
            }

            return true;
        }
        
        public static function check_add_input($params) {
            //self::$logger->logDebug("Check add.php input.");
            
            if (!isset($params['userid']) || !isset($params['imei']) || !isset($params['traffic']) || !isset($params['duration'])) {
                return false;
            }
            if (Util::check_imei($params['imei']) == false) {
                return false;
            }
            if (!Util::check_integer($params['userid']) || !Util::check_double($params['traffic']) || !Util::check_double($params['duration'])) {
                return false;
            }
            return true;
        }
        
        public static function compute_account_added($traffic, $duration) {
            //self::$logger->logDebug("Compute account added.");
            
            return 1.1;
        }
        
        public static function check_reduce_input($params) {
            //self::$logger->logDebug("Check reduce.php input.");
            
            if (!isset($params['userid']) || !isset($params['imei']) || !isset($params['type']) || !isset($params['qq']) || !isset($params['qb'])) {
                return false;
            }
            if (Util::check_imei($params['imei']) == false) {
                return false;
            }
            if (!Util::check_integer($params['userid']) || !Util::check_integer($params['qq']) || !Util::check_double($params['qb'])) {
                return false;
            }
            return true;            
        }
        
        public static function compute_account_reduced($type, $qq, $qb) {
            //self::$logger->logDebug("Compute account reduced."); 
            
            return -1.1;
        }
        
        public static function check_getadid_input($params) {
            //self::$logger->logDebug("Check getadid.php input.");
            
            if (!isset($params['userid']) || !Util::check_integer($params['userid'])) {
                return false;
            }
            
            return true;            
        }
        
        public static function compute_ad_id($params) {
            //self::$logger->logDebug("Compute ad id."); 
            $ad_id = 1;
            
            return $ad_id;
        }
        
        public static function check_getpaylist_input($params) {
            //self::$logger->logDebug("Check getpaylist.php input.");
            
            if (!isset($params['time']) || !Util::check_integer($params['time'])) {
                return false;
            }
            
            return true;
        }
        
        public static function check_postpayedlist_input($params, $qq_list) {
            //self::$logger->logDebug("Check postpayedlist.php input.");
            
            if (!isset($params['time']) || !Util::check_integer($params['time'])) {
                return false;
            }
            
            return true;
        }
    };
    
    Util::initialize();
?>