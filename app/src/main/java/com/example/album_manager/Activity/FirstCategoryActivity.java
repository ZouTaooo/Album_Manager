package com.example.album_manager.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.album_manager.Adapter.PicAdapter;
import com.example.album_manager.Bean.Label;
import com.example.album_manager.Bean.LabelsBean;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.Interface.ApiService;
import com.example.album_manager.R;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.bucket.GetBucketRequest;
import com.tencent.cos.xml.model.bucket.GetBucketResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.tag.ListBucket;
import com.tencent.cos.xml.utils.DigestUtils;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.SessionCredentialProvider;
import com.tencent.qcloud.core.http.HttpRequest;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import top.zibin.luban.Luban;


public class FirstCategoryActivity extends AppCompatActivity {
    final int FIRST_LEVEL = 1;

    private RecyclerView recyclerView;

    final private String[] firstCategory = {"场景", "动植物", "卡证文档", "美食", "人物", "事件", "物品", "其他"};
    private static final String TAG = "FirstCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_category);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.first_recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        List<Picture> pictures = new ArrayList<>();
        for (String string : firstCategory) {
            Picture picture = DataSupport.where("labelFirstCategory=?", string).findFirst(Picture.class);
            if (picture != null) {
                picture.setName(string);
                pictures.add(picture);
            }
        }
        PicAdapter picAdapter = new PicAdapter(pictures, this, FIRST_LEVEL);
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
                        + "\nSecond" + pic.getLabelSecondCategory()
                        + "\nLabel: " + pic.getLabelName());
            }
        }
    }
}
