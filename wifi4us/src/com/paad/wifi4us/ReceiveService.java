package com.paad.wifi4us;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo.State;
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
    private Context context;
    private String adWord;
    private String adId;
    private String adURL;
    private String adLength;
    private String requestURL;
    
    public static final String CLIENT_STATE_CONNECTED_TO_AP = "com.paad.wifi4us.connectedtoap";
    public static final String CLIENT_STATE_LEAVE_FROM_AP = "com.paad.wifi4us.leavefromap";
    public static final String CLIENT_STATE_CONNECTED_TO_AP_EXTRA = "com.paad.wifi4us.connectedtoap.extra";
	public static final String CONMUNICATION_SETUP = "com.paad.wifi4us.conmunication.setup";
    public static final String CONMUNICATION_SETUP_EXTRA_STATE = "com.paad.wifi4us.conmunication.setup.extra.state";
    public static final String CONMUNICATION_SETUP_EXTRA_ADWORD = "com.paad.wifi4us.conmunication.setup.extra.adword";
    public static final String CONMUNICATION_SETUP_EXTRA_ADID = "com.paad.wifi4us.conmunication.setup.extra.adid";
    public static final String AD_BASE_HTTPURL = "http://wifi4us.duapp.com/getadid.php";
    
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
		
		Runnable clientRunner = new Runnable(){
			public void run(){
				Intent intent = new Intent();
				intent.setAction(CONMUNICATION_SETUP); 
				if(!talkToServer()){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
					return;
				}
			}
		};
		
		
		Runnable adRunner = new Runnable(){
			public void run(){
				Intent intent = new Intent();
				intent.setAction(CONMUNICATION_SETUP); 
				
				if(!getAdvertisement()){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
					return;
				}
				
				

				
				
				
				

	
				if(true){
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "ok");
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_ADWORD, adWord);
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_ADID, adId);
					sendStickyBroadcast(intent);
				}else{ 
					intent.putExtra(CONMUNICATION_SETUP_EXTRA_STATE, "fail");
					sendStickyBroadcast(intent);
				}

			}
		};
		

		
		Thread thread1 = new Thread(clientRunner);
		Thread thread2 = new Thread(adRunner);
		thread1.start();
		thread2.start();
	}	
	
	public DhcpInfo getAPinfo(){
		return wifiManager.getDhcpInfo();
	}
	
	public void PlayVideo(){
		
	}
	
	private String getPassWord(){
		
		return "maancoffee";
	}
	
	private String getSSID(){
		return "Maan Coffee";
	}
	
	private WifiConfiguration IsExsits(String SSID)  
    {  
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();  
           for (WifiConfiguration existingConfig : existingConfigs)   
           {  
             if (existingConfig.SSID.equals("\""+SSID+"\""))  
             {  
                 return existingConfig;  
             }  
           }  
        return null;   
    }  
	
	private boolean talkToServer(){
		
		
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

}
