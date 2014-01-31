package com.paad.wifi4us.utility;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownLoader {
	private URL url;
	private String adDir;
	
	public HttpDownLoader(String adURL, String dir){
		try{
			url = new URL(adURL);
			adDir = dir;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean downLoad(long start, long end){
		try{
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(10000);
			urlConnection.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
			urlConnection.addRequestProperty("Range", "bytes=" + start + "-" + end);


            InputStream input=urlConnection.getInputStream();  
            FileOutputStream fs = new FileOutputStream(adDir, true);  
            int byteread = 0;
            byte[] buffer = new byte[1204];  
            while ((byteread = input.read(buffer)) != -1) {  
                fs.write(buffer, 0, byteread);  
            }
            
            input.close();
            fs.close();
            return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
	}


}
