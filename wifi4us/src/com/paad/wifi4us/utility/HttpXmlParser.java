package com.paad.wifi4us.utility;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v4.util.SimpleArrayMap;

public class HttpXmlParser {
    public static HttpClient httpClient;
    
        public static synchronized HttpClient getHttpClient() {
                  
                        if (null == httpClient) {
                           // ���濮����宸ヤ��
                            try {
                              KeyStore trustStore = KeyStore.getInstance(KeyStore
                                      .getDefaultType());
                               trustStore.load(null, null);
                               SSLSocketFactoryEx sf = new SSLSocketFactoryEx(trustStore);
                               sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //���璁告�����涓绘�虹��楠�璇�
                 
                               HttpParams params = new BasicHttpParams();
                
                               HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                               HttpProtocolParams.setContentCharset(params,
                                       HTTP.UTF_8);
                               HttpProtocolParams.setUseExpectContinue(params, true);
                
                               // 璁剧疆杩���ョ�＄����ㄧ��瓒����
                                 ConnManagerParams.setTimeout(params, 5000);
                               // 璁剧疆杩���ヨ�����
                               HttpConnectionParams.setConnectionTimeout(params, 5000);
                              // 璁剧疆socket瓒����
                              HttpConnectionParams.setSoTimeout(params, 5000);
                
                              // 璁剧疆http https������
                              SchemeRegistry schReg = new SchemeRegistry();
                              schReg.register(new Scheme("http", PlainSocketFactory
                                      .getSocketFactory(), 80));
                             schReg.register(new Scheme("https", sf, 443));
                
                               ClientConnectionManager conManager = new ThreadSafeClientConnManager(
                                        params, schReg);
                 
                                  httpClient = new DefaultHttpClient(conManager, params);
                           } catch (Exception e) {
                                 e.printStackTrace();
                                 return new DefaultHttpClient();
                           }
                        }
                       return httpClient;
                 }
    
	public static ArrayList<SimpleArrayMap<String, String>> getResultFromURL(String requestURL) {
		ArrayList<SimpleArrayMap<String, String>> mapInArr;
		try {
			System.out.println(requestURL);
			HttpClient client = getHttpClient();
			HttpGet get = new HttpGet(requestURL);
			HttpResponse httpResponse = client.execute(get);

			int status = httpResponse.getStatusLine().getStatusCode();

			if (status == HttpURLConnection.HTTP_OK) {

				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				InputStream in = httpResponse.getEntity().getContent();
				xpp.setInput(in, null);

				int eventType = xpp.getEventType();
				
				
				SimpleArrayMap<String, String> currentMap = null;  
				mapInArr = null;  
	            while (eventType != XmlPullParser.END_DOCUMENT) {  
	                switch (eventType) {  
	                case XmlPullParser.START_DOCUMENT:// 锟侥碉拷锟斤拷始锟铰硷拷,锟斤拷锟皆斤拷锟斤拷锟斤拷莩锟绞硷拷锟斤拷锟斤拷锟�  
	                	mapInArr = new ArrayList<SimpleArrayMap<String, String>>();// 实锟斤拷锟斤拷锟�  
	                    break;  
	                case XmlPullParser.START_TAG://锟斤拷始锟斤拷取某锟斤拷锟斤拷签  
	                    //通锟斤拷getName锟叫断讹拷锟斤拷锟侥革拷锟斤拷签锟斤拷然锟斤拷通锟斤拷nextText()锟斤拷取锟侥憋拷锟节碉拷值锟斤拷锟斤拷通锟斤拷getAttributeValue(i)锟斤拷取锟斤拷锟皆节碉拷值  
	                    String name = xpp.getName();  
	                    if (name.equalsIgnoreCase("record")) {  
	                    	currentMap = new SimpleArrayMap<String, String>();  
	                    	for(int i=0;i<xpp.getAttributeCount();i++){   
	                    		currentMap.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));  
	                        }   
	                    } else if (currentMap != null) { 
	                    	currentMap.put(name, xpp.nextText());// 锟斤拷锟斤拷锟斤拷锟斤拷Text元锟斤拷,锟斤拷锟斤拷锟斤拷锟斤拷锟街�    
	                    }  
	                    break;  
	                case XmlPullParser.END_TAG:// 锟斤拷锟斤拷元锟斤拷锟铰硷拷  
	                    //锟斤拷锟斤拷一锟斤拷Person锟斤拷锟斤拷锟皆斤拷锟斤拷锟斤拷拥锟斤拷锟斤拷锟斤拷锟斤拷锟�  
	                    if (xpp.getName().equalsIgnoreCase("record")&& currentMap != null) {  
	                    	mapInArr.add(currentMap);  
	                    	currentMap = null;  
	                    }  
	                    break;  
	                }  
	                eventType = xpp.next();  
	            }  
				in.close();
				return mapInArr;
			}else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
