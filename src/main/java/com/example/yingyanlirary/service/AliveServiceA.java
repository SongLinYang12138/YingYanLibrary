package com.example.yingyanlirary.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ysl on 2018/3/9.
 * 介绍:
 */

public class AliveServiceA extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    startForeground(this);
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        flags = START_STICKY;
        WorkTask task = new WorkTask();
        Timer timer = new Timer(true);
        //间隔：1小时
        long period = 1000 * 60 * 60;
        timer.schedule(task, 1, period);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ALIVE_BROADCAST);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){

                    try {
                        Log.i(Constants.TAG,"运行程序");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private static final int NOTIFICATION_ID = 10;

    private static void startForeground(Service service) {
        Notification.Builder builder = new Notification.Builder(service);
        builder.setContentTitle("百度定位正在运行");
        builder.setContentText("请保持应用打开");
        Notification notification =builder.getNotification();
        service.startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e(Constants.TAG, "serviceA死亡");
        Intent intent = new Intent();
        intent.setAction(Constants.ALIVE_BROADCAST);
        sendBroadcast(intent);


    }


    private final class WorkTask extends TimerTask {

        @Override
        public void run() {

            boolean flag = CommonUtil.isServiceRunning(AliveServiceA.this.getApplicationContext(), "com.baidu.trace.LBSTraceService");

            if (!flag && YingYanClient.mClient != null) {

                SharedPreferences preferences = getApplicationContext().getSharedPreferences("entitySetting", MODE_PRIVATE);

                String entityName = preferences.getString(YingYanClient.ENTITYNAME, "");
                int interval = preferences.getInt(YingYanClient.INTERVAL, 0);
                int packInterval = preferences.getInt(YingYanClient.PACKINTERVAL, 0);

                if (!TextUtils.isEmpty(entityName) && interval != 0 && packInterval != 0) {

                    YingYanUtil yingYanUtil = new YingYanUtil(getApplicationContext());
                    yingYanUtil.startService(entityName, interval, packInterval);
                    yingYanUtil.startGather();
                }
            }
        }
    }


}
