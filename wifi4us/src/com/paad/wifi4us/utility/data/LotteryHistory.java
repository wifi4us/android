package com.paad.wifi4us.utility.data;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.Constant;

public class LotteryHistory {
	public String periodNumber;
	public String program;
	public String ticketId;
	public String bonus;
	public int state = -1;
	public String tradeId;
	
	public LotteryHistory(){};
	
	
	/**
	 * build a history of lottery from the SimpleArrayMap which is often a parsed root element of an xml file.
	 * @param map
	 * @return
	 */
	public static LotteryHistory buildHistoryFromMap(SimpleArrayMap<String, String> map){
		LotteryHistory history = new LotteryHistory();
		history.periodNumber = map.get(Constant.XmlResultKey.PERIOD_NUMBER);
		history.program = map.get(Constant.XmlResultKey.PROGRAM);
		history.bonus = map.get(Constant.XmlResultKey.BONUS);
		history.ticketId = map.get(Constant.XmlResultKey.TICKET_ID);
		history.tradeId = map.get(Constant.XmlResultKey.TRADE_ID);
		String strState = map.get(Constant.XmlResultKey.STATE);
		if(strState != null) {
			history.state = Integer.valueOf(strState);
		}
		return history;
	}
}
