package com.example.yingyanlirary.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class AliveServiceB extends Service {

    private AliveReciver reciver = new AliveReciver();

    private SharedPreferences mperference;

    public class MyBinder extends Binder {

        public AliveServiceB getService() {
            return AliveServiceB.this;
        }
    }

    //通过binder实现了 调用者（client）与 service之间的通信
    private MyBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mperference = getSharedPreferences(Constants.PREFERENCE_FILE_NAME, MODE_PRIVATE);
        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent mintent = new Intent("com.service.alive");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AliveServiceB.this, 0, mintent, 0);
        /**
         * 定义重复提醒：间隔5秒后发送一次广播
         */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60 * 1000, 4 * 60 * 1000, pendingIntent);

        registerReceiver(reciver, new IntentFilter("com.action.alivea"));
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        boolean shouldStart = mperference.getBoolean(Constants.IS_SERVICE_STOPED, false);

        if (!shouldStart) {

            Intent intent = new Intent();
            intent.setAction("com.action.aliveb");
            sendBroadcast(intent);
        }

        unregisterReceiver(reciver);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private class AliveReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            Log.e(Constants.TAG, "守护进程 开启服务");
//            //检查是否停止服务
//            boolean shouldStartService = mperference.getBoolean(Constants.IS_SERVICE_STOPED, false);
//            //检查是否停止采集
//            boolean shouldStartGather = mperference.getBoolean(Constants.IS_GATHER_STOPED, false);
//            String entityName = mperference.getString(YingYanClient.ENTITYNAME, "");
//            int interval = mperference.getInt(YingYanClient.INTERVAL, 0);
//            int packInterval = mperference.getInt(YingYanClient.PACKINTERVAL, 0);
//
//            if (CommonUtil.isEmpty(entityName) && interval != 0 && packInterval != 0) {
//
//                YingYanUtil yingYanUtil = new YingYanUtil(context);
//
//                if (shouldStartService)
//                    yingYanUtil.startService(entityName, interval, packInterval, true);
//
//                if (shouldStartGather) yingYanUtil.startGather();
//            }


            Intent intent1 = new Intent(AliveServiceB.this, AliveServiceA.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent1);
            } else {
                context.startService(intent1);
            }
        }
    }

}
