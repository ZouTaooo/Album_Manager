package com.example.album_manager.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.album_manager.Bean.ImageLabels;
import com.example.album_manager.Bean.ImageUrl;
import com.example.album_manager.Bean.Picture;
import com.example.album_manager.Interface.ApiService;
import com.example.album_manager.R;
import com.example.album_manager.Util.HexUtils;
import com.example.album_manager.Util.Sha256;
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
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.SessionCredentialProvider;
import com.tencent.qcloud.core.http.HttpRequest;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
import retrofit2.converter.gson.GsonConverterFactory;
import top.zibin.luban.Luban;

import static android.os.Environment.getExternalStoragePublicDirectory;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class EntryActivity extends AppCompatActivity {
    private CosXmlService cosXmlService = null;

    private final static Charset UTF8 = StandardCharsets.UTF_8;

    final private String region = "ap-chengdu";
    final private String bucketName = "ai-album-1253931649";
    final private String SecretId = "AKIDf7LK4CUmGUUEiw9mRt68ub7PZWM1q0B7";
    final private String SecretKey = "nNjuwjHadYFh7PWO1OXXM1QaZO4VhnhL";

    private ProgressDialog progressDialog = null;
    private int count = 0;//统计图片数量
    private int success = 0;//成功数量
    private int fail = 0;//失败数量

    //private int num = 0;
    private boolean isSure = false;

    private Retrofit retrofit;
    private ApiService api;

    //缓存路径
    private String cachePath;

    //背景图片
    private ImageView imageView;
    //private NumberProgressBar progressBar;

    private static final String TAG = "EntryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        //初始化背景图片
        initBackgroundPic();

        //progressBar = findViewById(R.id.number_progress_bar);

        //隐藏状态栏
        setStatusBar();

        //隐藏标题栏
        setTitleBar();

        //初始化Cos服务
        initCosService();

        //初始化Retrofit2请求
        initRetrofit();

        //设置缓存路径
        cachePath = getExternalCacheDir().getPath();

        Log.e(TAG, "onCreate: " + getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());

        //IMG_20180306_145749.jpg  /storage/emulated/0/Android/data/com.example.album_manager/cache/1581763835880219.jpg
        //检查访问外存权限
        getExternalStoragePermission();
    }

    private void initBackgroundPic() {
        imageView = findViewById(R.id.entry_pic);
        Glide.with(this).load(R.drawable.entry).into(imageView);
    }

    private void setTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取权限成功
                    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                    Boolean isInitApp = pref.getBoolean("IS_INIT_APP", false);
                    if (!isInitApp) {
                        initApp();
                    } else {
                        delayTurn();
                    }
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
                //没有获得权限，请求权限
                Log.e(TAG, "getExternalStoragePermission: No permission");
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                    Log.e(TAG, "getExternalStoragePermission: Explain...");
                }

                Log.e(TAG, "getExternalStoragePermission: request...");
                //请求访问外存权限 请求code 0 用于请求结果的回调
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0
                );
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique
            } else {
                //已获得了权限 判断是否被初始化 如果初始化了延迟两秒进入 如果没有初始化进行初始化
                SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                Boolean isInitApp = pref.getBoolean("IS_INIT_APP", false);
                if (!isInitApp) {
                    initApp();
                } else {
                    delayTurn();
                }
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
                        finish();
                    }
                });
    }

    public static byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(UTF8));
    }


    public void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://tiia.tencentcloudapi.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .build())
                .build();
        api = retrofit.create(ApiService.class);
    }

    //获取标签
    private void getLabels(String ImgUrl, final int id) throws Exception {
        long StartTime = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String Timestamp = String.valueOf(StartTime);
        String Action = "DetectLabel";
        String Region = "ap-shanghai";
        String Version = "2019-05-29";
        String Date = new java.text.SimpleDateFormat("yyyy-MM-d").format(new java.util.Date(StartTime * 1000));
        //String ImgUrl = "https://ai-album-1253931649.cos.ap-chengdu.myqcloud.com/IMG20160812003422.jpg";
        //计算签名
        System.out.println("{\"ImageUrl\":\"" + ImgUrl + "\"}");
        String HashedRequestPayload = Sha256.getSHA256("{\"ImageUrl\":\"" + ImgUrl + "\"}");
        String CanonicalRequest =
                "POST" + '\n' +
                        "/" + '\n' +
                        "" + '\n' +
                        "content-type:application/json\nhost:tiia.tencentcloudapi.com\n" + '\n' +
                        "content-type;host" + '\n' +
                        HashedRequestPayload;
        System.out.println(CanonicalRequest);
        System.out.println("-------------------------------------------------------");


        String HashedCanonicalRequest = Sha256.getSHA256(CanonicalRequest);
        //1585279261
        String StringToSign =
                "TC3-HMAC-SHA256" + "\n" +
                        Timestamp + "\n" +
                        Date + "/tiia/tc3_request" + "\n" +
                        HashedCanonicalRequest;
        System.out.println(StringToSign);

        System.out.println("---------------------------------------------------------");

        byte[] secretDate = hmac256(("TC3" + SecretKey).getBytes(UTF8), Date);
        byte[] secretService = hmac256(secretDate, "tiia");
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        byte[] signature = hmac256(secretSigning, StringToSign);
        final String res = HexUtils.bytes2Hex(signature);
        System.out.println(res);
        System.out.println("---------------------------------------------------------");

        String s1 = "TC3-HMAC-SHA256 "
                + "Credential=" + SecretId
                + "/" + Date + "/tiia/tc3_request"
                + ",";
        String s2 = "SignedHeaders=content-type;host" +
                ",";
        String s3 = "Signature=" + res;
        String Authorization = s1 + s2 + s3;
        System.out.println(Authorization);
        System.out.println("---------------------------------------------------------");

        String Host = "tiia.tencentcloudapi.com";
        String Content_Type = "application/json";
        ImageUrl url = new ImageUrl();
        url.setImageUrl(ImgUrl);

        Call<ImageLabels> call = api.ImageAnalyse(url,
                Content_Type,
                Host,
                Action, Region, Version, Timestamp, Authorization);
        Log.e(TAG, "getLabels: 发送异步请求");
//        发异步请求
//        num++;//
        call.enqueue(new Callback<ImageLabels>() {
            @Override
            public void onResponse(Call<ImageLabels> call, Response<ImageLabels> response) {
                success++;
                if (response.body() != null) {
                    //选择可信度最高的标签 更新数据库
                    List<ImageLabels.ResponseBean.LabelsBean> list = response.body().getResponse().getLabels();
                    //int confidence = list.get(0).getConfidence();
                    for (ImageLabels.ResponseBean.LabelsBean label : list) {
                        final String LabelName = label.getName();
                        final String FirstCategory = label.getFirstCategory();
                        final String SecondCategory = label.getSecondCategory();
                        final int Confidence = label.getConfidence();
                        Log.e(TAG, "onResponse: " + LabelName + "  " + FirstCategory + "  " + SecondCategory + "  " + Confidence);
                        //防止有空数据 判断全部都有效再存入
                        if (LabelName != null && FirstCategory != null && SecondCategory != null) {
                            //切入IO线程进行数据库操作
                            Observable.create(new ObservableOnSubscribe<Picture>() {
                                @Override
                                public void subscribe(ObservableEmitter<Picture> e) throws Exception {
                                    Log.e(TAG, "subscribe: /////////////");
                                    Picture picture = DataSupport.find(Picture.class, id);
                                    //修改图片的一级标签、二级标签、标签名
                                    picture.setLabelName(LabelName);
                                    picture.setLabelFirstCategory(FirstCategory);
                                    picture.setLabelSecondCategory(SecondCategory);
                                    picture.setConfidence(Confidence);
                                    picture.save();
                                    //成功数量加一
                                    //切回主线程进行更新UI
                                    e.onNext(picture);
                                }
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Picture>() {
                                        @Override
                                        public void accept(Picture o) throws Exception {
                                            //如果count的值已确定，且所有图片处理完毕
                                            int all = success + fail;
                                            Log.e(TAG, "accept: fail  " + fail);
                                            Log.e(TAG, "accept: success  " + success);
                                            Log.e(TAG, "accept: all " + all);
                                            //count已确定且所有请求结果都获得
                                            if (all == count && isSure) {
                                                //写入缓存-初始化完成
                                                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                                editor.putBoolean("IS_INIT_APP", true);
                                                editor.apply();
                                                progressDialog.setMessage("全部处理完毕！");
                                                progressDialog.dismiss();
                                                //延迟两秒进入
                                                delayTurn();
                                            }
                                            Log.e(TAG, "accept: " + isSure);
                                            //更新Dialog
                                            //updateProgressDialog();
                                            if (isSure) {
                                                progressDialog.setMessage(o.getPath()
                                                        + "\n已分析" + all + "张照片");
                                            }
                                            Log.e(TAG, "onResponse: step3 访问标签成功");
                                        }
                                    });
                            //存储好最优结果 跳出循环
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "onResponse: body = null");
                }
            }

            @Override
            public void onFailure(Call<ImageLabels> call, Throwable t) {
                fail++;
            }
        });
    }


    //初始化COS
    private void initCosService() {

        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 HTTPS 请求，默认为 HTTP 请求
                .builder();

        URL url = null;
        try {
            //搭建的后台密钥
            // URL 是后台临时密钥服务的地址，如何搭建服务请参考（https://cloud.tencent.com/document/product/436/14048）
            url = new URL("http://101.133.225.58/album/key");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

/**
 * 初始化 {@link QCloudCredentialProvider} 对象，来给 SDK 提供临时密钥
 */
        QCloudCredentialProvider credentialProvider = new SessionCredentialProvider(new HttpRequest.Builder<String>()
                .url(url)
                .method("GET")
                .build());

        cosXmlService = new CosXmlService(EntryActivity.this, serviceConfig, credentialProvider);
    }

    //获取图片路径
    private void initApp() {
        //从内容提供器中搜索相机图片路径 like两边要加空格
        final Cursor photoCursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media.DISPLAY_NAME + " like ?", new String[]{"IMG%"}, null);
        //开启线程进行存储
        Observable.create(new ObservableOnSubscribe<Picture>() {
            @Override
            public void subscribe(ObservableEmitter<Picture> e) throws Exception {
                //int count = 0;
                //获取本地图片信息
                int num = 0;
                //String path = getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                try {
                    //还有下一张图片或者图片数量没到设置的预期的时候继续处理
                    //TODO 增加图片限制
                    while (photoCursor.moveToNext()) {
                        String photoPath = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.e(TAG, "subscribe: " + photoPath);

                        //照片日期
                        long photoDate = photoCursor.getLong(photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                        Log.e(TAG, "subscribe: 日期" + photoDate);

                        //照片标题
                        String photoTitle = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                        //如果图片名不是IMG开头说明不是手机拍摄照片 则跳过
//                        if (!photoTitle.startsWith("IMG")) {
//                            continue;
//                        }
                        Log.e(TAG, "subscribe: name" + photoTitle);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(photoPath, options);

                        //照片类型
                        String photoType = options.outMimeType;

                        //如果不是JPEG\JPG\PNG格式的则跳过
                        if (!photoType.equals("image/jpeg") && !photoType.equals("image/jpg") && !photoType.equals("image/png")) {
                            continue;
                        }

                        //照片长度
                        String photoLength = String.valueOf(options.outHeight);

                        //照片宽度
                        String photoWidth = String.valueOf(options.outWidth);

                        File f = new File(photoPath);
                        FileInputStream fis = new FileInputStream(f);

                        //照片大小
                        float size = fis.available() / 1000;
                        String photoSize = size + "KB";

                        //数据库中已存在则跳过
                        List<Picture> pictureList = DataSupport.where("name=?", photoTitle).find(Picture.class);
                        if (pictureList.size() != 0) {
                            continue;
                        }

                        //存入数据库
                        Picture picture = new Picture();
                        picture.setLabelFirstCategory(null);
                        picture.setLabelSecondCategory(null);
                        picture.setLabelName(null);
                        picture.setName(photoTitle);
                        picture.setPath(photoPath);
                        picture.setPut(false);
                        boolean flag = picture.save();

                        //存入数据库返回值的处理
                        if (!flag) {
                            Log.e(TAG, "subscribe: 存入数据库失败");
                            continue;
                        } else {
                            Log.e(TAG, "subscribe: 存入数据库成功");
                        }
                        num++;
                        //切换回主线程处理
                        count++;
                        e.onNext(picture);
                    }
                    photoCursor.close();
                    e.onComplete();
                } catch (Exception exc) {
                } finally {
                    if (photoCursor != null) photoCursor.close();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Picture>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        //初始化dialog
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(EntryActivity.this);
                        }
                        progressDialog.setTitle("扫描图片");
                        progressDialog.setMessage("请稍等...");
                        //设置back键不能取消
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(Picture value) {
                        //依据存入数据库的图片信息将图片压缩并上传到服务器
                        //updateProgressDialog();
                        //更新dialog
                        progressDialog.setMessage(value.getPath()
                                + "\n已扫描" + count + "张照片");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        //完成扫描
                        progressDialog.setMessage("请稍等...");
                        //统计上传的图片数量
                        count = 0;
                        //开始上传和分析
                        Observable.create(new ObservableOnSubscribe<Picture>() {
                            @Override
                            public void subscribe(ObservableEmitter<Picture> e) throws Exception {
                                //从数据库中获取所有的图片
                                List<Picture> pictures = DataSupport.findAll(Picture.class);
                                for (Picture picture : pictures) {
                                    try {
                                        //上传
                                        putObject(picture.getName(), picture.getPath(), picture.getId());
                                    } catch (IOException exc) {
                                        exc.printStackTrace();
                                    }
                                    count++;
                                    //切回主线程
                                    e.onNext(picture);
                                }
                                //所有上传请求完成
                                e.onComplete();
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Picture>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        progressDialog.setTitle("分析图片");
                                        progressDialog.setMessage("请稍等...");
                                    }

                                    @Override
                                    public void onNext(Picture value) {
                                        progressDialog.setMessage(value.getPath()
                                                + "\n已上传" + count + "张照片");
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {
                                        progressDialog.setMessage("请稍等...");
                                        //count的数量已被确定
                                        isSure = true;
                                    }
                                });
                    }
                });
    }


    //上传对象
    private void putObject(final String picName, final String picPath, final int id) throws IOException {
        String bucket = bucketName; //存储桶，格式：BucketName-APPID
        String cosPath = picName; //对象位于存储桶中的位置标识符，即对象键。例如 cosPath = "text.txt";
        final String srcPath;//"本地文件的绝对路径";


        File f = new File(picPath);

        //对图片进行压缩
        List<File> files = Luban.with(EntryActivity.this).load(f).setTargetDir(cachePath).get();
        File file = files.get(0);

        //压缩后图片的绝对路径
        srcPath = file.getAbsolutePath();
        Log.e(TAG, "putObject: 压缩图片的绝对路径：" + srcPath);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
//        putObjectRequest.setProgressListener(new CosXmlProgressListener() {
//            @Override
//            public void onProgress(long progress, long max) {
//                // todo Do something to update progress...
//                Log.e(TAG, "onProgress: 上传中");
//            }
//        });


        // 设置签名校验 Host，默认校验所有 Header
        Set<String> headerKeys = new HashSet<>();
        headerKeys.add("Host");
        putObjectRequest.setSignParamsAndHeaders(null, headerKeys);
        //使用同步方法上传
//        try {
//            PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
//            if (putObjectResult.httpCode == 200) {
//                //上传成功
//                Log.e(TAG, "putObject: 上传成功");
//                Picture pic = DataSupport.find(Picture.class, id);
//                Log.e(TAG, "putObject: 修改前路径：" + pic.getPath());
//                pic.setPath(srcPath);
//                pic.setPut(true);
//                pic.save();
//                return true;
//            }
//        } catch (CosXmlClientException e) {
//            e.printStackTrace();
//        } catch (CosXmlServiceException e) {
//            e.printStackTrace();
//        }
//        return false;

        // 使用异步回调上传
        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult result) {
                //上传成功
                PutObjectResult putObjectResult = (PutObjectResult) result;
                Log.e(TAG, "putObject: " + picName + "  " + srcPath + "  上传结果： " + putObjectResult.httpCode);
                //上传至COS服务器成功
                //更新数据库isPut字段为true
                Picture picture = DataSupport.find(Picture.class, id);
                picture.setPut(true);
                picture.setPath(srcPath);
                picture.save();
                Log.e(TAG, "accept: step2成功");
                try {
                    //获取标签
                    String url = "https://ai-album-1253931649.cos.ap-chengdu.myqcloud.com/" + picName;
                    getLabels(url, id);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                // todo Put object failed because of CosXmlClientException or CosXmlServiceException...
                Log.e(TAG, "onFail: " + serviceException.getMessage() + "\n" + serviceException.getErrorCode());
                //失败数量加一
                fail++;
                //updateProgressDialog();
            }
        });
    }

    //访问对象
    private void getObject() {
        GetBucketRequest getBucketRequest;

// 如果您需列出所有的对象，可以参考如下代码：
        getBucketRequest = new GetBucketRequest(bucketName);

// prefix 表示列出的 object 的 key 以 prefix 开始
        //getBucketRequest.setPrefix("");
// delimiter 表示分隔符，设置为 / 表示列出当前目录下的 object, 设置为空表示列出所有的 object
        //getBucketRequest.setDelimiter("");
// 设置最大遍历出多少个对象，一次 listobject 最大支持1000
        getBucketRequest.setMaxKeys(100);


        // 使用异步回调请求
        cosXmlService.getBucketAsync(getBucketRequest, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                GetBucketResult getBucketResult = (GetBucketResult) result;
                // commonPrefixs 表示表示被 delimiter 截断的路径，例如 delimter 设置为 /，commonPrefixs 则表示子目录的路径
                List<ListBucket.CommonPrefixes> commonPrefixs = getBucketResult.listBucket.commonPrefixesList;

                Log.e(TAG, "getObject: 路径" + commonPrefixs);
                // contents 表示列出的 object 列表
                List<ListBucket.Contents> contents = getBucketResult.listBucket.contentsList;

                Log.e(TAG, "getObject: contents  " + contents);

                String nextMarker = getBucketResult.listBucket.nextMarker;

                Log.e(TAG, "getObject: nextMaker" + nextMarker);

            }

            @Override
            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
                // todo Get Bucket failed because of CosXmlClientException or CosXmlServiceException...
                Log.e(TAG, "onFail: 请求对象列表失败\n");
                Log.e(TAG, "onFail: " + serviceException.getRequestId());
                Log.e(TAG, "onFail: " + serviceException.getErrorCode());
                Log.e(TAG, "onFail: " + serviceException.getMessage());
                Log.e(TAG, "onFail: " + clientException.errorCode);
            }
        });
    }
}
