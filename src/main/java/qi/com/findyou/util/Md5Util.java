package qi.com.findyou.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Md5Util {
	public static String encrypt(String str, String salt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(salt)) {
			return null;
		}
		str = str + salt;
		
		return encrypt(str);
	}
	
	public static String encrypt(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if (TextUtils.isEmpty(str)) {
			return null;
		}

		MessageDigest mdEnc = MessageDigest.getInstance("MD5"); 
		mdEnc.update(str.getBytes(), 0, str.length());
		String resultStr = new BigInteger(1, mdEnc.digest()).toString(32);
		
		return resultStr;
	} 
	
	public static String createSalt() {
    	SecureRandom random = new SecureRandom();
	    String str = new BigInteger(100, random).toString(32);
        
	    return str;
    }
    
    public static boolean validate(String str, String salt, String md5) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	if (TextUtils.isEmpty(str) || TextUtils.isEmpty(salt) || TextUtils.isEmpty(md5)) {
			return false;
		}
    	
    	String encryptStr = encrypt(str, salt);
    	
    	return encryptStr.equals(md5);
    }
    
    public static boolean validate(String str, String md5) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	if (TextUtils.isEmpty(str) || TextUtils.isEmpty(md5)) {
			return false;
		}
    	
    	String encryptStr = encrypt(str);

		return encryptStr.equals(md5);
    }
}
