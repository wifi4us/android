package com.paad.wifi4us.utility.data;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.Constant;

public class AdContent {
	public String adid;
	public String url;
	public String length;
	public String adtext;
	public String adword;
	
	/**
	 * build a history of lottery from the SimpleArrayMap which is often a parsed root element of an xml file.
	 * @param map
	 * @return
	 */
	public AdContent(){};

	public static AdContent buildAdFromMap(SimpleArrayMap<String, String> map){
		AdContent adcontent = new AdContent();
		adcontent.adid = map.get(Constant.XmlResultKey.AD_ID);
		adcontent.url = map.get(Constant.XmlResultKey.AD_URL);
		adcontent.length = map.get(Constant.XmlResultKey.AD_LENGTH);
		adcontent.adword = map.get(Constant.XmlResultKey.AD_WORD);
		adcontent.adtext = map.get(Constant.XmlResultKey.AD_TEXT);
		
		return adcontent;
	}
}
