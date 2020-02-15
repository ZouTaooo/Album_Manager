package com.example.album_manager.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.album_manager.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class EntryActivity extends AppCompatActivity {

    ImageView imageView;

    private static final String TAG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        imageView = findViewById(R.id.entry_pic);
        Glide.with(this).load("https://api.xygeng.cn/bing/1920.php").into(imageView);
        //隐藏标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //检查访问外存权限
        getExternalStoragePermission();
    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    delayTurn();
                } else {
                    finish();
                }
                return;
            }
            default:
                break;
        }
    }

    //获取外存权限
    private void getExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "getExternalStoragePermission: No permission");
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                    Log.e(TAG, "getExternalStoragePermission: Explain...");
                }

                Log.e(TAG, "getExternalStoragePermission: request...");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0
                );
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            } else {
                delayTurn();
            }
        }
    }

    private void delayTurn() {
        //延迟两秒进入
        Observable
                .just("")
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Intent intent = new Intent(EntryActivity.this, FirstCategoryActivity.class);
                        startActivity(intent);
                    }
                });
    }
}
