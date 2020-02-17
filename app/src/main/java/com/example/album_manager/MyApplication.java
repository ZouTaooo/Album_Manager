package com.example.album_manager;

import android.app.Application;

import org.litepal.LitePal;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化litepal数据库
        LitePal.initialize(this);
    }
}
