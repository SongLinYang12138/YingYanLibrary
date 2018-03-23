package com.example.yingyanlirary.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.baidu.trace.api.track.LatestPoint;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class GpsBean {

    public static final String ID = "id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CODETYPE = "codeType";
    public static final String RADIUS = "radius";
    public static final String DIRECTION = "direction";
    public static final String SPEED = "speed";
    public static final String HEIGHT = "height";
    public static final String FLOOR = "floor";

    public static final String[] GPSBEANS = new String[]{
            ID, LATITUDE, LONGITUDE, CODETYPE, RADIUS, DIRECTION, SPEED, HEIGHT, FLOOR
    };

    private int id;
    private double latitude;
    private double longitude;
    private String codeType;
    private double radius;
    private int direction;
    private double speed;
    private double height;
    private String floor;

    public ContentValues toContentValue() {

        ContentValues values = new ContentValues();
        values.put(LATITUDE, this.latitude);
        values.put(LONGITUDE, this.longitude);
        values.put(CODETYPE, this.codeType);
        values.put(RADIUS, this.radius);
        values.put(DIRECTION, direction);
        values.put(SPEED, speed);
        values.put(HEIGHT, height);
        values.put(FLOOR, floor);
        return values;
    }

    public static ArrayList<GpsBean> queryGpsBean(Cursor cursor) {

        ArrayList<GpsBean> list = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                GpsBean bean = new GpsBean();

                bean.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                bean.setLatitude(cursor.getDouble(cursor.getColumnIndex(LATITUDE)));
                bean.setLongitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
                bean.setCodeType(cursor.getString(cursor.getColumnIndex(CODETYPE)));
                bean.setRadius(cursor.getDouble(cursor.getColumnIndex(RADIUS)));
                bean.setDirection(cursor.getInt(cursor.getColumnIndex(DIRECTION)));
                bean.setSpeed(cursor.getDouble(cursor.getColumnIndex(SPEED)));
                bean.setHeight(cursor.getDouble(cursor.getColumnIndex(HEIGHT)));
                bean.setFloor(cursor.getString(cursor.getColumnIndex(FLOOR)));

                list.add(bean);
            }
            cursor.close();
        } else {
            return null;
        }


        return list;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }


}
