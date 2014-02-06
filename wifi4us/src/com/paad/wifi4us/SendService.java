package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.MyWifiManager;
import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class SendService extends Service {

	private final IBinder binder = new MyBinder();
	private MyWifiManager myWifiManager;
    private String randomString;

	private Socket socket;
	private ServerSocket serverSocket;
	private InputStreamReader in;
	private PrintWriter out;
	
	private SharedPreferenceHelper sharedPreference;

	
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
        super.onCreate();  
		myWifiManager = new MyWifiManager(getApplicationContext());   
    	sharedPreference = new SharedPreferenceHelper(getApplicationContext());
		try{
	    	serverSocket = new ServerSocket(Constant.Networks.SERVER_PORT);	
		}catch(Exception e){
			e.printStackTrace();
		}
		
    }  
	
	public void WifiApOff(){
		Runnable myRunnable = new Runnable(){
			public void run(){
				while(myWifiManager.getWifiApState() == myWifiManager.WIFI_AP_STATE_ENABLING){
					try {
						Thread.sleep(100);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
				}
				
				if(myWifiManager.setWifiApEnabled(false, null, null)){
					int try_count = 0;
					while(true){
						try {
							Thread.sleep(500);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
						if(myWifiManager.getWifiApState() == myWifiManager.WIFI_AP_STATE_DISABLED){
							sendApShutSuccessBroadcast();
							myWifiManager.getWifiManager().setWifiEnabled(true); 
							while(myWifiManager.getWifiManager().getWifiState() != WifiManager.WIFI_STATE_ENABLED){
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
		randomString = PasswdUtil.getRandomPasswd();
		myWifiManager.getWifiManager().setWifiEnabled(false); 
		Runnable myRunnable = new Runnable(){
			public void run(){
				while(myWifiManager.getWifiManager().getWifiState() != WifiManager.WIFI_STATE_DISABLED){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				while(myWifiManager.getWifiApState() == myWifiManager.WIFI_AP_STATE_DISABLING){
					try {
						Thread.sleep(100);
			        } catch (InterruptedException e) {
			            e.printStackTrace();
			        }
				}
				
				String ssid;
				String passwd;
				try{
					ssid = generateSSID();
					passwd = generatePasswd();
				}catch(Exception e){
					sendApOpenFailBroadcast();
					e.printStackTrace();
					return;
				}
				
				if(myWifiManager.setWifiApEnabled(true, ssid, passwd)){
					int try_count = 0;
					while(true){
						try {
							Thread.sleep(500);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
						
				 		if(myWifiManager.getWifiApState() == myWifiManager.WIFI_AP_STATE_ENABLED){
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
					socket.setSoTimeout(Constant.Networks.TIME_INTERVAL_AD);

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
						socket.setSoTimeout(Constant.Networks.TIME_INTERVAL);
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
						SystemClock.sleep(1000);

						if(myWifiManager.getWifiApState() == myWifiManager.WIFI_AP_STATE_DISABLED){
								sendConnectionFinishBroadcast();	
								try{
									out.close(); 
								}catch(Exception e){
									e.printStackTrace();
								}
								try{
						        	in.close();
								}catch(Exception e){
									e.printStackTrace();
								}
								try{
						        	socket.close();
								}catch(Exception e){
									e.printStackTrace();
								}

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
		

	 	private void sendApShutSuccessBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.AP_STATE_SHUT_ACTION);  
            intent.putExtra("apstate", "ok");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApShutFailBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.AP_STATE_SHUT_ACTION);  
            intent.putExtra("apstate", "fail");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApOpenSuccessBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.AP_STATE_OPEN_ACTION);  
            intent.putExtra("apstate", "ok");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendApOpenFailBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.AP_STATE_OPEN_ACTION);  
            intent.putExtra("apstate", "fail");  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendListenSetupBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.LISTEN_SETUP);  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionSetupBroadcast(){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.CONNECTION_SETUP);  
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionHeartbeatBroadcast(String info){
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.CONNECTION_HEARTBEAT);
            intent.putExtra(Constant.BroadcastSend.CONNECTION_HEARTBEAT_EXTRA_TRAFFIC, info);
            sendBroadcast(intent);  
	 	}
	 	
	 	private void sendConnectionFinishBroadcast(){
	 		//get reward for sending
	 		Intent intent  = new Intent();  
            intent.setAction(Constant.BroadcastSend.CONNECTION_FINISH);
            sendBroadcast(intent);  
	 	}
	 		 	
	 	private String generateSSID() throws Exception{
	 		String namepart = "W" + sharedPreference.getString("USER_ID");
	 		
	 		String passwdpart = PasswdUtil.encryptDES(randomString, Constant.Security.DES_KEY);
	 		
	 		String adMode = sharedPreference.getString("SEND_AD_MODE");
	 		String limitMode = sharedPreference.getString("SEND_LIMIT_MODE");
	 		String modepart1;
	 	    String modepart2;
	 		if(adMode.equals("YES")){
	 			modepart1 = "01";
	 		}else{
	 			modepart1 = "00";
	 		}
	 		if(limitMode.equals("30")){
	 			modepart2 = "30";
	 		}else if(limitMode.equals("60")){
	 			modepart2 = "60";
	 		}else{
	 			modepart2 = "UN";
	 		}
	 		String modepart = modepart1 + modepart2;
	 		
	 		String signpart = PasswdUtil.getMD5Sign(namepart + passwdpart + modepart);
	 		return namepart + passwdpart + modepart + signpart;
	 	}
	 	
	 	private String generatePasswd(){
	 		return randomString + "1";
	 	}
}
