package qi.com.findyou.view;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import qi.com.findyou.R;
import qi.com.findyou.adapter.PersonAdapter;
import qi.com.findyou.base.ApiBase;
import qi.com.findyou.base.ApiListener;
import qi.com.findyou.base.BaseFragment;
import qi.com.findyou.base.LocationApplication;
import qi.com.findyou.model.Person;
import qi.com.findyou.util.AlertUtil;
import qi.com.findyou.util.MapUtil;

/**
 * Created by qi_fu on 2017/11/15.
 */

public class MapFragment extends BaseFragment {

    private BaiduMap baiduMap;
    private LatLng ll;
    private MapView mMapView = null;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private EditText editText;
    private PersonAdapter adapter;
    private List<Person> searchList = new ArrayList<>();
    private ListView listView;
    private View view;
    private BitmapDescriptor bitmapDescriptor;
    private BitmapDescriptor mineDescriptor;
    private MapAsyncTask mapAsyncTask;
    private View allView;
    private ImageView imageBack;

    static MapFragment newInstance() {
        MapFragment f = new MapFragment();
        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SDKInitializer.initialize(getActivity().getApplication());
        view = inflater.inflate(R.layout.fragment_map, container, false);
        isPrepared = true;
        initView();
        setLocationOption();
        initListeren();
        return view;
    }


    private void initView() {
        imageBack = (ImageView) view.findViewById(R.id.come_back);
        editText = (EditText) view.findViewById(R.id.search_user);
        listView = (ListView) view.findViewById(R.id.list_search);
        mMapView = (MapView) view.findViewById(R.id.baidu_map);
        allView = this.view.findViewById(R.id.view);
        baiduMap = mMapView.getMap();
        // 设置可改变地图位置
        baiduMap.setMyLocationEnabled(true);
        mMapView.showZoomControls(false);//显示缩放按钮
        mLocClient = new LocationClient(getActivity());
        adapter = new PersonAdapter(searchList, getActivity());
        listView.setAdapter(adapter);
        bitmaps.add(BitmapDescriptorFactory.fromResource(R.mipmap.point_yellow));
        bitmaps.add(BitmapDescriptorFactory.fromResource(R.mipmap.point_red));

    }

    public View getAllView() {
        return allView;
    }

    private void initListeren() {
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            private Person person;

            @Override
            public boolean onMarkerClick(Marker result) {
                for (int i = 0; i < marks.size(); i++) {
                    if (marks.get(i).getName().equals(result.getTitle())) {
                        person = marks.get(i);
                        Intent intent = new Intent(getActivity(), PersonMsgActivity.class);
                        intent.putExtra("person", (Serializable) person);
                        startActivity(intent);
                        break;
                    }
                }
                return true;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchList.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeInput();
                Person person = searchList.get(position);
                LatLng location = new LatLng(person.getLatitude(), person.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(location);// 移动到某经纬度
                baiduMap.setMapStatus(status);//直接到中间
                baiduMap.animateMapStatus(status);
                searchList.clear();
                adapter.notifyDataSetChanged();
                editText.setText("");
//                status = MapStatusUpdateFactory.zoomBy(4f);
//                baiduMap.animateMapStatus(status);
                if (ll != null) {//显示位置点距离。
                    AlertUtil.showToastLong(getActivity(), MapUtil.getDistance(ll, location));
                }
            }
        });
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll != null) {
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                    // 移动到某经纬度
                    baiduMap.setMapStatus(status);//直接到中间
                    baiduMap.animateMapStatus(status);
                }
            }
        });
    }

    public void closeInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void searchList(String content) {
        if (content == null || content.equals("")) return;
        for (int i = 0; i < marks.size(); i++) {
            if (marks.get(i).getName().contains(content)) {
                searchList.add(marks.get(i));
            }
        }
        if (searchList.size() != 0) {
            adapter.notifyDataSetChanged();
        }
    }

    //设置地图相关参数
    private void setLocationOption() {
        LocationManager locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
            Toast.makeText(getContext(), "请打开GPS开关", Toast.LENGTH_SHORT).show();
        }
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        option.setServiceName("com.baidu.location.service_v2.9");
        option.setAddrType("all");
        option.setPriority(LocationClientOption.NetWorkFirst);//		option.disableCache(true);
        int span = 3 * 60 * 1000;//三分钟进行重现定位
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
//        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocClient.setLocOption(option);
        setUserVisibleHint(true);
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
        // 移动到某经纬度
        baiduMap.setMapStatus(status);//直接到中间
        baiduMap.animateMapStatus(status);
        status = MapStatusUpdateFactory.zoomBy(16f);
        baiduMap.animateMapStatus(status);
        baiduMap.getUiSettings().setCompassEnabled(false);//设置是否显示指南针
    }


    @Override
    protected void loadData() {
        if (!isPrepared || !isViable) {
            return;
        }
        //填充各控件的数据
        setMarksListData();
    }

    @Override
    public void refreshData() {
        baiduMap.clear();
        marks.clear();
        setMarksListData();
    }

    private List<Person> marks = new ArrayList<>();

    //设置标准点
    public void setMarksListData() {
        LocationApplication application = (LocationApplication) getActivity().getApplication();
        List<Person> personList = application.getPersonList();
        if (personList == null || personList.size() == 0) {
            return;
        }
        marks.addAll(personList);
        setMapMarks(marks);

    }

    ArrayList<BitmapDescriptor> bitmaps = new ArrayList<>();

    //用于标注定位点，附近的标志点。
    private void setMapMarks(List<Person> listla) {

        for (int i = 0; i < listla.size(); i++) {
            Person person = listla.get(i);
            if (person.getInfotype() == 'A') {
                if (person.getWarntype() == 1 || person.getWarntype() == 2) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.point_green);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(listla.get(i).getLatitude(), listla.get(i).getLongitude()))//设置位置
                            .icon(bitmapDescriptor)//设置覆盖物小图标
                            .title(listla.get(i).getName())
                            .draggable(false);//设置是否可以拖拽，默认为否
                    baiduMap.addOverlay(markerOptions);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(person.getLatitude(), person.getLongitude()))//设置位置
                            .icons(bitmaps)//设置覆盖物小图标
                            .title(person.getName())
                            .draggable(false);//设置是否可以拖拽，默认为否
                    if (baiduMap == null) {
                        return;
                    } else {
                        baiduMap.addOverlay(markerOptions);
                    }
                }
            }
        }
    }

    private CountDownTimer timer;


    private void setTimer(final Person person) {

//        if (timer == null) {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(person.getLatitude(), person.getLongitude()))//设置位置
                        .icons(bitmaps)//设置覆盖物小图标
                        .title(person.getName())
                        .draggable(false);//设置是否可以拖拽，默认为否
                if (baiduMap == null) {
                    return;
                } else {
                    baiduMap.addOverlay(markerOptions);
                }


                timer.start();
            }
        }.start();
