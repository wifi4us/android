package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.DeviceInfo;
import com.paad.wifi4us.utility.MyWifiManager;
import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
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
	private WakeLock wakeLock;  
	private ConnectivityManager connectivityManager;
	
	private String showcredit;
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
    	connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		Constant.FLAG.ADD_ONCE = false;

    	wakeLock = null;
		try{
	    	serverSocket = new ServerSocket(Constant.Networks.SERVER_PORT);	
		}catch(Exception e){
			e.printStackTrace();
		}
		
    }  
	
	public void WifiApOff(){
		stopForeground();
		Runnable myRunnable = new Runnable(){
			public void run(){
            	if(Constant.FLAG.TRAFFIC_SHARED > 0 && sharedPreference.getString("SEND_AD_MODE").equals("YES") && Constant.FLAG.ADD_ONCE){
            		Constant.FLAG.ADD_ONCE = false;
            		AddCredit(Constant.FLAG.TRAFFIC_SHARED);
            		Constant.FLAG.TRAFFIC_SHARED = 0;
            	}

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
		startForeground();
    	Constant.FLAG.TRAFFIC_SHARED = 0;
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
					String limitMode = sharedPreference.getString("SEND_LIMIT_MODE");
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
            		Constant.FLAG.ADD_ONCE = true;
		            while(true){
		            	info = reader.readLine();
						socket.setSoTimeout(Constant.Networks.TIME_INTERVAL);
						
		            	if(info == null){
							WifiApOff();
		            	}
		            	if(!isNumeric(info)){
		            		continue;
		            	}
		            	
		            	Constant.FLAG.TRAFFIC_SHARED = Double.parseDouble(info);
		            	
		            	if(limitMode.equals("30")){
		            		if(Integer.parseInt(info) > 5 * 1024 * 1024){
			            		WifiApOff();
			            	}
		            	}
		            	
		            	if(limitMode.equals("60")){
		            		if(Integer.parseInt(info) > 10 * 1024 * 1024){
			            		WifiApOff();
			            	}
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
		
		Runnable moniterMobile = new Runnable(){
			public void run(){
				while(true){
						SystemClock.sleep(1000);
						NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
						if (!mobNetInfo.isConnected()){
							Handler handler = new Handler(Looper.getMainLooper());                                                 
					    	handler.post(new Runnable() {     
			                         public void run() {     
			             		    	Toast.makeText(getApplicationContext(), "数据流量打开才能分享", Toast.LENGTH_LONG).show();  
			                         }     
			                });
							WifiApOff();
							return;
						}
				}
			}
		};
		
		String limitMode = sharedPreference.getString("SEND_LIMIT_MODE");
		if(!limitMode.equals("UN")){
			Thread thread1 = new Thread(heartBeat);
			thread1.start();
		}
		
		Thread thread2 = new Thread(moniterAp);
		thread2.start();
		
		Thread thread3 = new Thread(moniterMobile);
		thread3.start();
		
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

		private void startForeground(){
			acquireWakeLock();
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			   
			mBuilder.setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setContentTitle("正在使用一起wifi的服务");
			mBuilder.setContentText("使用结束后我会在通知栏消失哦~~");
			mBuilder.setTicker("正在使用一起wifi的服务");//第一次提示消息的时候显示在通知栏上
			mBuilder.setNumber(12);
			//构建一个Intent
			Intent resultIntent = new Intent(this, MainActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			//封装一个Intent
			PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			// 设置通知主题的意图
			mBuilder.setContentIntent(resultPendingIntent);
			final Notification notification = mBuilder.build();
			startForeground(1, notification);			   
		}

		private void stopForeground(){
			releaseWakeLock();
			stopForeground(true);
		}
		
	    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行  
	    private void acquireWakeLock()  
	    {  
	        if (null == wakeLock)  
	        {  
	            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);  
	            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");  
	            if (null != wakeLock)  
	            {  
	                wakeLock.acquire();  
	            }  
	        }  
	    }  
	      
	    //释放设备电源锁  
	    private void releaseWakeLock()  
	    {  
	        if (null != wakeLock)  
	        {  
	            wakeLock.release();  
	            wakeLock = null;  
	        }  
	    } 

	    private static boolean isNumeric(String str){
	        Pattern pattern = Pattern.compile("[0-9]*");
	        return pattern.matcher(str).matches();   
	    }
	    
	    private void AddCredit(double traffic){
	    	String credit = RemoteInfoFetcher.addUserCreditWithTraffic(DeviceInfo.getInstance(this).getIMEI(), sharedPreference.getString("USER_ID"), String.valueOf(traffic / 1024), 0);
	    	if(!credit.equals(null)){
	    		String tmpcredit = sharedPreference.getString("CREDIT");
		    	sharedPreference.putString("CREDIT", credit);
		    	DecimalFormat df = new DecimalFormat( "0.0");
	            showcredit = String.valueOf(df.format(Double.parseDouble(credit) - Double.parseDouble(tmpcredit)));
		    	
		    	Handler handler = new Handler(Looper.getMainLooper());                                                 
		    	handler.post(new Runnable() {     
                         public void run() {     
             		    	Toast.makeText(getApplicationContext(), "本次分享获得" + showcredit + "积分", Toast.LENGTH_LONG).show();  
                         }     
                }); 
	    	}else{
	    		Handler handler = new Handler(Looper.getMainLooper());                                                 
		    	handler.post(new Runnable() {     
                         public void run() {     
             	            Toast.makeText(getApplicationContext(), "获取积分失败，服务器繁忙", Toast.LENGTH_LONG).show();  
                         }     
                });
	    	}
	    }
	    
}
