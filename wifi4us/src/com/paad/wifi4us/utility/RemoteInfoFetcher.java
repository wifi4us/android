package com.paad.wifi4us.utility;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.data.AdContent;
import com.paad.wifi4us.utility.data.LotteryHistory;

public class RemoteInfoFetcher {
	public static List<LotteryHistory> fetchLotteryHistories(String imei, String userid){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryHistoryUrl(imei, userid));
		if(mapInArr == null){
			return null;
		}
		List<LotteryHistory> histories = new ArrayList<LotteryHistory>();
		for(SimpleArrayMap<String, String> map:mapInArr){
			
			LotteryHistory history = LotteryHistory.buildHistoryFromMap(map);
			histories.add(history);
		}
		return histories;
	}
	
	public static Integer fetchLotteryCredit(){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryCreditUrl());
		if(mapInArr == null){
			return null;
		}
		String strCredit = mapInArr.get(0).get(Constant.XmlResultKey.CREDIT);
		return Integer.valueOf(strCredit);
	}
	
	public static Integer buyTicket(String imei, String userid,
			String phone, String idNum, String name, String alipayId,
			String type, String program, int notes){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildExchangeLotteryUrl(imei, userid, phone, idNum, name, alipayId, type, program, notes));
		if(mapInArr == null){
			return null;
		}
		String state = mapInArr.get(0).get(Constant.XmlResultKey.STATE);
		return Integer.valueOf(state);
	}
	
	public static String resgisterUserId(String imei){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildRegisterUrl(imei));
		if(mapInArr == null){
			return null;
		}
		return mapInArr.get(0).get(Constant.XmlResultKey.USER_ID);
	}

	public static String fetchAccount(String userid, String imei){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildFetchAccountUrl(userid, imei));
		if(mapInArr == null){
			return null;
		}
		return mapInArr.get(0).get(Constant.XmlResultKey.ACCOUNT);
	}
	
	public static ArrayList<AdContent> fetchAdList(String userid, String make, String model, String resolution, String carrier, String androidversion){
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildGetAdidUrl(userid, make, model, resolution, carrier, androidversion));
		if(mapInArr == null){
			return null;
		}
		
		ArrayList<AdContent> adList = new ArrayList<AdContent>();
		for(SimpleArrayMap<String, String> map:mapInArr){
			AdContent adContent = AdContent.buildAdFromMap(map);
			adList.add(adContent);
		}
		return adList;
	}
}


