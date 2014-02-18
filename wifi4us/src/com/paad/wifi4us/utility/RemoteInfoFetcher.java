package com.paad.wifi4us.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import com.paad.wifi4us.utility.data.AdContent;
import com.paad.wifi4us.utility.data.LotteryHistory;

public class RemoteInfoFetcher {
	
	private static boolean debug = false;
	public static List<LotteryHistory> fetchLotteryHistories(String imei, String userid){
		if(debug){
		    try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			List<LotteryHistory> histories = new ArrayList<LotteryHistory>();
			LotteryHistory history = new LotteryHistory();
			history.periodNumber = "20131201";
			history.ticketId = "ticketid_123";
			history.tradeId = "tradeid_123";
			history.program = "01,02,03,05,22,25,29,31|21,30";
			history.state = 0;
			histories.add(history);
			history = new LotteryHistory();
			history.periodNumber = "20131222";
			history.ticketId = "ticketid_333";
			history.tradeId = "tradeid_333";
			history.program = "20,22,23,25,27,28,24,31|07";
			history.state = 2;
			histories.add(history);
			return histories;
		}
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryHistoryUrl(imei, userid));
		if(mapInArr == null){
			return null;
		}
		List<LotteryHistory> histories = new ArrayList<LotteryHistory>();
		for(SimpleArrayMap<String, String> map:mapInArr){
			
			LotteryHistory history = LotteryHistory.buildHistoryFromMap(map);
			histories.add(history);
		}
		Collections.sort(histories, new Comparator<LotteryHistory>() {

            @Override
            public int compare(LotteryHistory lhs, LotteryHistory rhs) {
                return rhs.tradeId.compareTo(lhs.tradeId);
            }
		    
		});
		return histories;
	}
	
	public static String addUserCredit(String imei,String userid,Integer type){
		if(debug){
			return "9527";
		}
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildAddCreditUrl(imei, userid, type));
		if(mapInArr == null){
		    System.out.println("null mapInArr");
			return null;
		}
		return mapInArr.get(0).get(Constant.XmlResultKey.ACCOUNT);
	}
	
	public static Integer fetchLotteryCredit(){
		if(debug){
			return 5;
		}
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildLotteryCreditUrl());
		if(mapInArr == null){
			return null;
		}
		String strCredit = mapInArr.get(0).get(Constant.XmlResultKey.CREDIT);
		return Integer.valueOf(strCredit);
	}
	
	public static int[] buyTicket(String imei, String userid,
			String phone, String idNum, String name, String alipayId,
			String type, String program, int notes){
		if(debug){
		    try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
			return new int[]{0,9527};
		}
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildExchangeLotteryUrl(imei, userid, phone, idNum, name, alipayId, type, program, notes));
		if(mapInArr == null){
		    Log.d("buyTicket", "null result");
			return null;
		}
		String state = mapInArr.get(0).get(Constant.XmlResultKey.STATE);
		String credit = mapInArr.get(0).get(Constant.XmlResultKey.ACCOUNT);
		Log.d("remoteInfoFetch", state);
		return new int[]{Integer.valueOf(state),Integer.valueOf(credit)};
	}
	
	public static String resgisterUserId(String imei){
		if(debug){
			return "2468";
		}
		ArrayList<SimpleArrayMap<String, String>> mapInArr = HttpXmlParser.getResultFromURL(UrlBuilder.buildRegisterUrl(imei));
		if(mapInArr == null){
			return null;
		}
		return mapInArr.get(0).get(Constant.XmlResultKey.USER_ID);
	}

	public static String fetchAccount(String userid, String imei){
		if(debug){
			return "9527";
		}
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


