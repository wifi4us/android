<?php
    require_once ROOT_LIBPATH."/mysql.lib.php";
    require_once "BaeLog.class.php";
    
    class DBHelper{
        private $db;
        private $logger;
        
        public function __construct() {
            $this->logger = BaeLog::getInstance();
            
            $this->db = new DB();
            $ret = $this->db->connect();
            if ($ret === false) {
                $this->logger->logFatal("Connecting to database failed.");
                http_response($HTTP_CODE['CONNECT FAILED']);
            }
        }
        
        public function add_data_in_user_account_info($table, $where_fields, $add_data, $set_data) {
            $update_querys[] = $this->db->get_update_add_sql($table, $where_fields, $add_data, $set_data);
            $select_query = $this->db->get_select_sql($table, $where_fields);
            
            $result = $this->db->query_with_transaction($update_querys, $select_query);
            if ($result === false) {
                $this->logger->logFatal("Transaction query failed : ".serialize($update_querys)."; select : ".$select_query);
            }
            if ($result === null) {
                $this->logger->logFatal("Transaction return empty : ".serialize($where_fields));
            }
           
            return $result;
        }
        
        public function insert_data_in_user_account_info($table, $insert_data) {
            $insert_query = $this->db->get_insert_sql($table, $insert_data);  
            
            $result = $this->db->execute($insert_query);
            if ($result === false) {
                $this->logger->logFatal("Query failed : ".$insert_query);
            }            
            
            return $result;
        }
        
        public function get_last_insert_autoincrease_id() {
            $id = $this->db->query_last_insert_id();
            if ($id === false) {
                $this->logger->logFatal("Query failed : select last insert id()");
            }
            
            return $id;
        }
        
        public function reduce_data_in_user_account_info($tables, $where_fields, $add_datas, $set_datas, $insert_datas) {
            $insert_update_querys[] = $this->db->get_update_add_sql($tables['user'], $where_fields['user'], $add_datas['user'], $set_datas['user']);
            $select_query = $this->db->get_select_sql($tables['user'], $where_fields['user']);
            $insert_update_querys[] = $this->db->get_insert_sql($tables['qb'], $insert_datas['qb']);            
            $candidate_update_query = $this->db->get_update_add_sql($tables['qb'], $where_fields['qb'], $add_datas['qb'], $set_datas['qb']);

            $result = $this->db->query_with_transaction($insert_update_querys, $select_query, $candidate_update_query);
            
            if ($result === false) {
                $this->logger->logFatal("Transaction query failed : ".serialize($insert_update_querys)."; candidate : ".$candidate_update_query."; select : ".$select_query);
            }
            if ($result === null) {
                $this->logger->logFatal("Transaction return empty : ".serialize($where_fields['user']));
            }
            
            return $result;
        }
        
        public function get_ad_id_in_ad_list($table, $where_fields) {
            $select_query = $this->db->get_select_sql($table, $where_fields);
            
            $result = $this->db->query_row($select_query);
            if ($result === false) {
                $this->logger->logFatal("Query failed : ".$select_query);
            }
            if ($result === null) {
                $this->logger->logFatal("Query return empty : ".serialize($where_fields));
            }
            
            return $result;
        }
        
        public function get_data_in_user_account_info($table, $where_fields) {
            $select_query = $this->db->get_select_sql($table, $where_fields);
            
            $result = $this->db->query_one_field($select_query, 'account');     
            if ($result === false) {
                $this->logger->logFatal("Query failed : ".$select_query);
            }
            if ($result === null) {
                $this->logger->logFatal("Query return empty : ".serialize($where_fields));
            }      
            
            return $result;
        }
        
        public function get_unpaid_list_in_qb_pay_list($table, $between_params, $where_fields) {
            $select_query = $this->db->get_select_between_sql($table, $between_params, $where_fields);
            
            $result = $this->db->query($select_query);        
            if ($result === false) {
                $this->logger->logFatal("Query failed : ".$select_query);
            }
            
            return $result;    
        }
        
        public function update_unpaid_list_in_qb_pay_list($table, $set_data, $in_field, $in_values, $between_params) {
            $update_query = $this->db->get_update_in_between_sql($table, $set_data, $in_field, $in_values, $between_params);
            
            $result = $this->db->execute($update_query);     
            if ($result === false) {
                $logger->logFatal("Query failed : ".$update_query);
            }       
            
            return $result;
        }

    };
?>