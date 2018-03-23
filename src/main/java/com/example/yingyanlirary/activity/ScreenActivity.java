package com.example.yingyanlirary.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.service.AliveServiceA;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

/**
 * Created by ysl on 2018/3/21.
 * 介绍:
 */

public class ScreenActivity extends Activity {


    private DestroyReceiver receiver = new DestroyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        Log.e(Constants.TAG, "oncreate");
        checkScreen();

        try {
            registerReceiver(receiver, new IntentFilter("com.driverapp.finish"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreen();
        Log.e(Constants.TAG, "onResume");
//
//        boolean isAlive = CommonUtil.isServiceRunning(this, AliveServiceA.class.getName());
//
//        GpsBean bean = new GpsBean();
//        bean.setCodeType("screenActivity 打开" + " 检查serviceA是否存活 " + isAlive);
//
//        bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));
//        getContentResolver().insert(GpsProvider.CONTENT_URI, bean.toContentValue());
//        if (!isAlive) {
//
//
////            //开启保活服务
////            Intent intent = new Intent(this, AliveServiceA.class);
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                startForegroundService(intent);
////            } else {
////                startService(intent);
////            }
//
//        }


    }


    /**
     * 检查屏幕状态  isScreenOn为true  屏幕“亮”结束该Activity
     */
    private void checkScreen() {

        PowerManager pm = (PowerManager) ScreenActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        Log.e(Constants.TAG, "screen  " + isScreenOn);

        if (isScreenOn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(Constants.TAG, "关闭activity");

        try {

            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
    }

    private class DestroyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(ScreenActivity.this != null)
            ScreenActivity.this.finish();
        }
    }
}
