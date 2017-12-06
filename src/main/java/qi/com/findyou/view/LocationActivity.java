package qi.com.findyou.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import qi.com.findyou.R;

public class LocationActivity extends Activity {

    private final static String TAG = "LocationActivity";
    private TextView tv_location;
    private String mLocation = "";
    private LocationManager mLocationMgr;
    private Criteria mCriteria = new Criteria();
    private Handler mHandler = new Handler();
    private boolean bLocationEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initWidget();
        initLocation();
        mHandler.postDelayed(mRefresh, 100);

    }

    private void initWidget() {
        tv_location = (TextView) findViewById(R.id.tv_location);
        mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 设置定位精确度 Criteria.ACCURACY_COARSE 比较粗略， Criteria.ACCURACY_FINE 则比较精细
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否需要海拔信息 Altitude
        mCriteria.setAltitudeRequired(true);
        // 设置是否需要方位信息 Bearing
        mCriteria.setBearingRequired(true);
        // 设置是否允许运营商收费
        mCriteria.setCostAllowed(true);
        // 设置对电源的需求
        mCriteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    private void initLocation() {
        String bestProvider = mLocationMgr.getBestProvider(mCriteria, true);
        if (bestProvider == null) {
            bestProvider = LocationManager.NETWORK_PROVIDER;
        }
        if (mLocationMgr.isProviderEnabled(bestProvider)) {
            tv_location.setText("\n正在获取" + bestProvider + "定位对象");
            mLocation = String.format("\n定位类型=%s", bestProvider);
            beginLocation(bestProvider);
            bLocationEnable = true;
        } else {
            tv_location.setText("\n" + bestProvider + "定位不可用");
            bLocationEnable = false;
        }
    }

    @SuppressLint("DefaultLocale")
    private void setLocationText(Location location) {
        if (location != null) {
            String desc = String.format("%s\n定位对象信息如下：\n\t其中时间：%s\n\t其中经度：%f\n\t其中纬度：%f\n\t其中高度：%f",
                    mLocation, System.currentTimeMillis(),
                    location.getLongitude(), location.getLatitude(), location.getAltitude());
            Log.d(TAG, desc);
            tv_location.setText(desc);
        } else {
            tv_location.setText(mLocation + "\n暂未获取到定位对象");
        }
    }

    private void beginLocation(String method) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        Location location = mLocationMgr.getLastKnownLocation(method);
        setLocationText(location);
    }

    // 位置监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLocationText(location);
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
    };

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (bLocationEnable == false) {
                initLocation();
                mHandler.postDelayed(this, 1000);
            }
        }
    };

}
