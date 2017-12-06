package qi.com.findyou.view;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import qi.com.findyou.R;


/**
 * 功能描述：通过手机信号获取基站信息
 * <p>
 * # 通过TelephonyManager 获取lac:mcc:mnc:cell-id
 * <p>
 * # MCC，Mobile Country Code，移动国家代码（中国的为460）；
 * <p>
 * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
 * <p>
 * # LAC，Location Area Code，位置区域码；
 * <p>
 * # CID，Cell Identity，基站编号；
 * <p>
 * # BSSS，Base station signal strength，基站信号强度。
 *
 * @author android_ls
 */

public class GSMCellLocationActivity extends Activity {


    private static final String TAG = "GSMCellLocationActivity";
    private static final int REQUEST_PHONE_STATE = 101;

    /*信号相关*/

    private static final int NETWORKTYPE_WIFI = 0;
    private static final int NETWORKTYPE_4G = 1;
    private static final int NETWORKTYPE_2G = 2;
    private static final int NETWORKTYPE_NONE = 3;
    public TextView mTextView;
    public TextView mTextView2;
    public TelephonyManager mTelephonyManager;
    public PhoneStatListener mListener;
    public int mGsmSignalStrength;
    private NetWorkBroadCastReciver mNetWorkBroadCastReciver;

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gsmcell_location);
        getPhoneMsg();


        // 获取基站信息

        findViewById(R.id.button_begain).setOnClickListener(new View.OnClickListener() {


            @Override

            public void onClick(View v) {

                // 中国移动和中国联通获取LAC、CID的方式
                //Android6.0需要动态获取权限
                if (ActivityCompat.checkSelfPermission(GSMCellLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            toast("需要动态获取权限");
                    ActivityCompat.requestPermissions(GSMCellLocationActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQUEST_PHONE_STATE);
                } else {
                    show();
                }

            }

        });

        getGsmData();

    }

    private void show() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int type = mTelephonyManager.getNetworkType();//获取网络类型
        //在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO

        // 返回值MCC + MNC

        String operator = mTelephonyManager.getNetworkOperator();

        int mcc = Integer.parseInt(operator.substring(0, 3));

        int mnc = Integer.parseInt(operator.substring(3));

        int lac = 0;
        int cellId = 0;
        if (type == TelephonyManager.NETWORK_TYPE_CDMA || type == TelephonyManager.NETWORK_TYPE_EVDO_A)
            //电信2G是CDMA // 中国电信获取LAC、CID的方式
        {
            CdmaCellLocation location = (CdmaCellLocation) mTelephonyManager.getCellLocation();
            lac = location.getNetworkId();
            cellId = location.getBaseStationId();

        } else {//移动、联通
            GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            lac = location.getLac();
            cellId = location.getCid();
        }


        Log.i(TAG, " MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);

        // 获取邻区基站信息

        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();

        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");

        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环

            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC

            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID

            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度

        }

        Log.i(TAG, " 获取邻区基站信息:" + sb.toString());
        mTextView2.setText(" MCC = " + mcc + "\n MNC = " + mnc + "\n LAC = " + lac + "\n CID = " + cellId + "\n  获取邻区基站信息:" + sb.toString());

    }

    private void getGsmData() {
        mTextView = (TextView) findViewById(R.id.textview);
        mTextView2 = (TextView) findViewById(R.id.textview2);
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        /**由于信号值变化不大时，监听反应不灵敏，所以通过广播的方式同时监听wifi和信号改变更灵敏*/
        mNetWorkBroadCastReciver = new NetWorkBroadCastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mNetWorkBroadCastReciver, intentFilter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //用户不在当前页面时，停止监听
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_NONE);
    }

    private class PhoneStatListener extends PhoneStateListener {
        //获取信号强度

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
//            int level = signalStrength.getLevel();
            mGsmSignalStrength = signalStrength.getGsmSignalStrength();
            //网络信号改变时，获取网络信息
            getNetWorkInfo();
        }
    }

    /**
     * 暂时不用这个方法
     */
    public int getNetWorkType(Context context) {
        int mNetWorkType = -1;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return isFastMobileNetwork() ? NETWORKTYPE_4G : NETWORKTYPE_2G;
            }
        } else {
            mNetWorkType = NETWORKTYPE_NONE;//没有网络
        }
        return mNetWorkType;
    }

    /**
     * 判断网络速度
     */
    private boolean isFastMobileNetwork() {
        if (mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
            return true;
        }
        return false;
    }

    //接收网络状态改变的广播
    class NetWorkBroadCastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNetWorkInfo();
        }
    }

    /**
     * 获取网络的信息
     */
    private void getNetWorkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //wifi
                    WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int rssi = connectionInfo.getRssi();
                    mTextView.setText("当前为wifi网络，信号强度=" + rssi);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //移动网络,可以通过TelephonyManager来获取具体细化的网络类型
                    String netWorkStatus = isFastMobileNetwork() ? "4G网络" : "2G网络";
                    mTextView.setText("当前为" + netWorkStatus + "，信号强度=" + mGsmSignalStrength);
                    break;
            }
        } else {
            mTextView.setText("没有可用网络");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetWorkBroadCastReciver);
    }


    public void getPhoneMsg() {
        Intent batteryInfoIntent = getApplicationContext()
                .registerReceiver(null,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryInfoIntent.getIntExtra("level", 0);//电量（0-100）
        int status = batteryInfoIntent.getIntExtra("status", 0);
        int health = batteryInfoIntent.getIntExtra("health", 1);
        boolean present = batteryInfoIntent.getBooleanExtra("present", false);
        int scale = batteryInfoIntent.getIntExtra("scale", 0);
        int plugged = batteryInfoIntent.getIntExtra("plugged", 0);//
        int voltage = batteryInfoIntent.getIntExtra("voltage", 0);//电压
        int temperature = batteryInfoIntent.getIntExtra("temperature", 0); // 温度的单位是10℃
        String technology = batteryInfoIntent.getStringExtra("technology");

        Log.i(TAG, "--------getPhoneMsg: " + level);
    }

    /**
     * 加个获取权限的监听
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            show();
        }
    }

}
