/**
 * @(#)Constants.java, 2014-1-6. 
 * 
 * Copyright 2014 Yodao, Inc. All rights reserved.
 * YODAO PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.paad.wifi4us.utility;

/**
 *
 * @author yangshi
 *
 */
public class Constant {
	
	public static class HttpParas{
		public static String IMEI = "imei";
		public static String PHONE = "phone";
		public static String USER_ID="userid";
		public static String ID_NUM = "id_num";
		public static String NAME = "name";
		public static String ALIPAY_ID="alipay_id";
		public static String TYPE = "type";
		public static String PROGRAM = "program";
		public static String NOTES = "notes";
		public static String MAKE = "make";
		public static String MODEL = "model";
		public static String RESOLUTION = "resolution";
		public static String CARRIER = "carrier";
		public static String DURATION = "duration";
		public static String TRAFFIC = "traffic";
		public static String ANDROIDVER = "androidversion";

	}
	
	public static class XmlResultKey{
		public static String TRADE_ID="trade_id";
		public static String PROGRAM="program";
		public static String PERIOD_NUMBER="period_number";
		public static String STATE = "state";
		public static String TICKET_ID="ticket_id";
		public static String BONUS = "bonus";
		public static String CREDIT = "credit";
		public static String USER_ID = "userid";
		public static String ACCOUNT = "account";
		public static String AD_ID = "ad_id";
		public static String AD_URL = "url";
		public static String AD_LENGTH = "length";
		public static String AD_WORD = "ad_word";
		public static String AD_TEXT = "text";
	}
    
	public static class FLAG {
		public static boolean FINISH_VIDEO;
		public static boolean FINISH_PRECONNNECT;
		public static boolean STATE_RECEIVE;
		public static String  LAST_TAB = "NULL";
		public static boolean RECEIVE_HAS_AD;
		public static String RECEIVE_LIMIT_MODE;
	}
	
    public static final class Security {
    	public static final String DES_KEY = "12345678";
    }
    
    public static final class Networks {
        public static final String SERVER_PREFIX = "";
    	public static final int SERVER_PORT = 12345;
    	public static final String AD_BASE_HTTPURL = "http://wifi4us.duapp.com/getadid.php";
    	public static final int TIME_INTERVAL_AD = 20000;
    	public static final int TIME_INTERVAL = 2500;
    	public static final int TIME_INTERVAL_RECEIVE = 5000;
        public static final String REGISTER_BASE_HTTPURL = "http://wifi4us.duapp.com/register.php";
        public static final String GETCREDIT_BASE_HTTPURL = "http://wifi4us.duapp.com/getcredit.php";
        public static final String LOTTERY_HISTORY_HTTPURL = "http://wifi4us.duapp.com/get_lottery_info.php";
        public static final String CREDIT_PER_LOTTERY_HTTPURL = "http://wifi4us.duapp.com/get_credit_mechanism.php";
        public static final String EXCHANGE_LOTTERY_HTTPURL = "http://wifi4us.duapp.com/reduce.php";
        public static final String ADD_CREDIT_HTTPURL = "http://wifi4us.duapp.com/add.php";
    	public static final int WIFICONNECT_TRIALS = 10;
    }
    
    public static final class BroadcastSend {
        public static final String AP_STATE_OPEN_ACTION = "com.paad.wifi4us.apopen";
        public static final String AP_STATE_SHUT_ACTION = "com.paad.wifi4us.apshut";
        public static final String LISTEN_SETUP = "com.paad.wifi4us.listen.setup";
        public static final String CONNECTION_SETUP = "com.paad.wifi4us.connection.setup";
        public static final String CONNECTION_HEARTBEAT = "com.paad.wifi4us.connection.heartbeat";
        public static final String CONNECTION_HEARTBEAT_EXTRA_TRAFFIC = "com.paad.wifi4us.connection.heartbeat.extra.traffic";
        public static final String CONNECTION_FINISH = "com.paad.wifi4us.connection.finish";
    }
    
    public static final class BroadcastReceive {
    	public static final String CONMUNICATION_SETUP_INTERRUPT = "com.paad.wifi4us.conmunication.setup.interrupt";
    	public static final String CONMUNICATION_SETUP = "com.paad.wifi4us.conmunication.setup";
        public static final String CONMUNICATION_SETUP_EXTRA_STATE = "com.paad.wifi4us.conmunication.setup.extra.state";
        public static final String CONMUNICATION_SETUP_EXTRA_AD = "com.paad.wifi4us.conmunication.setup.extra.adword";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN = "com.paad.wifi4us.conmunication.setup.heartbeaten";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME = "com.paad.wifi4us.conmunication.setup.heartbeaten.time";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC = "com.paad.wifi4us.conmunication.setup.heartbeaten.traffic";	
    }
    
    public static final class StartIntentKey {
        public static final String VIDEO_EXTRA_AD = "com.paad.wifi4us.video.extra.ad";
    }
    
    public static final class PreventAbuse {
        public static boolean DOUBLE_START_SEND;
        public static boolean DOUBLE_STOP_SEND;

    }
    
}
