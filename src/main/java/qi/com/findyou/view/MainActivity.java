package qi.com.findyou.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.base.BaseFragment;
import qi.com.findyou.base.LocationApplication;
import qi.com.findyou.model.Person;
import qi.com.findyou.service.AppPresenter;

public class MainActivity extends FragmentActivity implements Runnable {

    private static final int REQUEST_PHONE_STATE = 101;

    private Fragment[] fragments;
    private FragmentManager fm;

    private int index = 0;
    private int currentTabIndex = 0;
    private View lastSelectedTab = null;

    private MapFragment mapFragment = new MapFragment();
    private ListDataFragment listFragment = new ListDataFragment();
    private DataFragment dataFragment = new DataFragment();
    private UserFragment userFragment = new UserFragment();
    private View mapView;
    private LocationApplication application;
    private Thread thread;
    private AppPresenter appPresenter;

    private boolean isFirst = true;
    private List<Person> marks = new ArrayList<>();

    private long lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.tab_conversation);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE
            }, REQUEST_PHONE_STATE);
        } else {
            initView();
        }
    }


    private void initView() {
        setDeviceId();
        application = LocationApplication.getInstance();
        appPresenter = new AppPresenter(this);
        appPresenter.getLocation("", "");
        initBeepSound();
        fm = getSupportFragmentManager();
        fragments = new Fragment[]{mapFragment, listFragment, dataFragment, userFragment};

        // 添加显示第一个fragment
        fm.beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .show(mapFragment)
                .commit();
        lastSelectedTab = mapView;
        //设置默认进入的fragment
        mapView.setSelected(true);
        thread = new Thread(this);
        thread.start();
    }

    public void onTabClicked(View view) {
        if (view.isSelected()) {
            if (currentTabIndex == 0) {
                mapFragment.loadData();
            }
            return;
        }
        if (lastSelectedTab != null) {
            lastSelectedTab.setSelected(false);
        }
        view.setSelected(true);
        lastSelectedTab = view;
        if (view.getId() != R.id.tab_conversation) {
            hideKeyboard();
        }
        switch (view.getId()) {
            case R.id.tab_conversation:
                index = 0;
                mapFragment.setUserVisibleHint(true);
                break;
            case R.id.tab_backlog:
                index = 1;
                listFragment.setUserVisibleHint(true);
                break;
            case R.id.tab_schedule:
                index = 2;
                dataFragment.setUserVisibleHint(true);
                break;
            case R.id.tab_setting:
                index = 3;
                userFragment.setUserVisibleHint(true);
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = fm.beginTransaction();
            trx.hide(fragments[currentTabIndex]);

            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

    public Fragment getVisibleFragment() {
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //连续按两次退出
    @Override
    public void onBackPressed() {
        if (isFirst) {
            Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT).show();
            lastTime = System.currentTimeMillis();
            isFirst = false;
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime <= 2000) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出！", Toast.LENGTH_SHORT).show();
                lastTime = System.currentTimeMillis();
            }
        }
    }

    public void setDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
//        LocationApplication.getInstance().setDeviceId(deviceId);
        LocationApplication.getInstance().setDeviceId("353537063938918");//测试
        Log.e("token", deviceId);
    }

    /**
     * 加个获取权限的监听
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHONE_STATE && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initView();
        } else {
            finish();
        }
    }

    public void getNetData(List<Person> list) {
        Log.e("locaton",list.toString());
        marks.clear();
        marks.addAll(list);
        application.setPersonList(marks);
        BaseFragment fragment = (BaseFragment) getVisibleFragment();
        if (fragment != null) {
            fragment.refreshData();
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getWarntype() > 2) {
                playBeepSoundAndVibrate();
            }
        }
    }
    /*====位置信息轮训=====*/
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(20000);
//                Thread.sleep(3*60*1000);
                handler.sendMessage(handler.obtainMessage());
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            appPresenter.getLocation("", "");
            Log.e("time---", "hahhahah---");
        }
    };
    public Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            appPresenter.getLocation("", "");
            Log.e("time---", "hahhahah---");
            Person info2 = new Person();
            //latitude: 22.539007, longitude: 113.942499深圳大学
            info2.setName("深圳大学");
            info2.setLatitude(22.539007);
            info2.setLongitude(113.942499);
            info2.setId("2222222");
            info2.setInfotype('A');
            info2.setWarntype(3);
            BaseFragment fragment = (BaseFragment) getVisibleFragment();
            if (fragment != null) {
                application.addPersonList(info2);
                playBeepSoundAndVibrate();
                fragment.refreshData();
            }
        }
    };

    /*======声音和震动=====*/
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.40f;
    private boolean vibrate;

    private void initBeepSound() {
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.deep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
        vibrate = true;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        final long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
        vibrator.vibrate(pattern, -1);
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    /*======测试数据=====*/
    public void getNetData() {
        setMarksListData();
        application.setPersonList(marks);
    }

    public void setMarksListData() {
        Person info = new Person();
        //latitude: 22.543864, longitude: 113.950332深大地铁
        info.setId("11111");
        info.setName("深大地铁站");
        info.setLatitude(22.543864);
        info.setLongitude(113.950332);
        info.setInfotype('A');
        info.setWarntype(1);
        Person info2 = new Person();
        //latitude: 22.539007, longitude: 113.942499深圳大学
        info2.setName("深圳大学");
        info2.setLatitude(22.539007);
        info2.setLongitude(113.942499);
        info2.setId("2222222");
        info2.setInfotype('A');
        info2.setWarntype(2);
        Person info3 = new Person();
        //latitude: 22.546033, longitude: 113.96042 南山区---高新园-地铁站
        info3.setName("高新园-地铁站");
        info3.setLatitude(22.546033);
        info3.setLongitude(113.96042);
        info3.setId("333333");
        info3.setWarntype(3);
        info3.setInfotype('A');
        Person info4 = new Person();
        //茶光 longitude：113.960831,latitude：22.580087
        info4.setName("茶光-地铁站");
        info4.setLatitude(22.580087);
        info4.setLongitude(113.960831);
        info4.setId("44444");
        info4.setWarntype(2);
        info4.setInfotype('A');
        Person info5 = new Person();
        //大学城 longitude：113.972248,latitude：22.588385 113.972248,22.588385
        info5.setName("大学城-地铁站");
        info5.setLatitude(22.588385);
        info5.setLongitude(113.972248);
        info5.setId("55555");
        info5.setWarntype(1);
        info5.setInfotype('A');
        Person info6 = new Person();
        //西丽 longitude：113.972248,latitude：22.588385 113.960285,22.586456
        info6.setName("西丽-地铁站");
        info6.setLatitude(22.586456);
        info6.setLongitude(113.960285);
        info6.setId("55555");
        info6.setWarntype(3);
        info6.setInfotype('A');
        marks.add(info);
        marks.add(info6);
        marks.add(info2);
        marks.add(info3);
        marks.add(info4);
        marks.add(info5);
        thread = new Thread(this);
        thread.start();
    }

}
