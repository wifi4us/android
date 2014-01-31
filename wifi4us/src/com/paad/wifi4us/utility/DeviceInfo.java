package com.paad.wifi4us.utility;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Here is some shared members used by multiple components.
 * @author yangshi
 *
 */
public class DeviceInfo {
	private static DeviceInfo instance = null;

	private Context context;
	private TelephonyManager telephonyManager;
	synchronized public static DeviceInfo getInstance(Context c) {
		if (instance == null) {
			instance = new DeviceInfo();
			instance.init(c);
		}
		return instance;
	}
	
	public String getMake() {
		return Build.MANUFACTURER;
	}
	
	public String getModel() {
		return Build.MODEL;
	}
	
	public String getAndroidVer() {
		return Build.VERSION.RELEASE;
	}
	
	public String getCarrier() {
		return telephonyManager.getSimOperatorName() + "." + telephonyManager.getNetworkType();
	}
	
	public String getResolution() {
		DisplayMetrics metrics = new DisplayMetrics();
		Display display = (Display) ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		Integer w = Integer.valueOf(metrics.widthPixels);
		Integer h = Integer.valueOf(metrics.heightPixels);
		return h.toString() + "x" + w.toString();
	}
	
	public String getIMEI() {
		if (telephonyManager.getDeviceId() == null) {
			return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} else {
			return telephonyManager.getDeviceId();
		}
	}
	
	public DeviceInfo() {}

	protected void init(Context c) {
		context = c;
		telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
	}

}
