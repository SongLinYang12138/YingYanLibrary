package com.example.yingyanlirary.broadcast;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.activity.ScreenActivity;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.service.AliveServiceA;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

/**
 * Created by ysl on 2018/3/21.
 * 介绍:
 */

public class OnePixelReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences mPerference = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean flag = CommonUtil.isServiceRunning(context, "com.baidu.trace.LBSTraceService");
        //检查是否停止服务

        if (!flag) {
            String entityName = mPerference.getString(YingYanClient.ENTITYNAME, "");
            int interval = mPerference.getInt(YingYanClient.INTERVAL, 0);
            int packInterval = mPerference.getInt(YingYanClient.PACKINTERVAL, 0);

            boolean shouldStopServer = mPerference.getBoolean(Constants.IS_SERVICE_STOPED, false);
            boolean shouldStopGather = mPerference.getBoolean(Constants.IS_GATHER_STOPED, false);

            if (!shouldStopServer && CommonUtil.isNotEmpty(entityName) && interval != 0 && packInterval != 0) {
                YingYanUtil yingYanUtil = new YingYanUtil(context);
//后台可以播放音乐后就不需要了。
                //                yingYanUtil.startService(entityName, interval, packInterval, true);

                if (!shouldStopGather) {
                    yingYanUtil.startGather();
                }
            }
            GpsBean bean = new GpsBean();
            bean.setCodeType("onePixel name " + entityName + " interval " + interval + " pack" + packInterval + " stopServer " + shouldStopServer + "  stopGather " + shouldStopGather + "  flag  " + flag);
            bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));

            context.getContentResolver().insert(GpsProvider.CONTENT_URI, bean.toContentValue());

        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {    //屏幕关闭启动1像素Activity
//            Intent it = new Intent(context, ScreenActivity.class);

//            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e(Constants.TAG, "打开一像素" + flag);
//            try {
//                context.startActivity(it);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {   //屏幕打开 结束1像素
//            Log.e(Constants.TAG, "关闭一像素" + flag);
//
//            context.sendBroadcast(new Intent("com.driverapp.finish"));
//            Intent main = new Intent(Intent.ACTION_MAIN);
//            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            main.addCategory(Intent.CATEGORY_HOME);
//            context.startActivity(main);
        }


    }
}
