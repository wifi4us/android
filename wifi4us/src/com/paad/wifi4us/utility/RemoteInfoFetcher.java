package com.paad.wifi4us.utility;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.data.FetchedInfo;
import com.paad.wifi4us.utility.data.LotteryHistory;

public class RemoteInfoFetcher {
	public static List<LotteryHistory> fetchLotteryHistories(String imei, String userid){
		FetchedInfo fetchedInfo = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryHistoryUrl(imei, userid));
		if(fetchedInfo == null){
			return null;
		}
		List<LotteryHistory> histories = new ArrayList<LotteryHistory>();
		for(SimpleArrayMap<String, String> map:fetchedInfo){
			
			LotteryHistory history = LotteryHistory.buildHistoryFromMap(map);
			histories.add(history);
		}
		return histories;
	}
	
	public static Integer fetchLotteryCredit(){
		FetchedInfo fetchedInfo = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryCreditUrl());
		if(fetchedInfo == null){
			return null;
		}
		String strCredit = fetchedInfo.get(0).get(Constant.XmlResultKey.CREDIT);
		return Integer.valueOf(strCredit);
	}
	
	public static Integer buyTicket(String imei, String userid,
			String phone, String idNum, String name, String alipayId,
			String type, String program, int notes){
		FetchedInfo fetchedInfo = HttpXmlParser.getResultFromURL(UrlBuilder.buildExchangeLotteryUrl(imei, userid, phone, idNum, name, alipayId, type, program, notes));
		if(fetchedInfo == null){
			return null;
		}
		String state = fetchedInfo.get(0).get(Constant.XmlResultKey.STATE);
		return Integer.valueOf(state);
	}
}


