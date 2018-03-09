package com.example.yingyanlirary;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.ProcessOption;
import com.example.yingyanlirary.utils.NetUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ysl on 2018/3/8.
 * 介绍:
 */

public class YingYanClient {

    private static Context mContext;
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
    private static final long serviceId = 161016;


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

    public static void initClient(Application context) {
        mContext = context;
        SDKInitializer.initialize(mContext);
        mClient = new LBSTraceClient(mContext);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        preferences = context.getSharedPreferences("entitySetting", Context.MODE_PRIVATE);
    }

    /**
     * Entity标识
     */
    public static void setEntityName(String entityName, int interval, int packInterval) {


        if (interval <= 0) interval = 5;
        if (packInterval <= 0) packInterval = 10;
        mClient.setInterval(interval, packInterval);
        mTrace = new Trace(serviceId, entityName, false);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENTITYNAME, entityName);
        editor.putInt(INTERVAL, interval);
        editor.putInt(PACKINTERVAL, packInterval);
        editor.commit();

    }


}
