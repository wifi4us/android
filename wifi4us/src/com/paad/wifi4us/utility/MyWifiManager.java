package com.paad.wifi4us.utility;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class MyWifiManager {
	private WifiManager wifiManager;
	private WifiConfiguration apConfig;
	
	public int WIFI_AP_STATE_DISABLING;  
    public int WIFI_AP_STATE_DISABLED;  
    public int WIFI_AP_STATE_ENABLING;  
    public int WIFI_AP_STATE_ENABLED;  
    public int WIFI_AP_STATE_FAILED;
    
    
	public MyWifiManager(Context context){
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		try{
			WIFI_AP_STATE_DISABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLING").get(wifiManager);
			WIFI_AP_STATE_DISABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_DISABLED").get(wifiManager);
			WIFI_AP_STATE_ENABLING  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLING").get(wifiManager);
			WIFI_AP_STATE_ENABLED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED").get(wifiManager);
			WIFI_AP_STATE_FAILED  = (Integer) wifiManager.getClass().getDeclaredField("WIFI_AP_STATE_FAILED").get(wifiManager);
		}catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	public WifiManager getWifiManager(){
		return wifiManager;
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
 	
 	
	public boolean setWifiApEnabled(boolean enabled, String ssid, String passwd) {
	 	boolean isHtc = false;  
	 	try {  
	 		isHtc = WifiConfiguration.class  
                 .getDeclaredField("mWifiApProfile") != null;  
	 	} catch (java.lang.NoSuchFieldException e) {  
	 		isHtc = false;  
	 	}  
		
        try {  
        	
            apConfig = new WifiConfiguration();  
            apConfig.SSID = ssid;
         	apConfig.preSharedKey = passwd;  
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
