package qi.com.findyou.base;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ApiBase extends ApiHelper {
    public static void getAllLocation(String imei, final ApiListener apiListener) {
        String url = String.format(AppConfig.GET_LOCATION);
        Map<String, Object> params = new HashMap<>();
        params.put("imei", imei);
        ApiBase.get(url, params, new ApiListener() {
            @Override
            public void onSuccess(JsonObject result) {

                if (apiListener != null) {
                    apiListener.onSuccess(result);
                }
            }

            @Override
            public void onComplete(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onComplete(result);
                }
            }
        });
    }

    public static void getWarn(String imei, final ApiListener apiListener) {
        String url = String.format(AppConfig.GET_WARN);
        Map<String, Object> params = new HashMap<>();
        params.put("imei", imei);
        ApiBase.get(url, params, new ApiListener() {
            @Override
            public void onSuccess(JsonObject result) {

                if (apiListener != null) {
                    apiListener.onSuccess(result);
                }
            }

            @Override
            public void onComplete(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onComplete(result);
                }
            }
        });
    }

    public static void getHeartRate(String imei, final ApiListener apiListener) {
        String url = String.format(AppConfig.GET_HEART_RATE);
        Map<String, Object> params = new HashMap<>();
        params.put("imei", imei);
        ApiBase.get(url, params, new ApiListener() {
            @Override
            public void onSuccess(JsonObject result) {


                if (apiListener != null) {
                    apiListener.onSuccess(result);
                }
            }

            @Override
            public void onComplete(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onComplete(result);
                }
            }
        });
    }
    public static void getMember(String imei, final ApiListener apiListener) {
        String url = String.format(AppConfig.GET_MEMBER);
        Map<String, Object> params = new HashMap<>();
        params.put("imei", imei);
        ApiBase.get(url, params, new ApiListener() {
            @Override
            public void onSuccess(JsonObject result) {


                if (apiListener != null) {
                    apiListener.onSuccess(result);
                }
            }

            @Override
            public void onComplete(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onComplete(result);
                }
            }
        });
    }

    /*
    *
    * mcc	 int	  是 	mcc国家代码：中国代码 460
      mnc	 int	是	mnc网络类型：0移动，1联通，2电信对应sid，十进制
      lac	 int	是	lac(电信对应nid)，十进制
      ci	  int	是	cellid(电信对应bid)，十进制
      coord	  string	否	坐标类型(wgs84/gcj02/bd09)，默认wgs84
      output	string	否	返回格式(csv/json/xml)，默认csv
    * */
    public static void getCellLocation(int mcc, int mnc, int lac, int ci, final ApiListener apiListener) {
        String url = String.format(AppConfig.GET_HEART_RATE);
        Map<String, Object> params = new HashMap<>();
        params.put("mcc", mcc);
        if(mnc  == 2){//电信
            params.put("sid", mnc);
            params.put("nid", lac);
            params.put("bid", ci);
        }else {//移动、联通
            params.put("mnc", mnc);
            params.put("lac", lac);
            params.put("ci", ci);
        }
        params.put("coord", "bd09");
        params.put("output", "json");
        ApiBase.get(url, params, new ApiListener() {
            @Override
            public void onSuccess(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onSuccess(result);
                }
            }

            @Override
            public void onComplete(JsonObject result) {
                if (apiListener != null) {
                    apiListener.onComplete(result);
                }
            }
        });
    }
}
