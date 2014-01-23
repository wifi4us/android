package com.paad.wifi4us.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * an utility class for build GET-URL with multiple parameters.
 * @author ys
 *
 */
public class UrlBuilder {
	
	public static final String SEG_PARA = "&";
	public static final String SEG_URL = "?";
	public static final String SEG_KV = "=";

	public static String buildLotteryHistoryUrl(String imei, String userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.LOTTERY_HISTORY_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, false);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, true);
		try {
			return URLEncoder.encode(sb.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String buildLotteryCreditUrl() {
		try {
			return URLEncoder.encode(Constant.Networks.CREDIT_PER_LOTTERY_HTTPURL,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String buildExchangeLotteryUrl(String imei, String userid,
			String phone, String idNum, String name, String alipayId,
			String type, String program, int notes) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.EXCHANGE_LOTTERY_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, false);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, false);
		appendOnePara(sb, Constant.HttpParas.PHONE, phone, false);
		appendOnePara(sb, Constant.HttpParas.ID_NUM, idNum, false);
		appendOnePara(sb, Constant.HttpParas.NAME, name, false);
		appendOnePara(sb, Constant.HttpParas.ALIPAY_ID, alipayId, false);
		appendOnePara(sb, Constant.HttpParas.TYPE, type, false);
		appendOnePara(sb, Constant.HttpParas.PROGRAM, program, false);
		appendOnePara(sb, Constant.HttpParas.NOTES, String.valueOf(notes), true);
		try {
			return URLEncoder.encode(sb.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void appendOnePara(StringBuilder sb, String key,
			String value, boolean isLast) {
		sb.append(key);
		sb.append(SEG_KV);
		sb.append(value);
		if (!isLast) {
			sb.append(SEG_PARA);
		}
	}
}
