package com.example.yingyanlirary.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LBSTraceService;
import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.service.AliveServiceA;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.Constants;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        GpsBean bean = new GpsBean();



        SharedPreferences mperference = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        boolean flag = CommonUtil.isServiceRunning(context, "com.baidu.trace.LBSTraceService");
        //检查是否停止服务



        boolean shouldStartService = mperference.getBoolean(Constants.IS_SERVICE_STOPED, false);
        //检查是否停止采集
        boolean shouldStartGather = mperference.getBoolean(Constants.IS_GATHER_STOPED, false);

        bean.setCodeType("alarmService运行  LBS存活 = "+flag+"  shouldStartService"+shouldStartService+"  shouldGatherService "+shouldStartGather);
        bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));
        context.getContentResolver().insert(GpsProvider.CONTENT_URI, bean.toContentValue());


        String entityName = mperference.getString(YingYanClient.ENTITYNAME, "");
        int interval = mperference.getInt(YingYanClient.INTERVAL, 0);
        int packInterval = mperference.getInt(YingYanClient.PACKINTERVAL, 0);

        if (CommonUtil.isNotEmpty(entityName) && interval != 0 && packInterval != 0) {

            YingYanUtil yingYanUtil = new YingYanUtil(context);

            if (shouldStartService)
//                yingYanUtil.startService(entityName, interval, packInterval, true);

            if (shouldStartGather) yingYanUtil.startGather();
        }


    }
}
