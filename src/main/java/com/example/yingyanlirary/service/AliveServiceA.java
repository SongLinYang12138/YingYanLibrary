package com.example.yingyanlirary.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.AddPointRequest;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.AddPointsResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.Point;
import com.baidu.trace.model.ProcessOption;
import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.R;
import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.broadcast.AlarmReceiver;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;
import com.example.yingyanlirary.utils.NetUtil;
import com.example.yingyanlirary.utils.NotificationUtils;
import com.example.yingyanlirary.utils.WorkThread;


import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ysl on 2018/3/9.
 * 介绍:
 */

public class AliveServiceA extends Service implements MediaPlayer.OnCompletionListener {

//    private AliveReciver reciver = new AliveReciver();

    private ContentResolver resolver;
    private PowerManager.WakeLock wl;
    private static final int NOTIFICATION_ID = 10;
    private SharedPreferences mpreferences;

    private MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "aliveservicea");
        wl.acquire();
        resolver = getContentResolver();

        player = MediaPlayer.create(this, R.raw.sliences);
        player.setOnCompletionListener(this);

//        registerReceiver(reciver, new IntentFilter("com.action.aliveb"));

        mpreferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
//        WorkTask task = new WorkTask();
//        Timer timer = new Timer(true);
//        //间隔：1小时 1000 * 60 * 60
//        long period = 1000 * 60 * 1;
//        timer.schedule(task, 1, period);
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.ALIVE_BROADCAST);


    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flags = START_STICKY;

        if (!player.isPlaying()) {
            // 开始播放
            player.start();
            // 允许循环播放
            player.setLooping(true);
        }

        String notiTitle = mpreferences.getString(Constants.NOTIFICATION_TITLE,"无车承运人");
        String notiContent = mpreferences.getString(Constants.NOTIFICATION_CONTENT,"百度定位");

        Notification notification = new NotificationUtils(this).sendNotification(notiTitle, notiContent);

        startForeground(NOTIFICATION_ID, notification);

        String entityName = mpreferences.getString(YingYanClient.ENTITYNAME, "20");

        Log.e(Constants.TAG, "开启定时任务");
        long period = 1000 * 60 * 5;
        WorkThread workTask = new WorkThread();
        workTask.setEntityName(entityName);
        workTask.setWorkContext(getApplicationContext());
        workTask.setContentResolver(getApplicationContext());
        Timer timer = new Timer(false);
        timer.schedule(workTask, 1, period);
        Log.e(Constants.TAG, "startCommand开始");


//        /**
//         * 定时开启服务和采集
//         * */
//        WorkTask workTask = new WorkTask();
//        Timer timer = new Timer(false);
//        long period = 1000 * 60 * 2;
//        timer.schedule(workTask, 1, period);
        //使用alarmManager 定时去检查服务
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int anHour = 1 * 60 * 1000; // 这是一分钟的毫秒数
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);


        return flags;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        wl.release();

//        boolean shouldStart = mpreferences.getBoolean(Constants.IS_SERVICE_STOPED,false);

        Log.e(Constants.TAG, "serviceA死亡");
        GpsBean bean = new GpsBean();
        bean.setCodeType("serviceA onDestroy");
        bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));
        getContentResolver().insert(GpsProvider.CONTENT_URI, bean.toContentValue());


//        if(!shouldStart){
//            Intent intent = new Intent();
//            intent.setAction("com.action.alivea");
//            sendBroadcast(intent);
//        }
//        unregisterReceiver(reciver);

        //先停止 再释放
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();


    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // 结束Service
        stopSelf();
    }




    private final class WorkTask extends TimerTask {

        @Override
        public void run() {

            SharedPreferences preferences = getApplicationContext().getSharedPreferences(Constants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
            String entityName = preferences.getString(YingYanClient.ENTITYNAME, "20");
            int interval = preferences.getInt(YingYanClient.INTERVAL, 0);
            int packInterval = preferences.getInt(YingYanClient.PACKINTERVAL, 0);

            if (!TextUtils.isEmpty(entityName) && interval != 0 && packInterval != 0) {
                Log.i(Constants.TAG, "开启百度定位服务");
                YingYanUtil yingYanUtil = new YingYanUtil(getApplicationContext());
                try {
//                    yingYanUtil.startService(entityName, interval, packInterval, true);
                    yingYanUtil.startGather();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                Log.i(Constants.TAG, "entitynmae没有设置，interval没有设置");

            }
//            boolean flag = CommonUtil.isServiceRunning(AliveServiceA.this.getApplicationContext(), "com.baidu.trace.LBSTraceService");
//
//            Log.i(Constants.TAG, "检查定时任务" + flag + YingYanClient.mClient);
//            if (!flag && YingYanClient.mClient != null) {
//
//                SharedPreferences preferences = getApplicationContext().getSharedPreferences("entitySetting", MODE_PRIVATE);
//
//                String entityName = preferences.getString(YingYanClient.ENTITYNAME, "20");
//                int interval = preferences.getInt(YingYanClient.INTERVAL, 20);
//                int packInterval = preferences.getInt(YingYanClient.PACKINTERVAL, 20);
//
//                if (!TextUtils.isEmpty(entityName) && interval != 0 && packInterval != 0) {
//                    Log.i(Constants.TAG, "开启百度定位服务");
//                    YingYanUtil yingYanUtil = new YingYanUtil(getApplicationContext());
//                    yingYanUtil.startService(entityName, interval, packInterval);
//                    yingYanUtil.startGather();
//                } else {
//
//                    Log.i(Constants.TAG, "entitynmae没有设置，interval没有设置");
//
//                }
//            } else {
//                Log.i(Constants.TAG, "百度定位服务已开启");
//            }
        }
    }



}