//        } else timer.start();
    }


    /**
     * warning: this method is callback by activity manager; Be careful it's
     * lifecycle ： It is called after oncreate , before oncreateview; see
     * detail:
     * http://developer.android.com/reference/android/support/v4/app/Fragment
     * .html#setUserVisibleHint(boolean)
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {        //核心方法，避免因Fragment跳转导致地图崩溃
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser == true) {
            // if this view is visible to user, start to request user location
            startRequestLocation();
        } else if (isVisibleToUser == false) {
            // if this view is not visible to user, stop to request user
            // location
            stopRequestLocation();
        }
    }

    private void stopRequestLocation() {
        if (mLocClient != null) {
            mLocClient.unRegisterLocationListener(myListener);
            mLocClient.stop();
        }
    }

    long startTime;
    long costTime;

    private void startRequestLocation() {
        if (mLocClient != null) {
            mLocClient.registerLocationListener(myListener);
            mLocClient.start();
            mLocClient.requestLocation();
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.onDestroy();
        super.onDestroy();
    }

    private float mLastX;

    /**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mineDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.positioning);
            //配置定位图层显示方式，使用自己的定位图标
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mineDescriptor);
            baiduMap.setMyLocationConfigeration(configuration);
            //获取经纬度
            ll = new LatLng(location.getLatitude(), location.getLongitude());
            //将获取的location信息给百度map
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
// 此处设置开发者获取到的方向信息，顺时针0-360，mLastX就是获取到的方向传感器传来的x轴数值
                    .direction(mLastX)
                    .latitude(ll.latitude)
                    .longitude(ll.longitude)
                    .build();
            baiduMap.setMyLocationData(data);
//            if (isFirstLocation) {
            MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
            // 移动到某经纬度
            baiduMap.setMapStatus(status);//直接到中间
            baiduMap.animateMapStatus(status);
//            status = MapStatusUpdateFactory.zoomBy(4f);
//            baiduMap.animateMapStatus(status);

            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nNetworkLocationType : ");
            sb.append(location.getNetworkLocationType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            sb.append("\nindoor : ");
            sb.append(location.isIndoorLocMode());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.e("BaiduLocationApiDem", sb.toString());
//            }
//            isFirstLocation = false;
        }
    }
    //        //用来进行基站位置获取经纬度
//        mapAsyncTask=new MapAsyncTask(baiduMap,marks);
//        mapAsyncTask.executeOnExecutor((ExecutorService) Executors.newCachedThreadPool());//定义自增长的线程池
    private class MapAsyncTask extends AsyncTask<Void, Integer, Void> {
        private BaiduMap baiduMap;
        private List<Person> listla;

        public MapAsyncTask(BaiduMap baiduMap, List<Person> listla) {
            this.baiduMap = baiduMap;
            this.listla = listla;
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < listla.size(); i++) {
                if (listla.get(i).getInfotype() == 'c') {
                    final Person person = listla.get(i);
                    if (person.getInfotype() == 'V') {
                        String[] split = person.getLbs().split(",");
                        ApiBase.getCellLocation(Integer.valueOf(split[0]), Integer.valueOf(split[1]),
                                Integer.valueOf(split[2]), Integer.valueOf(split[3]), new ApiListener() {
                                    @Override
                                    public void onSuccess(JsonObject result) {
                                        int errcode = result.get("errcode").getAsInt();
                                        if (errcode == 0) {
                                            double lat = result.get("lat").getAsDouble();
                                            double lon = result.get("lon").getAsDouble();
                                            int radius = result.get("radius").getAsInt();
                                            String address = result.get("address").getAsString();

                                            if (person.getInfotype() == 1) {
                                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.point_green);
                                            } else if (person.getInfotype() == 2) {
                                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.point_yellow);
                                            } else {
                                                bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.point_red);
                                            }
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(new LatLng(lat, lon))//设置位置
                                                    .icon(bitmapDescriptor)//设置覆盖物小图标
                                                    .title(person.getName());
                                            baiduMap.addOverlay(markerOptions);
                                        }
                                    }

                                    @Override
                                    public void onComplete(JsonObject result) {

                                    }
                                });
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
