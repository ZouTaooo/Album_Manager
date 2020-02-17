package com.example.album_manager.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.album_manager.Adapter.PicAdapter;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


public class FirstCategoryActivity extends AppCompatActivity {

    final int FIRST_LEVEL = 1;

    private RecyclerView recyclerView;

    final private String[] firstCategory = {"场景", "动植物", "卡证文档", "美食", "人物", "事件", "物品", "其他"};

    private static final String TAG = "FirstCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_category);
        //test();
        initRecyclerView();
    }


    //获取遗漏的标签
    private void test() {
        List<String> first = new ArrayList<>();
        List<Picture> pictures = DataSupport.findAll(Picture.class);
        for (Picture picture : pictures) {
            String temp = picture.getLabelFirstCategory();
            if (!first.contains(temp) && temp != null) {
                first.add(temp);
            }
        }

        Log.e(TAG, "onCreate: " + first);

        List<String> second = new ArrayList<>();
        for (String a : first) {
            List<Picture> list = DataSupport.where("labelFirstCategory=?", a).find(Picture.class);
            for (Picture picture : list) {
                String temp = picture.getLabelSecondCategory();
                if (!second.contains(temp) && temp != null) {
                    second.add(temp);
                }

            }
            Log.e(TAG, "onCreate: " + a + " :" + second);
            second.clear();
        }
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.first_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        List<Picture> pictures = new ArrayList<>();
        for (String string : firstCategory) {
            Picture picture = DataSupport.where("labelFirstCategory=? and Confidence>30", string).findFirst(Picture.class);
            if (picture != null) {
                picture.setName(string);
                pictures.add(picture);
            }
        }
        PicAdapter picAdapter = new PicAdapter(pictures, this, FIRST_LEVEL, null, null);
        recyclerView.setAdapter(picAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            //getPicturePath();
            Log.e(TAG, "onStart: ////////////////////////////////////////////////////////");
            List<Picture> pictures = DataSupport.findAll(Picture.class);
            Log.e(TAG, "onStart: size: " + pictures.size());
            for (Picture pic : pictures) {
                Log.e(TAG, "onStart: id: " + pic.getId()
                        + "\nConfidence" + pic.getConfidence()
                        + "\nName" + pic.getName()
                        + "\nSecond" + pic.getLabelSecondCategory()
                        + "\nLabel: " + pic.getLabelName());
            }
        }
    }
}
