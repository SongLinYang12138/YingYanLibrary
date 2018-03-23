package com.example.yingyanlirary;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.yingyanlirary.bean.GpsBean;

/**
 * Created by ysl on 2018/3/20.
 * 介绍:
 */

public class GpsProvider extends ContentProvider {
    //    cn.com.karl.personProvider
//    com.driverapp.gpsdb
    private static final int BILLS = 1;
    private static final int BILL = 2;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final String AUTHORITY = "com.driverapp.gpsdb";
    private static final String CONTENT_URI_STRING = "content://" + AUTHORITY + "/gpsdata";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);
    private GpsDb gpsDb;
    private static final String TABLE_NAME = "gpsdata";

    static {
        MATCHER.addURI(AUTHORITY, "gpsdata", BILLS);
        MATCHER.addURI(AUTHORITY, "gpsdata/#", BILL);
    }

    @Override
    public boolean onCreate() {

        gpsDb = new GpsDb(getContext(), TABLE_NAME, null, 2);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = gpsDb.getReadableDatabase();
        switch (MATCHER.match(uri)) {
            case BILLS:
                if (sortOrder == null) {
                    return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, GpsBean.ID + " desc");
                }
                return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            case BILL:
                long id = ContentUris.parseId(uri);
                String where = GpsBean.ID+"=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                return db.query(TABLE_NAME, projection, where, selectionArgs, null, null, sortOrder);
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        switch (MATCHER.match(uri)) {
            case BILLS:
                return "vnd.android.cursor.dir/" + TABLE_NAME;

            case BILL:
                return "vnd.android.cursor.item/" + TABLE_NAME;

            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = gpsDb.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case BILLS:
                long rowid = db.insert(TABLE_NAME, GpsBean.ID, values);
                return ContentUris.withAppendedId(uri, rowid);
            default:
                throw new IllegalArgumentException("Unkwon Uri:" + uri.toString());

        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = gpsDb.getWritableDatabase();
        int count = 0;
        switch (MATCHER.match(uri)) {
            case BILLS:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                return count;
            case BILL:
                long id = ContentUris.parseId(uri);
                String where = GpsBean.ID+"=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.delete(TABLE_NAME, where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkown Uri:" + uri.toString());
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        SQLiteDatabase db = gpsDb.getWritableDatabase();

        int count = 0;
        switch (MATCHER.match(uri)) {
            case BILLS:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                return count;
            case BILL:
                long id = ContentUris.parseId(uri);
                String where = GpsBean.ID+"=" + id;
                if (selection != null && !"".equals(selection)) {
                    where = selection + " and " + where;
                }
                count = db.update(TABLE_NAME, values, where, selectionArgs);
                return count;
            default:
                throw new IllegalArgumentException("Unkowon Uri: " + uri.toString());
        }

    }

    private class GpsDb extends SQLiteOpenHelper {

        public GpsDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"("+ GpsBean.ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+GpsBean.LATITUDE+" DOUBLE ,"+GpsBean.LONGITUDE+" DOUBLE,"+GpsBean.CODETYPE+" VARCHAR(20)," +
                    ""+GpsBean.RADIUS+" DOUBLE, "+GpsBean.DIRECTION+" INT, "+GpsBean.SPEED+" DOUBLE, "+GpsBean.HEIGHT+" DOUBLE, "+GpsBean.FLOOR+" VARCHAR(20));";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
