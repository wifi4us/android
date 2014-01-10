package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

public class SendService extends Service {
	public int WIFI_AP_STATE_DISABLING;  
    public int WIFI_AP_STATE_DISABLED;  
    public int WIFI_AP_STATE_ENABLING;  
    public int WIFI_AP_STATE_ENABLED;  
    public int WIFI_AP_STATE_FAILED;
    public static final String AP_STATE_OPEN_ACTION = "com.paad.wifi4us.apopen";
    public static final String AP_STATE_SHUT_ACTION = "com.paad.wifi4us.apshut";
    public static final String LISTEN_SETUP = "com.paad.wifi4us.listen.setup";
    public static final String CONNECTION_SETUP = "com.paad.wifi4us.connection.setup";
    public static final String CONNECTION_HEARTBEAT = "com.paad.wifi4us.connection.heartbeat";
    public static final String CONNECTION_HEARTBEAT_EXTRA_TRAFFIC = "com.paad.wifi4us.connection.heartbeat.extra.traffic";
    public static final String CONNECTION_FINISH = "com.paad.wifi4us.connection.finish";

    
	private final IBinder binder = new MyBinder();
	private WifiManager wifiManager;
	private WifiConfiguration apConfig;

	private Socket socket;
	private ServerSocket serverSocket;
	private InputStreamReader in;
	private PrintWriter out;
	private static final int SERVER_PORT = 12345;
	private static final int TIME_INTERVAL_AD = 20000;
	private static final int TIME_INTERVAL = 5000;
	
	public class MyBinder extends Binder {  
		SendService getService() {  
            return SendService.this;  
        }  
    }  
	
	public IBinder onBind(Intent intent) {  
		return binder;  
	} 
	
