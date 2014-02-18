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

	public static String buildAddCreditUrl(String imei,String userid,Integer type){
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.ADD_CREDIT_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, false);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, false);
		appendOnePara(sb, Constant.HttpParas.TYPE, type.toString(), true);
		return sb.toString();
	}
	
	public static String buildLotteryHistoryUrl(String imei, String userid) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.LOTTERY_HISTORY_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, false);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, true);
		
		return sb.toString();
	}

	public static String buildLotteryCreditUrl() {
		return Constant.Networks.CREDIT_PER_LOTTERY_HTTPURL;
	}
	
	public static String buildRegisterUrl(String imei) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.REGISTER_BASE_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, true);
		
		return sb.toString();
	}	
	
	public static String buildFetchAccountUrl(String userid, String imei) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.GETCREDIT_BASE_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, false);
		appendOnePara(sb, Constant.HttpParas.IMEI, imei, true);

		return sb.toString();
	}
	
	public static String buildGetAdidUrl(String userid, String make, String model, String resolution, String carrier, String androidversion) {
		StringBuilder sb = new StringBuilder();
		sb.append(Constant.Networks.AD_BASE_HTTPURL);
		sb.append(SEG_URL);
		appendOnePara(sb, Constant.HttpParas.USER_ID, userid, false);
		appendOnePara(sb, Constant.HttpParas.MAKE, make, false);
		appendOnePara(sb, Constant.HttpParas.MODEL, model, false);
		appendOnePara(sb, Constant.HttpParas.RESOLUTION, resolution, false);
		appendOnePara(sb, Constant.HttpParas.CARRIER, carrier, false);
		appendOnePara(sb, Constant.HttpParas.ANDROIDVER, androidversion, true);
		
		return sb.toString();
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
			
		return sb.toString();
	}

	private static void appendOnePara(StringBuilder sb, String key,
			String value, boolean isLast) {
		try {
			sb.append(URLEncoder.encode(key,"UTF-8"));
			sb.append(SEG_KV);
			sb.append(URLEncoder.encode(value,"UTF-8"));
			if (!isLast) {
				sb.append(SEG_PARA);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
