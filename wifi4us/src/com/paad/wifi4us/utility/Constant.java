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
    public static final String SERVER_PREFIX = "";
    
    public static final class Security {
    	public static final String DES_KEY = "12345678";
    }
    
    public static final class Networks {
    	public static final int SERVER_PORT = 12345;
    	public static final String AD_BASE_HTTPURL = "http://wifi4us.duapp.com/getadid.php";
    	public static final int TIME_INTERVAL_AD = 20000;
    	public static final int TIME_INTERVAL = 5000;
        public static final String REGISTER_BASE_HTTPURL = "http://wifi4us.duapp.com/register.php";
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
    	public static final String CONMUNICATION_SETUP = "com.paad.wifi4us.conmunication.setup";
        public static final String CONMUNICATION_SETUP_EXTRA_STATE = "com.paad.wifi4us.conmunication.setup.extra.state";
        public static final String CONMUNICATION_SETUP_EXTRA_ADWORD = "com.paad.wifi4us.conmunication.setup.extra.adword";
        public static final String CONMUNICATION_SETUP_EXTRA_ADID = "com.paad.wifi4us.conmunication.setup.extra.adid";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN = "com.paad.wifi4us.conmunication.setup.heartbeaten";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME = "com.paad.wifi4us.conmunication.setup.heartbeaten.time";
        public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC = "com.paad.wifi4us.conmunication.setup.heartbeaten.traffic";	
    }
}