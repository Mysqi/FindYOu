package qi.com.findyou.base;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by k on 16/7/6.
 */
public class RequestParams extends HashMap<String, String> {
	public static RequestParams create() {
		return new RequestParams();
	}

	public String put(String key, Long value) {
		if (value == null) {
			return null;
		}
		return super.put(key, String.valueOf(value));
	}

	public String put(String key, Integer value) {
		if (value == null) {
			return null;
		}

		return super.put(key, String.valueOf(value));
	}

	public String put(String key, Short value) {
		if (value == null) {
			return null;
		}

		return super.put(key, String.valueOf(value));
	}

	public String put(String key, String value) {
		if (value == null) {
			return null;
		}

		return super.put(key, value);
	}

	public static RequestParams createWithAuth() throws UnsupportedEncodingException, NoSuchAlgorithmException {
		RequestParams rp = new RequestParams();
		String token = LocationApplication.getInstance().getDeviceId();
		long timestamp = System.currentTimeMillis();
		String signature = AccountManager.generateSignature(token, timestamp);

		rp.put("imei", token);
		rp.put("reporttime", String.valueOf(timestamp));
		rp.put("signature", signature);

		return rp;
	}

	public static MultipartBody.Part getFileParam(String paramName, String filePath) {
		File file = new File(filePath);

//		LogUtil.error("file:"+file.getName()+" size:"+file.length());
//
		// create RequestBody instance from file
		RequestBody requestFile =
				RequestBody.create(MediaType.parse("multipart/form-data"), file);

		// MultipartBody.Part is used to send also the actual file name
		MultipartBody.Part body =
				MultipartBody.Part.createFormData(paramName, file.getName(), requestFile);

		return body;
	}
}
