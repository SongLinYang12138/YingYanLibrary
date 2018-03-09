package com.example.yingyanlirary.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.yingyanlirary.service.AliveServiceA;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

/**
 * Created by ysl on 2018/3/9.
 * 介绍:
 */

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    private String action = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();

        Log.e(Constants.TAG, action + "收到广播");
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏

            Log.i(Constants.TAG, "开屏");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            Log.i(Constants.TAG, "锁屏");

        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁


            boolean flag = CommonUtil.isServiceRunning(context, "com.example.yingyanlirary.service.AliveServiceA");
            Log.i(Constants.TAG, "解锁" + flag);

            if (flag == false) {

                Intent intent1 = new Intent(context, AliveServiceA.class);
                context.startService(intent1);
                Log.e(Constants.TAG, "服务已重启");
            }

        }
    }
}