package com.example.yingyanlirary.utils;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.AddPointRequest;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.AddPointsResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.Point;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.example.yingyanlirary.GpsProvider;
import com.example.yingyanlirary.YingYanClient;
import com.example.yingyanlirary.YingYanUtil;
import com.example.yingyanlirary.bean.GpsBean;
import com.example.yingyanlirary.model.CurrentLocation;

import java.util.TimerTask;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class WorkThread extends TimerTask {

    private OnTrackListener trackListener;
    private String entityName = "";
    private ContentResolver contentResolver;
    private Context context;
    private SharedPreferences mPerferences;


    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;
    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private OnEntityListener entityListener = null;


    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setContentResolver(Context context) {
        contentResolver = context.getContentResolver();
    }

    public void setWorkContext(Context context) {

        this.context = context;
        mPerferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void run() {
//上报轨迹的代码

        Log.e(Constants.TAG, "workThread 开启服务");

        GpsBean bean = new GpsBean();

        //检查是否停止服务
        boolean shouldStartService = mPerferences.getBoolean(Constants.IS_SERVICE_STOPED, false);
        //检查是否停止采集
        boolean shouldStartGather = mPerferences.getBoolean(Constants.IS_GATHER_STOPED, false);


        int serviceId = mPerferences.getInt(Constants.SERVICE_ID, 161464);
        String entityName = mPerferences.getString(YingYanClient.ENTITYNAME, "");
        int interval = mPerferences.getInt(YingYanClient.INTERVAL, 0);
        int packInterval = mPerferences.getInt(YingYanClient.PACKINTERVAL, 0);
        String notiTitle = mPerferences.getString(Constants.NOTIFICATION_TITLE, "");
        String notiContent = mPerferences.getString(Constants.NOTIFICATION_CONTENT, "");
        String baiduTitle = mPerferences.getString(Constants.BAI_DU_NOTIFICATION_TITLE, "");
        String baiduContent = mPerferences.getString(Constants.BAI_DU_NOTIFICATION_CONTENT, "");

        boolean flag = CommonUtil.isServiceRunning(context, "com.baidu.trace.LBSTraceService");

        bean.setCodeType("workThread   stopService " + shouldStartService + " stopGather " + shouldStartGather + " LBS存活 " + flag);
        bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));
        contentResolver.insert(GpsProvider.CONTENT_URI, bean.toContentValue());

        if (flag) {
            return;
        }


        if (CommonUtil.isNotEmpty(entityName) && interval != 0 && packInterval != 0) {

            YingYanUtil yingYanUtil = new YingYanUtil(context);

            if (shouldStartService)
                yingYanUtil.startService(serviceId,entityName, interval, packInterval, true,notiTitle,notiContent,baiduTitle,baiduContent);

            if (shouldStartGather) yingYanUtil.startGather();
        }

//
//        if (trackListener == null) {
//            initListiner();
//        }
//        YingYanClient.getCurrentLocation(entityListener, trackListener, entityName);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private void initListiner() {
        mapUtil = MapUtil.getInstance();
        trackListener = new OnTrackListener() {
            @Override
            public void onAddPointCallback(AddPointResponse addPointResponse) {
                super.onAddPointCallback(addPointResponse);

                Log.e(Constants.TAG, addPointResponse.toString() + "  onAddPointCallback");
            }

            @Override
            public void onAddPointsCallback(AddPointsResponse addPointsResponse) {
                super.onAddPointsCallback(addPointsResponse);
                Log.e(Constants.TAG, addPointsResponse.toString() + "  onAddPointsCallback");

            }

            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                super.onHistoryTrackCallback(historyTrackResponse);
                Log.e(Constants.TAG, historyTrackResponse.toString() + "  onHistoryTrackCallback");
            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                super.onDistanceCallback(distanceResponse);
                Log.e(Constants.TAG, distanceResponse.toString() + "  onDistanceCallback");

            }

            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                super.onLatestPointCallback(response);
//                Log.e(Constants.TAG, response.toString() + "  onLatestPointCallback");

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

                Log.e(Constants.TAG, CurrentLocation.locTime + " la: " + CurrentLocation.latitude + "  lo: " + CurrentLocation.longitude);
//                saveLog(point);

//                03-20 02:15:32.947 5777-5777/? E/YingYan: LatestPointResponse [tag=14, status=0, message=成功,
// entityName=ceshi8fuwu, latestPoint=LatestPoint [location=LatLng [latitude=20.0, longitude=20.0],
// coordType=wgs84, radius=0.0, locTime=1521458975, direction=261, speed=0.0, height=0.0, floor=null,
// objectName=com.example.yingyanlirary.YingYanUtil, columns={locate_mode=GPS/北斗定位}],
// limitSpeed=0.0]  onLatestPointCallback
//  03-20 02:15:32.949 5777-5777/? I/YingYan: 1521458975 la: 20.0  lo: 20.0
//
                //自定义上传的数据
//                        LatLng currentLatLng = new LatLng(20, 20);
//        Point myPoint = response.getLatestPoint();
//                new Point(currentLatLng, CoordType.bd09ll);
//        point.setLocTime(Long.valueOf(getNowTimeStamp()));


                //自己上传轨迹的方法，后台播放音乐后不需要了
//                AddPointRequest request = new AddPointRequest(2, YingYanClient.serviceId, response.getEntityName(), point, WorkThread.class.getName(), null);
//                YingYanClient.mClient.addPoint(request, trackListener);
//
//                GpsBean bean = new GpsBean();
//                bean.setCodeType("上传轨迹  ");
//                bean.setFloor(CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis()));
//                contentResolver.insert(GpsProvider.CONTENT_URI, bean.toContentValue());
//
//                Log.e(Constants.TAG, "上传轨迹 " + point.toString());
            }
        };

        entityListener = new OnEntityListener() {

            @Override
            public void onReceiveLocation(TraceLocation location) {

                if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                        location.getLongitude())) {
                    return;
                }
                com.baidu.mapapi.model.LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                if (null == currentLatLng) {
                    return;
                }
                CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                CurrentLocation.latitude = currentLatLng.latitude;
                CurrentLocation.longitude = currentLatLng.longitude;

                Log.e(Constants.TAG, "onReceiveLocation  loc " + CurrentLocation.locTime + " la" + CurrentLocation.latitude + "  lo" + CurrentLocation.longitude);

            }

        };
    }


    private long lastSave = 0;

    private void saveLog(LatestPoint point) {

        long currentSave = System.currentTimeMillis();

        if (currentSave - lastSave < 30000) {
            return;
        } else {
            lastSave = currentSave;
        }
//        private double latitude;
//        private double longitude;
//        private String codeType;
//        private double radius;
//        private int direction;
//        private double speed;
//        private double height;
//        private String floor;
        GpsBean bean = new GpsBean();

        bean.setLatitude(point.getLocation().latitude);
        bean.setLongitude(point.getLocation().longitude);
        Log.e(Constants.TAG, "CodeType " + point.getCoordType().toString());
        bean.setCodeType(point.getCoordType().toString());
        bean.setRadius(point.getRadius());
        bean.setDirection(point.getDirection());
        bean.setSpeed(point.getSpeed());
        bean.setHeight(point.getHeight());
        bean.setFloor(point.getFloor());

        ContentValues values = bean.toContentValue();
        contentResolver.insert(GpsProvider.CONTENT_URI, values);
    }

    /**
     * 取得当前时间戳（精确到秒）
     *
     * @return nowTimeStamp
     */
    public static String getNowTimeStamp() {
        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000);
        return nowTimeStamp;
    }
}
