package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.DeviceInfo;
import com.paad.wifi4us.utility.HttpDownLoader;
import com.paad.wifi4us.utility.MyWifiManager;
import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.RemoteInfoFetcher;
import com.paad.wifi4us.utility.SharedPreferenceHelper;
import com.paad.wifi4us.utility.data.AdContent;

public class ReceiveService extends Service {
	private final IBinder binder = new MyBinder();
	private MyWifiManager myWifiManager;
    private ArrayList<String> wifiApList; 
    private String connectinfo;
    private ArrayList<AdContent> adList;
    
    private static int totalTimeSeconds;
    private static long totalTrafficBytes;
    public  Timer timer; 

 
    private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	private SharedPreferenceHelper sharedPreference;

	
	public class MyBinder extends Binder {  
		ReceiveService getService() {  
            return ReceiveService.this;  
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
		wifiApList = new ArrayList<String>();
    	sharedPreference = new SharedPreferenceHelper(getApplicationContext());
    }  
	
	public void onDestroy() {  
        super.onDestroy();  
	}

	
	public void WifiSwitcher(){
		Runnable myRunnable = new Runnable(){
			public void run(){
				if (myWifiManager.getWifiManager().isWifiEnabled()) { 
					WifiDisconnectCompletely();
					myWifiManager.getWifiManager().setWifiEnabled(false); 
				} 
				else { 
					WifiDisconnectCompletely();
					myWifiManager.getWifiManager().setWifiEnabled(true); 
				} 			
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
		
	}

	
	//scan the wifi signal available 	
	public void WifiScan(){
		Runnable myRunnable = new Runnable(){
			public void run(){
				myWifiManager.getWifiManager().startScan();
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}
	
	//get the scan result list
	public ArrayList<String> getWifiScanResult(){
		wifiApList.clear();
		List<ScanResult> scanResultList = myWifiManager.getWifiManager().getScanResults();
		if(scanResultList.size() > 0){
	        for (int i =0; i < scanResultList.size(); i++) {  
	        	wifiApList.add(scanResultList.get(i).SSID);       
	        }
		}else{
			wifiApList.clear();
		}
        
		return wifiApList;
	}
	
	public void WifiConnect(String rawssid){
		connectinfo = rawssid;

		Runnable wifiConnectRunner = new Runnable(){
			public void run(){
					String passwd = getPassWord();
					String ssid = getSSID();
					myWifiManager.WifiSetupConnect(ssid, passwd);
				}
		};
		
		Runnable wifiStateCheckRunner = new Runnable(){
			public void run(){
				int trial = Constant.Networks.WIFICONNECT_TRIALS;
				Intent intent = new Intent();
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "wifi连接超时");
				intent.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP);

				while(trial > 0){

					trial--;
					try{
						SystemClock.sleep(1000);
						ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			    		State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
			    		if(State.CONNECTED == state){  
							return;
						}
					}catch(Exception e){
						e.printStackTrace();
						WifiDisconnectCompletely();
						sendBroadcast(intent);
						return;
					}
					
				}
				WifiDisconnectCompletely();
				sendBroadcast(intent);
			}
		};
		
		Thread thread1 = new Thread(wifiConnectRunner);
		Thread thread2 = new Thread(wifiStateCheckRunner);
		thread1.start();
		thread2.start();
	}

	public void WifiDisconnectCompletely(){
		myWifiManager.getWifiManager().removeNetwork(myWifiManager.getNetworkId());
	}

	public void WifiDisconnect(){
		myWifiManager.getWifiManager().disconnect();
	}
	
	public void EstablishConmunication(){
		totalTimeSeconds = 0;
		totalTrafficBytes = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();

		Runnable setupConnectionRunner = new Runnable(){
			public void run(){
				Intent intent = new Intent();
				intent.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP); 
				if(!openSocketConnection()){
					WifiDisconnectCompletely();
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "已经有人接入这个分享者");
					sendBroadcast(intent);
					return;
				}
				
				if(!getAdvertisement()){
					WifiDisconnectCompletely();
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "对方3G网速太慢或者网络异常");
					sendBroadcast(intent);
					return;
				}
				
				if(!setHeartBeat()){
					WifiDisconnectCompletely();
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "建立连接异常");
					sendBroadcast(intent);
					return;
				}
				
				String adwords = "";
				String adids = "";
				String adtexts = "";
				for(AdContent adcontent:adList){
					adwords += adcontent.adword + "|";
					adids += adcontent.adid + "|";
					adtexts += adcontent.adtext + "|";
				}
				
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "ok");
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADWORD, adwords);
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADID, adids);
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADTEXT, adtexts);

				sendBroadcast(intent);
			}
		};
		
		Thread thread = new Thread(setupConnectionRunner);
		thread.start();
	}	
	
	private boolean openSocketConnection(){
		try{
			socket=new Socket(getIpFromInt(myWifiManager.getWifiManager().getDhcpInfo().gateway), Constant.Networks.SERVER_PORT);
			socket.setSoTimeout(Constant.Networks.TIME_INTERVAL);

			out = new PrintWriter(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out.println("hello_server");
			out.flush();

			String firstResponse = in.readLine();
			if(firstResponse.equals("hello_client")){
				return true;
			}else{
				return false;
			}
			
		}catch(SocketTimeoutException e){
			e.printStackTrace();
			return false;
			//heartbeat stop 
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}	
	}
	
	private boolean setHeartBeat(){
		//init the time counter total seconds
	
		//start the alarm and send broadcast every 5 seconds
		timer = new Timer();  
		
	    TimerTask task = new TimerTask(){  
	        public void run() {  
	        	try{
		        	totalTimeSeconds = totalTimeSeconds + 3;
					String time = String.valueOf(totalTimeSeconds);
					long totalTrafficBytesShown = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes() - totalTrafficBytes;
					String traffic = String.valueOf(totalTrafficBytesShown);
					
					Intent heartbeat = new Intent();
					heartbeat.setAction(Constant.BroadcastReceive.CONMUNICATION_SETUP_HEART_BEATEN); 
					heartbeat.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME, time);
					heartbeat.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC, traffic);
					
					getApplicationContext().sendBroadcast(heartbeat);
					
					//send traffic to ap host through socket
					out.println("T" + totalTrafficBytesShown);
					out.flush();
					
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}

	        }  
	          
	    };  
	    timer.schedule(task, 0, 3000);
	
		return true;
	}

	
	private boolean getAdvertisement(){
		//get url to request advertisement meta data
		String make = DeviceInfo.getInstance(this).getMake();
		String model = DeviceInfo.getInstance(this).getModel();
		String androidversion = DeviceInfo.getInstance(this).getAndroidVer();
		String carrier = DeviceInfo.getInstance(this).getCarrier();
		String resolution = DeviceInfo.getInstance(this).getResolution();
		String userid = sharedPreference.getString("USER_ID");
		
		adList = RemoteInfoFetcher.fetchAdList(userid, make, model, resolution, carrier, androidversion);


		for(AdContent adcontent:adList){
			String adDir = getApplicationContext().getCacheDir().toString() + "/ad_" + adcontent.adid + ".3gp";
			File adFile = new File(adDir);
			HttpDownLoader dld = new HttpDownLoader(adcontent.url, adDir);
			boolean downloadResult = true;
			if(adFile.exists()){
				if(adFile.length() < Long.parseLong(adcontent.length)){
					downloadResult = dld.downLoad(adFile.length(), Long.parseLong(adcontent.length) - 1);
				}
			}else{
				downloadResult = dld.downLoad(0, Long.parseLong(adcontent.length) - 1);
			}
			
			if(!downloadResult){
				return false;
			}
		}
		
		
		
		//check local and download
				
		return true;
	}
	
	private String getPassWord(){
		String passwd = null;
		try{
			passwd = PasswdUtil.decryptDES(connectinfo.substring(8, 24), Constant.Security.DES_KEY);
		}catch(Exception e){
			e.printStackTrace();
		}
		return passwd + "1";
	}
	
	private String getSSID(){
		return connectinfo;
	}

	private String getIpFromInt(int ip){
		try{
			byte[] b = new byte[] {
				    (byte)((ip      ) & 0xff),
				    (byte)((ip >>  8) & 0xff),
				    (byte)((ip >> 16) & 0xff),
				    (byte)((ip >> 24) & 0xff)
				  };
			return InetAddress.getByAddress(b).getHostAddress();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