	public int onStartCommand(Intent intent, int flags, int startId) {
	      return START_STICKY;
	  }
	public void onCreate() { 
		wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		apConfig = new WifiConfiguration();

        super.onCreate();  
        
		try{
			serverSocket = new ServerSocket(SERVER_PORT);	

			WIFI_AP_STATE_DISABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLING").get(wifiManager);
			WIFI_AP_STATE_DISABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLED").get(wifiManager);
			WIFI_AP_STATE_ENABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLING").get(wifiManager);
			WIFI_AP_STATE_ENABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager);
			WIFI_AP_STATE_FAILED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_FAILED").get(wifiManager);
		}catch(Exception e){
        	e.printStackTrace();
        }
		
    }  
	
	public void onDestroy() {  
        super.onDestroy();  
	}

	
	public void WifiApOff(){    	
		Runnable myRunnable = new Runnable(){
			public void run(){
				while(getWifiApState() == WIFI_AP_STATE_ENABLING){
					try {
						Thread.sleep(500);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
				}
				if(setWifiApEnabled(false)){
					int try_count = 0;
					while(true){
						try {
							Thread.sleep(500);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
						if(getWifiApState() == WIFI_AP_STATE_DISABLED){
							sendApShutSuccessBroadcast();
							wifiManager.setWifiEnabled(true); 
							while(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							break;
						}else{
							try_count++;
							if(try_count > 20){
								sendApShutFailBroadcast();
								break;
							}
						}
					}
				}else{
					sendApShutFailBroadcast();
				}
			 
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}
	
	
	
	public void WifiApOn(){			 
		
		
		Runnable myRunnable = new Runnable(){
			public void run(){
				wifiManager.setWifiEnabled(false); 
				while(wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED){
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				while(getWifiApState() == WIFI_AP_STATE_DISABLING){
					try {
						Thread.sleep(500);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
				}

				if(setWifiApEnabled(true)){
					int try_count = 0;
					while(true){
						try {
							Thread.sleep(500);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
						
				 		if(getWifiApState() == WIFI_AP_STATE_ENABLED){
							sendApOpenSuccessBroadcast();
							break;
						}else{
							try_count++;
							if(try_count > 20){
								sendApOpenFailBroadcast();
								break;
							}
						}
					}
				}else{
					sendApOpenFailBroadcast();
				}
			 
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}

	public void ListenHeartBeat(){
		Runnable heartBeat = new Runnable(){
			public void run(){
				socket = null;
				try{
					socket = serverSocket.accept();
					socket.setSoTimeout(TIME_INTERVAL_AD);

	            	in = new InputStreamReader(socket.getInputStream());
					out = new PrintWriter(socket.getOutputStream());

					BufferedReader reader = new BufferedReader(in);

	            	String info = reader.readLine();
	            	if(info.equals("hello_server")){
	            		out.println("hello_client");
	            		out.flush();
	            	}
	            	
	            	boolean connect_setup_done = false;
		            while(true){
		            	info = reader.readLine();
						socket.setSoTimeout(TIME_INTERVAL);
		            	if(info == null){
							WifiApOff();
		            	}
		            	if(!connect_setup_done){
							sendConnectionSetupBroadcast();
							connect_setup_done = true;
		            	}
		            	sendConnectionHeartbeatBroadcast(info);
		            }
				}catch(SocketTimeoutException e){
					e.printStackTrace();
					WifiApOff();
					//heartbeat stop 
				}catch(Exception e){
					e.printStackTrace();
				}
			
			}
				
		};
		
		Runnable moniterAp = new Runnable(){
			public void run(){
				while(true){
					try{
						SystemClock.sleep(1000);

						if(getWifiApState() == WIFI_AP_STATE_DISABLED){
								sendConnectionFinishBroadcast();	
								out.close(); 
					        	in.close();
					        	socket.close();
					        	return;
						}
					}catch(Exception e){
						e.printStackTrace();
						return;
					}
				}
			}
		};
		
		Thread thread1 = new Thread(heartBeat);
		thread1.start();
		
		Thread thread2 = new Thread(moniterAp);
		thread2.start();
		
		sendListenSetupBroadcast();
	}
		
	
	 public boolean setWifiApEnabled(boolean enabled) {
		 	boolean isHtc = false;  
		 	try {  
		 		isHtc = WifiConfiguration.class  
                     .getDeclaredField("mWifiApProfile") != null;  
		 	} catch (java.lang.NoSuchFieldException e) {  
		 		isHtc = false;  
		 	}  
			
	        try {  
	        	
	            apConfig = new WifiConfiguration();  
	            apConfig.SSID =generateSSID("00000001", "30", "5", "19851123");  
	         	apConfig.preSharedKey="19851123";  
	         	apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
	         	apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
	         	apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
	         	apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
	         	apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);  
	         	apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
	         	apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
	         	
	         	if (isHtc) {  
	         		setHtcConfig(apConfig);  
	            }  
	         	 
	            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);  
	            return (Boolean) method.invoke(wifiManager, apConfig, enabled);  

	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	            return false;
	        }  
	    }  

	 	public int getWifiApState() {  
	        try {  
	            Method method = wifiManager.getClass().getMethod("getWifiApState");  
	            int i = (Integer) method.invoke(wifiManager);  
	            return i;  
	        } catch (Exception e) {  

	            return WIFI_AP_STATE_FAILED;  
	        }  
	    }  
	 	

	 	private void sendApShutSuccessBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(AP_STATE_SHUT_ACTION);  
            intent.putExtra("apstate", "ok");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApShutFailBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(AP_STATE_SHUT_ACTION);  
            intent.putExtra("apstate", "fail");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApOpenSuccessBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(AP_STATE_OPEN_ACTION);  
            intent.putExtra("apstate", "ok");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApOpenFailBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(AP_STATE_OPEN_ACTION);  
            intent.putExtra("apstate", "fail");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendListenSetupBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(LISTEN_SETUP);  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionSetupBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(CONNECTION_SETUP);  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionHeartbeatBroadcast(String info){
	 		Intent intent  = new Intent();  
            intent.setAction(CONNECTION_HEARTBEAT);
            intent.putExtra(CONNECTION_HEARTBEAT_EXTRA_TRAFFIC, info);
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionFinishBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(CONNECTION_FINISH);
            sendBroadcast(intent);  
	 	}
	 	
	 	private String generateSSID(String userid, String time, String traffic, String passwd){
	 		return "111111";
	 	}
	 	
	 	private void setHtcConfig(WifiConfiguration config) {  
	        try {  
	            Field mWifiApProfileField = WifiConfiguration.class  
	                    .getDeclaredField("mWifiApProfile");  
	            mWifiApProfileField.setAccessible(true);  
	            Object hotSpotProfile = mWifiApProfileField.get(config);  
	            mWifiApProfileField.setAccessible(false);  
	  
	  
	            if (hotSpotProfile != null) {  
	                Field ssidField = hotSpotProfile.getClass().getDeclaredField("SSID");  
	                ssidField.setAccessible(true);  
	                ssidField.set(hotSpotProfile, config.SSID);  
	                ssidField.setAccessible(false);  
	  
	  
	                Field keyField = hotSpotProfile.getClass().getDeclaredField("key");  
	                keyField.setAccessible(true);  
	                keyField.set(hotSpotProfile, config.preSharedKey);  
	                keyField.setAccessible(false);  
	  
	  
	                Field dhcpField = hotSpotProfile.getClass().getDeclaredField("dhcpEnable");  
	                dhcpField.setAccessible(true);  
	                dhcpField.setInt(hotSpotProfile, 1);  
	                dhcpField.setAccessible(false);  
	                
	                Field secureField = hotSpotProfile.getClass().getDeclaredField("secureType");
	                secureField.setAccessible(true);  
	                secureField.set(hotSpotProfile, "wpa-psk");  
	                secureField.setAccessible(false); 
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	    }  
	 	
	 	
}
