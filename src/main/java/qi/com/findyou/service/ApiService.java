package qi.com.findyou.service;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import qi.com.findyou.base.AppConfig;
import qi.com.findyou.base.LocationApplication;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by qi_fu on 2017/12/5.
 */

public class ApiService {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    public static <S> S getApi(Class<S> serviceClass, String apiPath) {
        return createApi(serviceClass, AppConfig.BASE_URL + apiPath);
    }

    public static <S> S createApi(Class<S> serviceClass, String apiBaseUrl) {
        final String deviceId = LocationApplication.getInstance().getDeviceId();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());
                requestBuilder.header("imei", deviceId);
                Log.e("imei========",deviceId);
                Request request = requestBuilder.build();

                return chain.proceed(request);

            }
        });

        // add log
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        builder.baseUrl(apiBaseUrl);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();

        return retrofit.create(serviceClass);
    }
}

