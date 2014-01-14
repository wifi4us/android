package com.paad.wifi4us.utility;

import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class PasswdUtil {

	private static byte[] iv = {1,2,3,4,5,6,7,8};

	public static String encryptDES(String encryptString, String encryptKey) throws Exception{
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
  
        return byte2HexString(encryptedData);
	}

	public static String decryptDES(String decryptString, String decryptKey) throws Exception {
		 byte[] byteMi = String2Byte(decryptString);
         IvParameterSpec zeroIv = new IvParameterSpec(iv);
         SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
         Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
         cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
         byte decryptedData[] = cipher.doFinal(byteMi);
   
         return new String(decryptedData);
	}
       
	private static String byte2HexString(byte[] b){
		StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < b.length; i++) {
    		String stmp = Integer.toHexString(b[i]&0xff);
    		if(stmp.length() == 1)
    			sb.append("0"+stmp);
    		else
    			sb.append(stmp);
    	}
    	return sb.toString();
	}
        
    private static byte[] String2Byte(String hexString){
    	if(hexString.length() % 2 ==1)
    		return null;
    	byte[] ret = new byte[hexString.length()/2];
    	for (int i = 0; i < hexString.length(); i+=2) {
    		ret[i/2] = Integer.decode("0x"+hexString.substring(i,i+2)).byteValue();
    	}
    	return ret;
    }

    public static String getMD5Sign(String src) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(src.getBytes("UTF-8"));
        
        return byte2HexString(b);
    }
    
    public static String getRandomPasswd() {   
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";   
        Random random = new Random();   
        StringBuffer sb = new StringBuffer();   
        for (int i = 0; i < 7; i++) {   
            int number = random.nextInt(base.length());   
            sb.append(base.charAt(number));   
        }   
        return sb.toString();   
    }   
}
