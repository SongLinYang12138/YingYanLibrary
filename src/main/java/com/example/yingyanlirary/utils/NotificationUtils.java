package com.example.yingyanlirary.utils;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * Created by ysl on 2018/3/19.
 * 介绍:
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "com.drvierapp";
    public static final String name = "com.driverapp_baidu";
    private Context context;
    private String myPackage = "com.driverapp";
    private String myActivity = "com.driverapp.MainActivity";

    public NotificationUtils(Context context) {
        super(context);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getChannelNotification(String title, String content) {

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(myPackage, myActivity);
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式

        PendingIntent pendingIntent = PendingIntent.getActivity(context,111,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);

    }

    private NotificationCompat.Builder getNotification_25(String title, String content) {

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(myPackage, myActivity);
        intent.setComponent(componentName);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式

        PendingIntent pendingIntent = PendingIntent.getActivity(context,111,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }


    public Notification sendNotification(String title, String content) {


        Notification notification = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            notification = getChannelNotification
                    (title, content).build();
        } else {
            notification = getNotification_25(title, content).build();
        }


        return notification;

    }
}
