package qi.com.findyou.service;

import java.util.List;

import qi.com.findyou.model.Person;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by qi_fu on 2017/5/5.
 */

public interface AppApi {
    String LOCATION = "/m_location/";
    String WARN = "/m_warn/";
    String HEAR = "/m_heartrate/";
    String MEMBER = "/m_member/";

    @POST("?")
    @FormUrlEncoded
    Observable<List<Person>> getLocation(@Field("startTime") String startime,@Field("endTime") String endtime);

    @POST("?")
    @FormUrlEncoded
    Observable<List<Person>> getWarn(@Field("startTime") String startime,@Field("endTime") String endtime);

    @POST("?")
    @FormUrlEncoded
    Observable<List<Person>> getHeart(@Field("startTime") String startime,@Field("endTime") String endtime);

    @POST("?")
    @FormUrlEncoded
    Observable<Person> getMember(@Field("name") String name,@Field("idCard") String endtime);

}