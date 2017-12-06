package qi.com.findyou.base;

import com.google.gson.JsonObject;

/**
 * Created by k on 2017/3/18.
 */
public interface ApiListener {
    void onSuccess(JsonObject result);
    void onComplete(JsonObject result);
}
