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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
                        + "\nName" + pic.getLabelName()
                        + "\nSecond" + pic.getLabelSecondCategory()
                        + "\nLabel: " + pic.getLabelFirstCategory());
            }
        }
    }


    //此方法的作用是创建一个选项菜单，我们要重写这个方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //加载菜单文件
        getMenuInflater().inflate(R.menu.title_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //在点击这个菜单的时候，会做对应的事，类似于侦听事件，这里我们也要重写它
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //这里是一个switch语句,主要通过menu文件中的id值来确定点了哪个菜单，然后做对应的操作，这里的menu是指你加载的那个菜单文件
        switch (item.getItemId()) {
            case R.id.Analyse:
                Toast.makeText(this, "点击添加菜单", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
