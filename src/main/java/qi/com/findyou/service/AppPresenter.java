package qi.com.findyou.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import qi.com.findyou.model.Person;
import qi.com.findyou.util.AlertUtil;
import qi.com.findyou.view.MainActivity;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by steve on 2017/4/23.
 */

public class AppPresenter {

    private  MainActivity activity;
    private  AppApi appApi;


    public AppPresenter(MainActivity activity) {
        super();
        this.activity = activity;
    }
    public void getLocation(String starTime,String endTime) {
        this.appApi = ApiService.getApi(AppApi.class, AppApi.LOCATION);
        appApi.getLocation(starTime,endTime)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(null,r, "LargeStackThread", 2000000L);
                    }
                })))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Person>>() {
                    @Override
                    public void onCompleted() {
//	                    AlertUtil.showToastLong(activity, "complete");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        AlertUtil.showToastLong(activity, "请检查网络设置！");
                    }

                    @Override
                    public void onNext(List<Person> response) {
                        activity.getNetData(response);
                    }
                });
    }
    public void getWarn(String starTime,String endTime) {
        this.appApi = ApiService.getApi(AppApi.class, AppApi.WARN);
        appApi.getWarn(starTime,endTime)
                .subscribeOn(Schedulers.from(Executors.newFixedThreadPool(1, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(null,r, "LargeStackThread", 2000000L);
                    }
                })))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Person>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        AlertUtil.showToastLong(activity, "请检查网络设置！");
                    }

                    @Override
                    public void onNext(List<Person> response) {
                        activity.getNetData(response);
                    }
                });
    }
}
