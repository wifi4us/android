package com.paad.wifi4us.utility;

public class Constant {
	public static native String stringFromJNI(int keyName); 
	static{
		System.loadLibrary("com_paad_wifi4us_utility_Constant");
	} 
	
	public static final String UMENG_KEY = "52d0e47956240b1c0b11e8da";
	
	public static final String UMENG_CHANNEL = "main_page";

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
		public static double TRAFFIC_SHARED;
		public static boolean ADD_ONCE;
	}
	
    public static final class Security {
    	public static final String DES_KEY = stringFromJNI(0);
    }
    
    public static final class Networks {
    	public static final int SERVER_PORT = 12345;
    	public static final String AD_BASE_HTTPURL = stringFromJNI(1);
    	public static final int TIME_INTERVAL_AD = 20000;
    	public static final int TIME_INTERVAL = 2500;
    	public static final int TIME_INTERVAL_RECEIVE = 5000;
        public static final String REGISTER_BASE_HTTPURL = stringFromJNI(2);
        public static final String GETCREDIT_BASE_HTTPURL = stringFromJNI(3);
        public static final String LOTTERY_HISTORY_HTTPURL = stringFromJNI(4);
        public static final String CREDIT_PER_LOTTERY_HTTPURL = stringFromJNI(5);
        public static final String EXCHANGE_LOTTERY_HTTPURL = stringFromJNI(6);
        public static final String ADD_CREDIT_HTTPURL = stringFromJNI(7);
    	public static final int WIFICONNECT_TRIALS = 30;
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
