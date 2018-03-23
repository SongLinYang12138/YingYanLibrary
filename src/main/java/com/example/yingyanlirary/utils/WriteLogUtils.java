package com.example.yingyanlirary.utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class WriteLogUtils {

    private String folderPath = "logfolder";
    private String fileName = "yingYanLog.txt";
    private String path;

    public WriteLogUtils() {

        String firstPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(firstPath+File.separator+folderPath);
        if(!file.exists()){
            file.mkdirs();
        }
         path = firstPath+File.separator+folderPath+File.separator+CommonUtil.ConverToString_PreciseMinuteSecond(System.currentTimeMillis())+fileName;
        file = new File(path);
        if(!file.exists()){
            try {
                boolean flag =  file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void writeLog(String text){


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


            }
        });
        thread.setDaemon(true);
        thread.start();




    }



}
