package com.paad.wifi4us.utility.data;

import android.support.v4.util.SimpleArrayMap;

import com.paad.wifi4us.utility.Constant;

public class LotteryHistory {
	public String periodNumber;
	public String program;
	public String ticketId;
	public int state = -1;
	public String tradeId;
	public LotteryHistory(String periodNumber, String program, String ticketId,String tradeId, int state){
		this.periodNumber = periodNumber;
		this.program = program;
		this.ticketId = ticketId;
		this.state = state;
		this.tradeId = tradeId;
	}
	
	private LotteryHistory(){};
	
	
	/**
	 * build a history of lottery from the SimpleArrayMap which is often a parsed root element of an xml file.
	 * @param map
	 * @return
	 */
	public static LotteryHistory buildHistoryFromMap(SimpleArrayMap<String, String> map){
		LotteryHistory history = new LotteryHistory();
		history.periodNumber = map.get(Constant.XmlResultKey.PERIOD_NUMBER);
		history.program = map.get(Constant.XmlResultKey.PROGRAM);
		history.ticketId = map.get(Constant.XmlResultKey.TICKET_ID);
		history.tradeId = map.get(Constant.XmlResultKey.TRADE_ID);
		String strState = map.get(Constant.XmlResultKey.STATE);
		if(strState != null) {
			history.state = Integer.valueOf(strState);
		}
		return history;
	}
}
