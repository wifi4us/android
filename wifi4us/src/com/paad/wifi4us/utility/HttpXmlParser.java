package com.paad.wifi4us.utility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v4.util.SimpleArrayMap;

public class HttpXmlParser {
	public static ArrayList<SimpleArrayMap<String, String>> getResultFromURL(String requestURL) {
		URL url;
		HttpURLConnection urlConnection;
		ArrayList<SimpleArrayMap<String, String>> mapInArr;
		try {
			System.out.println(requestURL);

			url = new URL(requestURL);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(5000);
			urlConnection.addRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			urlConnection.connect();

			int status = urlConnection.getResponseCode();

			if (status == HttpURLConnection.HTTP_OK) {
				InputStream in = urlConnection.getInputStream();

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(in, null);

				int eventType = xpp.getEventType();
				
				
				SimpleArrayMap<String, String> currentMap = null;  
				mapInArr = null;  
	            while (eventType != XmlPullParser.END_DOCUMENT) {  
	                switch (eventType) {  
	                case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理  
	                	mapInArr = new ArrayList<SimpleArrayMap<String, String>>();// 实例化集合类  
	                    break;  
	                case XmlPullParser.START_TAG://开始读取某个标签  
	                    //通过getName判断读到哪个标签，然后通过nextText()获取文本节点值，或通过getAttributeValue(i)获取属性节点值  
	                    String name = xpp.getName();  
	                    if (name.equalsIgnoreCase("record")) {  
	                    	currentMap = new SimpleArrayMap<String, String>();  
	                    	for(int i=0;i<xpp.getAttributeCount();i++){   
	                    		currentMap.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));  
	                        }   
	                    } else if (currentMap != null) { 
	                    	currentMap.put(name, xpp.nextText());// 如果后面是Text元素,即返回它的值    
	                    }  
	                    break;  
	                case XmlPullParser.END_TAG:// 结束元素事件  
	                    //读完一个Person，可以将其添加到集合类中  
	                    if (xpp.getName().equalsIgnoreCase("record")&& currentMap != null) {  
	                    	mapInArr.add(currentMap);  
	                    	currentMap = null;  
	                    }  
	                    break;  
	                }  
	                eventType = xpp.next();  
	            }  
				in.close();
				urlConnection.disconnect();
				return mapInArr;
			}else{
				urlConnection.disconnect();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
