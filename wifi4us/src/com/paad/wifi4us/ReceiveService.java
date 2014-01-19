package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.paad.wifi4us.utility.Constant;
import com.paad.wifi4us.utility.HttpDownLoader;
import com.paad.wifi4us.utility.HttpXmlParser;
import com.paad.wifi4us.utility.MyWifiManager;
import com.paad.wifi4us.utility.PasswdUtil;
import com.paad.wifi4us.utility.SharedPreferenceHelper;

public class ReceiveService extends Service {
	private String make;
	private String model;
	private String androidversion;
	private String carrier;
	private String resolution;
	private String userid;
	
	
	private final IBinder binder = new MyBinder();
	private MyWifiManager myWifiManager;
    private ArrayList<String> wifiApList; 
    private String connectinfo;
    private String adWord;
    private String adId;
    private String adURL;
    private String adLength;
    private String requestURL;
    
    private static int totalTimeSeconds;
    private static long totalTrafficBytes;
    private Timer timer; 

 
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

		Runnable myRunnable = new Runnable(){
			public void run(){
					String passwd = getPassWord();
					String ssid = getSSID();
					myWifiManager.WifiSetupConnect(ssid, passwd);
				}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
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
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "已经有人接入这个分享者了");
					sendBroadcast(intent);
					return;
				}
				
				if(!getAdvertisement()){
					WifiDisconnectCompletely();
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "分享者关闭的3G或者网速极慢");
					sendBroadcast(intent);
					return;
				}
				
				if(!setHeartBeat()){
					WifiDisconnectCompletely();
					intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "正常连接中断");
					sendBroadcast(intent);
					return;
				}
				
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_STATE, "ok");
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADWORD, adWord);
				intent.putExtra(Constant.BroadcastReceive.CONMUNICATION_SETUP_EXTRA_ADID, adId);
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
		try{
			make = URLEncoder.encode(Build.MANUFACTURER, "UTF-8");
			model = URLEncoder.encode(Build.MODEL, "UTF-8");
			androidversion = URLEncoder.encode(Build.VERSION.RELEASE, "UTF-8");
		
			TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
			carrier = URLEncoder.encode(telephonyManager.getSimOperatorName() + "." + telephonyManager.getNetworkType(), "UTF-8");
		
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = (Display) ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);
			Integer w = Integer.valueOf(metrics.widthPixels);
			Integer h = Integer.valueOf(metrics.heightPixels);
			resolution = URLEncoder.encode(h.toString() + "x" + w.toString(), "UTF-8");
		
			userid = URLEncoder.encode(sharedPreference.getString("USER_ID"), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String parameters = "?" + "userid" + "=" + userid + "&"
									+ "make" + "=" + make + "&"
									+ "model" + "=" + model + "&"
									+ "resolution" + "=" + resolution + "&"
									+ "carrier" + "=" + carrier + "&"
									+ "androidversion" + "=" + androidversion;
		requestURL = Constant.Networks.AD_BASE_HTTPURL + parameters;
		
		SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();
		HttpXmlParser xpp = new HttpXmlParser();
		if(xpp.getResultFromURL(requestURL, result)){
			adWord = result.get("adword");
    		adLength = result.get("length");
    		adId = result.get("adid");
        	adURL = result.get("url");
    		
		}else{
			return false;	
		}
		
		
		String adDir = getApplicationContext().getCacheDir().toString() + "/ad_" + adId + ".3gp";
		File adFile = new File(adDir);
		HttpDownLoader dld = new HttpDownLoader(adURL, adDir);
		boolean downloadResult = true;
		if(adFile.exists()){
			if(adFile.length() < Long.parseLong(adLength)){
				downloadResult = dld.downLoad(adFile.length(), Long.parseLong(adLength) - 1);
			}
		}else{
			downloadResult = dld.downLoad(0, Long.parseLong(adLength) - 1);
		}
		
		if(!downloadResult){
			return false;
		}
		
		
		//check local and download
				
		return true;
	}
	
	public void closeConnection(){
		try{
			timer.cancel();
			out.close();
			in.close();
			socket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
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
