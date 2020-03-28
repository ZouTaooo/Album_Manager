package com.example.album_manager.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.album_manager.Adapter.PicAdapter;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.R;
import com.example.album_manager.Util.PhotoUtil;

import org.litepal.crud.DataSupport;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class FirstCategoryActivity extends AppCompatActivity {

    final int FIRST_LEVEL = 1;

    private RecyclerView recyclerView;

    final private String[] firstCategory = {"场景", "动植物", "卡证文档", "美食", "人物", "事件", "物品", "其他"};

    private Interpreter tflite = null;

    private int[] ddims = {1, 3, 224, 224};
    private static final int USE_PHOTO = 1001;
    private static final int START_CAMERA = 1002;
    private String camera_image_path;
    private List<String> resultLabel = new ArrayList<>();

    private static final String TAG = "FirstCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_category);
        //test();
        initRecyclerView();
        load_model();
        readCacheLabelFromLocalFile();
        for (String s : resultLabel) {
            Log.e(TAG, "onCreate: " + s);
        }
        request_permissions();
    }

    private void readCacheLabelFromLocalFile() {
        try {
            AssetManager assetManager = getApplicationContext().getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("cacheLabel.txt")));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                resultLabel.add(readLine);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("labelCache", "error " + e);
        }
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
//            List<Picture> pictures = DataSupport.findAll(Picture.class);
//            Log.e(TAG, "onStart: size: " + pictures.size());
//            for (Picture pic : pictures) {
//                Log.e(TAG, "onStart: id: " + pic.getId()
//                        + "\nConfidence" + pic.getConfidence()
//                        + "\nName" + pic.getLabelName()
//                        + "\nSecond" + pic.getLabelSecondCategory()
//                        + "\nLabel: " + pic.getLabelFirstCategory());
//            }
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
                camera_image_path = PhotoUtil.start_camera(FirstCategoryActivity.this, START_CAMERA);
                Log.e(TAG, "onOptionsItemSelected: " + camera_image_path);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String image_path;
        RequestOptions options = new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        Log.e(TAG, "onActivityResult: return");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case USE_PHOTO:
                    if (data == null) {
                        Log.w(TAG, "user photo data is null");
                        return;
                    }
                    Uri image_uri = data.getData();
                    //Glide.with(MainActivity.this).load(image_uri).apply(options).into(show_image);
                    // get image path from uri
                    //image_path = PhotoUtil.get_path_from_URI(MainActivity.this, image_uri);
                    // predict image
                    //predict_image(image_path);
                    break;
                case START_CAMERA:
                    // show photo
                    //Glide.with(MainActivity.this).load(camera_image_path).apply(options).into(show_image);
                    // predict image
                    Log.e(TAG, "onActivityResult: " + camera_image_path);
                    predict_image(camera_image_path);
                    break;
            }
        } else {
            Log.e(TAG, "onActivityResult: not ok");
        }
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd("mobilenet_v2_1.0_224.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // load infer model
    private void load_model() {
        try {
            tflite = new Interpreter(loadModelFile());
            Toast.makeText(FirstCategoryActivity.this, " model load success", Toast.LENGTH_SHORT).show();
            Log.d(TAG, " model load success");
            tflite.setNumThreads(4);

        } catch (IOException e) {
            Toast.makeText(FirstCategoryActivity.this, " model load fail", Toast.LENGTH_SHORT).show();
            Log.d(TAG, " model load fail");
            e.printStackTrace();
        }
    }

    //  predict image
    private void predict_image(String image_path) {
        // picture to float array
        Log.e(TAG, "predict_image: 1");
        Bitmap bmp = PhotoUtil.getScaleBitmap(image_path);
        ByteBuffer inputData = PhotoUtil.getScaledMatrix(bmp, ddims);
        //Log.e(TAG, "predict_image: " + inputData);
        try {
            // Data format conversion takes too long
            // Log.d("inputData", Arrays.toString(inputData));
            float[][] labelProbArray = new float[1][1001];
            long start = System.currentTimeMillis();
            // get predict result
            Log.e(TAG, "predict_image: 2");
            tflite.run(inputData, labelProbArray);
            Log.e(TAG, "predict_image: 3");
            long end = System.currentTimeMillis();
            long time = end - start;
            float[] results = new float[labelProbArray[0].length];
            System.arraycopy(labelProbArray[0], 0, results, 0, labelProbArray[0].length);
            // show predict result and time
            int[] r = get_max_result(results);
            DecimalFormat decimalFormat = new DecimalFormat("0.00%");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String res = resultLabel.get(r[0]) + " : " + decimalFormat.format(results[r[0]]) + "\n"
                    + resultLabel.get(r[1]) + " : " + decimalFormat.format(results[r[1]]) + "\n"
                    + resultLabel.get(r[2]) + " : " + decimalFormat.format(results[r[2]]) + "\n"
                    + resultLabel.get(r[3]) + " : " + decimalFormat.format(results[r[3]]) + "\n"
                    + resultLabel.get(r[4]) + " : " + decimalFormat.format(results[r[4]]) + "\n";
            Log.e(TAG, "predict_image: " + res);
            Intent intent = new Intent(FirstCategoryActivity.this, ResultActivity.class);
            intent.putExtra("res", res);
            intent.putExtra("path", image_path);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get max probability label
    private int[] get_max_result(float[] result) {
        float probability = result[0];
        int[] r = {0, 0, 0, 0, 0};
        for (int i = 0; i < result.length; i++) {
            if (probability < result[i]) {
                probability = result[i];
                r[4] = r[3];
                r[3] = r[2];
                r[2] = r[1];
                r[1] = r[0];
                r[0] = i;
            }
        }
        return r;
    }

    // request permissions
    private void request_permissions() {

        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // if list is not empty will request permissions
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            String s = permissions[i];
                            Toast.makeText(this, s + " permission was denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
