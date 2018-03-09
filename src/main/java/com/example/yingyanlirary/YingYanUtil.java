package com.example.yingyanlirary;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.fence.FenceAlarmPushInfo;
import com.baidu.trace.api.fence.MonitoredAction;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.example.yingyanlirary.model.CurrentLocation;
import com.example.yingyanlirary.utils.CommonUtil;
import com.example.yingyanlirary.utils.MapUtil;
import com.example.yingyanlirary.utils.ViewUtil;

/**
 * Created by ysl on 2018/3/8.
 * 介绍:
 */

public class YingYanUtil {
    private ViewUtil viewUtil = null;
    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;

    /**
     * 轨迹服务监听器
     */
    private OnTraceListener traceListener = null;

    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    private OnTrackListener trackListener = null;

    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private OnEntityListener entityListener = null;

    private static final String TAG = "YINGYANUTIL";
    private Context context;


    public YingYanUtil(Context context) {
        mapUtil = MapUtil.getInstance();
        viewUtil = new ViewUtil();
        this.context = context;
        initListener();
    }

    /***
     * 开启服务
     * */

    public void startService(String name,int interval,int packInterval) {

        YingYanClient.setEntityName(name,interval,packInterval);
        if (YingYanClient.mTrace == null) {
            viewUtil.showToast(context, "请设置EntityName");
            return;
        }
        try {
            YingYanClient.mClient.startTrace(YingYanClient.mTrace, traceListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭服务
     */
    public void stopService() {

        try {
            YingYanClient.mClient.stopTrace(YingYanClient.mTrace, traceListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 开启采集
     */

    public void startGather() {
        if (!YingYanClient.isTraceStarted) {
            viewUtil.showToast(context, "请开启服务");
            return;
        }
        try {
            YingYanClient.mClient.startGather(traceListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭采集
     */

    public void stopGather() {

        try {
            YingYanClient.mClient.stopGather(traceListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initListener() {

        trackListener = new OnTrackListener() {

            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                if (StatusCodes.SUCCESS != response.getStatus()) {
                    return;
                }

                LatestPoint point = response.getLatestPoint();
                if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                        .getLongitude())) {
                    return;
                }

                LatLng currentLatLng = mapUtil.convertTrace2Map(point.getLocation());
                if (null == currentLatLng) {
                    return;
                }
//经纬度
                CurrentLocation.locTime = point.getLocTime();
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;


            }
        };

        entityListener = new OnEntityListener() {

            @Override
            public void onReceiveLocation(TraceLocation location) {

                if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                        location.getLongitude())) {
                    return;
                }
                LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                if (null == currentLatLng) {
                    return;
                }
                CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;


            }

        };

        traceListener = new OnTraceListener() {

            /**
             * 绑定服务回调接口
             * @param errorNo  状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>1：失败</pre>
             */
            @Override
            public void onBindServiceCallback(int errorNo, String message) {

                viewUtil.showToast(context, String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
                Log.e(TAG, String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 开启服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功 </pre>
             *                <pre>10000：请求发送失败</pre>
             *                <pre>10001：服务开启失败</pre>
             *                <pre>10002：参数错误</pre>
             *                <pre>10003：网络连接失败</pre>
             *                <pre>10004：网络未开启</pre>
             *                <pre>10005：服务正在开启</pre>
             *                <pre>10006：服务已开启</pre>
             */
            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    YingYanClient.isTraceStarted = true;

//                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
//                    editor.putBoolean("is_trace_started", true);
//                    editor.apply();
////                    setTraceBtnStyle();
//                    registerReceiver();
                } else {


                }
                viewUtil.showToast(context, String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
                Log.e(TAG, String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 停止服务回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>11000：请求发送失败</pre>
             *                <pre>11001：服务停止失败</pre>
             *                <pre>11002：服务未开启</pre>
             *                <pre>11003：服务正在停止</pre>
             */
            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    YingYanClient.isTraceStarted = false;
                    YingYanClient.isGatherStarted = false;

                    // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
//                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
//                    editor.remove("is_trace_started");
//                    editor.remove("is_gather_started");
//                    editor.apply();
//                    setTraceBtnStyle();
//                    setGatherBtnStyle();
//                    unregisterPowerReceiver();
                }
                viewUtil.showToast(context,
                        String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 开启采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>12000：请求发送失败</pre>
             *                <pre>12001：采集开启失败</pre>
             *                <pre>12002：服务未开启</pre>
             */
            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                    YingYanClient.isGatherStarted = true;

//                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
//                    editor.putBoolean("is_gather_started", true);
//                    editor.apply();
//                    setGatherBtnStyle();
                }
                viewUtil.showToast(context,
                        String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 停止采集回调接口
             * @param errorNo 状态码
             * @param message 消息
             *                <p>
             *                <pre>0：成功</pre>
             *                <pre>13000：请求发送失败</pre>
             *                <pre>13001：采集停止失败</pre>
             *                <pre>13002：服务未开启</pre>
             */
            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                    YingYanClient.isGatherStarted = false;

//                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
//                    editor.remove("is_gather_started");
//                    editor.apply();
//                    setGatherBtnStyle();

                }
                viewUtil.showToast(context,
                        String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            /**
             * 推送消息回调接口
             *
             * @param messageType 状态码
             * @param pushMessage 消息
             *                  <p>
             *                  <pre>0x01：配置下发</pre>
             *                  <pre>0x02：语音消息</pre>
             *                  <pre>0x03：服务端围栏报警消息</pre>
             *                  <pre>0x04：本地围栏报警消息</pre>
             *                  <pre>0x05~0x40：系统预留</pre>
             *                  <pre>0x41~0xFF：开发者自定义</pre>
             */
            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {
                if (messageType < 0x03 || messageType > 0x04) {
                    viewUtil.showToast(context, pushMessage.getMessage());
                    return;
                }
                FenceAlarmPushInfo alarmPushInfo = pushMessage.getFenceAlarmPushInfo();
                if (null == alarmPushInfo) {
                    viewUtil.showToast(context,
                            String.format("onPushCallback, messageType:%d, messageContent:%s ", messageType,
                                    pushMessage));
                    return;
                }
                StringBuffer alarmInfo = new StringBuffer();
                alarmInfo.append("您于")
                        .append(CommonUtil.getHMS(alarmPushInfo.getCurrentPoint().getLocTime() * 1000))
                        .append(alarmPushInfo.getMonitoredAction() == MonitoredAction.enter ? "进入" : "离开")
                        .append(messageType == 0x03 ? "云端" : "本地")
                        .append("围栏：").append(alarmPushInfo.getFenceName());

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {

                    Log.e("baidu", alarmInfo.toString());
                    viewUtil.showToast(context, alarmInfo.toString());
//                    Notification notification = new Notification.Builder(trackApp)
//                            .setContentTitle(getResources().getString(R.string.alarm_push_title))
//                            .setContentText(alarmInfo.toString())
//                            .setSmallIcon(R.mipmap.icon_app)
//                            .setWhen(System.currentTimeMillis()).build();
//                    notificationManager.notify(notifyId++, notification);
                }
            }

            @Override
            public void onInitBOSCallback(int errorNo, String message) {
                viewUtil.showToast(context,
                        String.format("onInitBOSCallback, errorNo:%d, message:%s ", errorNo, message));
            }
        };

    }


}
