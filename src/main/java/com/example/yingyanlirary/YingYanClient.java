package com.example.yingyanlirary;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.ProcessOption;
import com.example.yingyanlirary.service.AliveServiceA;
import com.example.yingyanlirary.utils.Constants;
import com.example.yingyanlirary.utils.NetUtil;
import com.example.yingyanlirary.utils.NotificationUtils;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by ysl on 2018/3/8.
 * 介绍:
 */

public class YingYanClient {

    public static Context mContext;
    /**
     * 轨迹客户端
     */
    public static LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */
    public static Trace mTrace = null;

    /**
     * 轨迹服务ID
     */
    public   static   int serviceId = 161464;//川流天下
    //  private static final long serviceId = 161016;


    public static boolean isRegisterReceiver = false;
    /**
     * 服务是否开启标识
     */
    public static boolean isTraceStarted = false;

    /**
     * 采集标识
     */
    public static boolean isGatherStarted = false;

    public static int screenWidth = 0;
    public static int screenHeight = 0;


    private static SharedPreferences preferences;
    public static final String ENTITYNAME = "entity_name";
    public static final String INTERVAL = "interval";
    public static final String PACKINTERVAL = "pack_interval";



    public static void initClient(Context context) {

        mContext = context;
        mClient = new LBSTraceClient(mContext);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        preferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

    }



    /**
     * Entity标识
     */
    public static void setEntityName(int serverId,String entityName, int interval, int packInterval,String title,String content) {

//        if (interval <= 0) interval = 40;
//        if (packInterval <= 0) packInterval = 80;
        if(entityName == null){
            Toast.makeText(mContext, "请添加entityname", Toast.LENGTH_SHORT).show();
            return;
        }else if(interval <=0 || packInterval <=0){
            Toast.makeText(mContext, "请添加上传周期和打包周期", Toast.LENGTH_SHORT).show();

        }
        mClient.setInterval(interval, packInterval);
        mTrace = new Trace(serverId, entityName, false);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENTITYNAME, entityName);
        editor.putInt(INTERVAL, interval);
        editor.putInt(PACKINTERVAL, packInterval);
        editor.commit();
        NotificationUtils notificationUtils = new NotificationUtils(mContext);

        mTrace.setNotification(notificationUtils.sendNotification(title,content));

    }
    private static AtomicInteger mSequenceGenerator = new AtomicInteger();
    /**
     * 获取请求标识
     *
     * @return
     */
    public static int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    private static LocRequest locRequest = null;
    /**
     * 获取当前位置
     */
    public static void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener, String name,int serviceId) {
        locRequest = new LocRequest(serviceId);
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, name);
            ProcessOption processOption = new ProcessOption();
            processOption.setNeedDenoise(true);
            processOption.setRadiusThreshold(100);
            request.setProcessOption(processOption);
            YingYanClient.mClient.queryLatestPoint(request, trackListener);
        } else {
            YingYanClient.mClient.queryRealTimeLoc(locRequest, entityListener);
        }
    }



}
