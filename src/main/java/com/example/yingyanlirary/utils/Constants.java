package com.example.yingyanlirary.utils;

/**
 * Created by baidu on 17/1/12.
 */

public final class Constants {

    public static final String TAG = "YingYan";

    public static final int REQUEST_CODE = 1;

    public static final int RESULT_CODE = 1;

    public static final int DEFAULT_RADIUS_THRESHOLD = 0;

    public static final int PAGE_SIZE = 5000;

    /**
     * 轨迹分析查询间隔时间（1分钟）
     */
    public static final int ANALYSIS_QUERY_INTERVAL = 60;

    /**
     * 停留点默认停留时间（1分钟）
     */
    public static final int STAY_TIME = 60;

    /**
     * 启动停留时间
     */
    public static final int SPLASH_TIME = 3000;

    /**
     * 默认采集周期
     */
    public static final int DEFAULT_GATHER_INTERVAL = 5;

    /**
     * 默认打包周期
     */
    public static final int DEFAULT_PACK_INTERVAL = 10;

    /**
     * 实时定位间隔(单位:秒)
     */
    public static final int LOC_INTERVAL = 10;

    /**
     * 最后一次定位信息
     */
    public static final String LAST_LOCATION = "last_location";

    /**
     * 存储信息的文件名称
     * */
    public static final String PREFERENCE_FILE_NAME = "entitySetting";

    /**
     *检查是否停止了服务
     * */
    public static  final String IS_SERVICE_STOPED = "is_service_stop";

    /**
     *检查是否停止采集
     * */
    public static  final String IS_GATHER_STOPED = "is_gather_stop";

   /**
    * 程序的notification显示的标题
    * */
   public static  final String NOTIFICATION_TITLE = "notification_title";

   /**
    * 程序的notification显示的内容
    * */
   public static  final String NOTIFICATION_CONTENT = "notification_content";

   /**
    * 百度的notifcation显示的标题
    * */
   public static final String BAI_DU_NOTIFICATION_TITLE = "bai_du_notification_title";

    /**
     * 百度的notifcation显示的内容
     * */
    public static final String BAI_DU_NOTIFICATION_CONTENT = "bai_du_notification_title";

    /**
     * 百度服务的鹰眼id
     * */
    public static final String SERVICE_ID = "service_id";

}
