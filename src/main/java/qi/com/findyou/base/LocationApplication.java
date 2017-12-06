package qi.com.findyou.base;


import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import com.baidu.mapapi.SDKInitializer;

import java.util.List;

import qi.com.findyou.model.Person;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 * 
 * 直接拷贝com.baidu.location.service包到自己的工程下，简单配置即可获取定位结果，也可以根据demo内容自行封装
 */
public class LocationApplication extends Application {
//	public LocationService locationService;
    public Vibrator mVibrator;
    public static Context applicationContext;
    public static LocationApplication instance;
    private List<Person> personList;

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        instance = this;
        applicationContext = this;
//        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

    }

    public static  LocationApplication getInstance(){
        return instance;
    }

    public static Context getAppContext(){
        return  applicationContext;
    }
    public static String getDeviceId(){
        TelephonyManager telephonyManager = (TelephonyManager) getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId != null){
            return deviceId;
        }else {
            return "";
        }
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }
    public void addPersonList(Person person){
        boolean contains = personList.contains(person);
        if(!contains){
            personList.add(person);
        }
    }
    public void deletePersonList(Person person){
        if(this.personList.size() == 0){
            return;
        }
        boolean contains = personList.contains(person);
        if(!contains){
            personList.remove(person);
        }
    }
}
