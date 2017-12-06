package qi.com.findyou.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import qi.com.findyou.util.AlertUtil;

public class ApiHelper {
    protected static Context context = LocationApplication.getInstance().getAppContext();
    private static int TIMEOUT = 10000000;

    protected static String METHOD_GET = "GET";
    protected static String METHOD_POST = "POST";


    public static void get(String url,Map<String, Object> params, final ApiListener apiListener) {
        request(url, METHOD_GET, params, apiListener);
    }
    public static void post(String url, Map<String, Object> params, final ApiListener apiListener) {
        request(url, METHOD_POST, params, apiListener);
    }

    protected static void request(String url, String method, Map<String, Object> params, final ApiListener apiListener) {
        Builders.Any.B ion = Ion.with(context)
                .load(method, url)
                .setTimeout(TIMEOUT);

        if (params != null) {
            Map<String, List<String>> paramsResult = new HashMap<String, List<String>>();

            Set<String> keys = params.keySet();
            for (String key : keys) {
                List<String> value = new ArrayList<String>();
                value.add(String.valueOf(params.get(key)));
                paramsResult.put(key, value);
            }

            ion.setBodyParameters(paramsResult);
        }

        ion.asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    apiListener.onComplete(result);

                    if (e!=null) {
                        Log.e("Request Exception", e.getMessage());
                        AlertUtil.showToastShort(context, "请求异常，请重试");
                        return;
                    }

                    if (result == null) {
                        Log.e("Request Exception", "result == null  "+e.getMessage());
                        AlertUtil.showToastShort(context, "请求失败，请重试");
                        return;
                    }
                    apiListener.onSuccess(result);
                }
            });
    }

    public static void requestUnauth(String url, final ApiListener apiListener) {
        Ion.with(context)
                .load(url)
                .setTimeout(TIMEOUT)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        apiListener.onComplete(result);

                        if (result == null) {
                            AlertUtil.showToastShort(context, "服务器错误");
                            return;
                        }

                        Boolean success = result.get("success")==null ? null : result.get("success").getAsBoolean();
                        if (success!=null && !success) {
                            AlertUtil.showToastLong(context, result.get("msg").getAsString());
                        }

                        apiListener.onSuccess(result);
                    }
                });
    }

    public static void requestUncheck(String url, final ApiListener apiListener) {
        Ion.with(context)
                .load(url)
                .setTimeout(TIMEOUT)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e!=null) {
                            Log.e("Request Exception", e.getMessage());
                            AlertUtil.showToastShort(context, "请求异常，请重试");
                            return;
                        }
                        apiListener.onSuccess(result);
                        apiListener.onComplete(result);
                    }
                });
    }
}
