package com.paad.wifi4us;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.util.SimpleArrayMap;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.paad.wifi4us.utility.HttpXmlParser;

public class ReceiveService extends Service {
	private String make;
	private String model;
	private String androidversion;
	private String carrier;
	private String resolution;
	private String userid;
	
	
	private final IBinder binder = new MyBinder();
	private WifiManager wifiManager;
    private ArrayList<String> wifiApList; 
    private String connectinfo;
    private String adWord;
    private String adId;
    private String adURL;
    private String adLength;
    private String requestURL;
    
    private static int totalTimeSeconds;
    private static long totalTrafficBytes;

    
    public static final String CLIENT_STATE_CONNECTED_TO_AP = "com.paad.wifi4us.connectedtoap";
    public static final String CLIENT_STATE_LEAVE_FROM_AP = "com.paad.wifi4us.leavefromap";
    public static final String CLIENT_STATE_CONNECTED_TO_AP_EXTRA = "com.paad.wifi4us.connectedtoap.extra";
	public static final String CONMUNICATION_SETUP = "com.paad.wifi4us.conmunication.setup";
    public static final String CONMUNICATION_SETUP_EXTRA_STATE = "com.paad.wifi4us.conmunication.setup.extra.state";
    public static final String CONMUNICATION_SETUP_EXTRA_ADWORD = "com.paad.wifi4us.conmunication.setup.extra.adword";
    public static final String CONMUNICATION_SETUP_EXTRA_ADID = "com.paad.wifi4us.conmunication.setup.extra.adid";
    public static final String AD_BASE_HTTPURL = "http://wifi4us.duapp.com/getadid.php";
    public static final String CONMUNICATION_SETUP_HEART_BEATEN = "com.paad.wifi4us.conmunication.setup.heartbeaten";
    public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME = "com.paad.wifi4us.conmunication.setup.heartbeaten.time";
    public static final String CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC = "com.paad.wifi4us.conmunication.setup.heartbeaten.traffic";

    
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
		wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiApList = new ArrayList<String>();
    }  
	
	public void onDestroy() {  
        super.onDestroy();  
	}

	
	public void WifiSwitcher(){
		Runnable myRunnable = new Runnable(){
			public void run(){
				if (wifiManager.isWifiEnabled()) { 
					wifiManager.setWifiEnabled(false); 
				} 
				else { 
					wifiManager.setWifiEnabled(true); 
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
				wifiManager.startScan();
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}
	
	//get the scan result list
	public ArrayList<String> getWifiScanResult(){
		wifiApList.clear();
		List<ScanResult> scanResultList = wifiManager.getScanResults();
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
				WifiConfiguration wifiConfig=new WifiConfiguration();  
				wifiConfig.SSID="\"" + ssid + "\"";  
				wifiConfig.preSharedKey="\"" + passwd + "\"";  
				wifiConfig.hiddenSSID = true;  
				wifiConfig.status = WifiConfiguration.Status.ENABLED;  
				wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
				wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);  
				wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
				wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
				wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
				wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);  
				wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
				wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
				
				wifiManager.enableNetwork(wifiManager.addNetwork(wifiConfig), true);
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}

	public void WifiDisconnect(){
		int current_networkid = wifiManager.getConnectionInfo().getNetworkId();
		wifiManager.removeNetwork(current_networkid);
	}

	public void EstablishConmunication(){
		
		Runnable setupConnectionRunner = new Runnable(){
			public void run(){
				Intent intent = new Intent();
				intent.setAction(CONMUNICATION_SETUP); 
				if(!openSocketConnection()){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
					return;
				}
				if(!setHeartBeat()){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
					return;
				}
				if(!getAdvertisement()){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
					return;
				}

				intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "ok");
				intent.putExtra(CONMUNICATION_SETUP_EXTRA_ADWORD, adWord);
				intent.putExtra(CONMUNICATION_SETUP_EXTRA_ADID, adId);
				sendBroadcast(intent);
					

			}
		};
		
		Thread thread = new Thread(setupConnectionRunner);
		thread.start();
	}	
	
	private boolean openSocketConnection(){
		try{
			Socket socket=new Socket(getIpFromInt(wifiManager.getDhcpInfo().gateway), 12580);
			BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
			PrintWriter os=new PrintWriter(socket.getOutputStream());
			BufferedReader is=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String readline;
			readline=sin.readLine(); //从系统标准输入读入一字符串
			while(!readline.equals("bye")){
				os.println(readline);
				os.flush();
				System.out.println("Client:"+readline);
				System.out.println("Server:"+is.readLine());
				readline=sin.readLine(); //从系统标准输入读入一字符串
			}

			os.close(); //关闭Socket输出流
			is.close(); //关闭Socket输入流
			socket.close(); //关闭Socket
		}catch(Exception e) {
			System.out.println("Error"+e); //出错，则打印出错信息
		}	
		return true;
	}
	
	private boolean setHeartBeat(){
		//init the time counter total seconds
		totalTimeSeconds = 0;
		totalTrafficBytes = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes();
		
		//start the alarm and send broadcast every 5 seconds
		Timer timer = new Timer();  

	    TimerTask task = new TimerTask(){  
	        public void run() {  

	        	totalTimeSeconds = totalTimeSeconds + 3;
				String time = String.valueOf(totalTimeSeconds);
				long totalTrafficBytesShown = TrafficStats.getTotalTxBytes() + TrafficStats.getTotalRxBytes() - totalTrafficBytes;
				String traffic = String.valueOf(totalTrafficBytesShown);
				
				Intent heartbeat = new Intent();
				heartbeat.setAction(CONMUNICATION_SETUP_HEART_BEATEN); 
				heartbeat.putExtra(CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TIME, time);
				heartbeat.putExtra(CONMUNICATION_SETUP_HEART_BEATEN_EXTRA_TRAFFIC, traffic);
				getApplicationContext().sendBroadcast(heartbeat);
				
				//send traffic to ap host through socket
	        }  
	          
	    };  
	    timer.schedule(task, 0, 1000);
	
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
		
			SharedPreferences sharedata = getSharedPreferences(getApplicationContext().getPackageName(), MODE_PRIVATE); 
			userid = URLEncoder.encode(sharedata.getString("USER_ID", "NULL"), "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String parameters = "?" + "userid" + "=" + userid + "&"
									+ "make" + "=" + make + "&"
									+ "model" + "=" + model + "&"
									+ "resolution" + "=" + resolution + "&"
									+ "carrier" + "=" + carrier + "&"
									+ "androidversion" + "=" + androidversion;
		requestURL = AD_BASE_HTTPURL + parameters;
		
		SimpleArrayMap<String, String> result = new SimpleArrayMap<String, String>();
		HttpXmlParser xpp = new HttpXmlParser();

		

		if(xpp.getResultFromURL(requestURL, result)){
			adWord = result.get("adword");
    		adURL = result.get("url");
    		adLength = result.get("length");
    		adId = result.get("adid");
		}else{
			return false;	
		}
		
		//check local and download
				
		return true;
	}
	
	public DhcpInfo getAPinfo(){
		return wifiManager.getDhcpInfo();
	}

	
	private String getPassWord(){
		//return "4001001111";
		//return "19851123";
		return "maancoffee";
	}
	
	private String getSSID(){
		return "Maan Coffee";
		//return "111111";
		//return "TP-LINK_3003";
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
