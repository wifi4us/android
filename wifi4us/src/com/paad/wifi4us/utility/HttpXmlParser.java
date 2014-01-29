package com.paad.wifi4us.utility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.data.FetchedInfo;

public class HttpXmlParser {

	public static boolean getResultFromURL(String requestURL,
			SimpleArrayMap<String, String> arrResult) {

	URL url;
	 HttpURLConnection urlConnection;
		try {
			url = new URL(requestURL);
			System.out.println(requestURL);
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
				while (eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG) {
						if (xpp.getName().equals("result")) {
							eventType = xpp.next();
							continue;
						}
						arrResult.put(xpp.getName(), xpp.nextText());

					}
					eventType = xpp.next();
				}
				in.close();
			}
			urlConnection.disconnect();
			return true;
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

	}
	
	public static FetchedInfo getResultFromURL(String requestURL) {
		URL url;
		 HttpURLConnection urlConnection;
		try {
			FetchedInfo result = new FetchedInfo();
			url = new URL(requestURL);
			System.out.println(requestURL);
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

				while(result.addElementFromXml(xpp));
				in.close();
				urlConnection.disconnect();
				return result;
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
