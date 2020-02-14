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
import android.util.Log;

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
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainActivity extends AppCompatActivity {

    private CosXmlService cosXmlService = null;
    final private String region = "ap-chengdu";
    final private String bucketName = "ai-album-1253931649";
    private ProgressDialog progressDialog = null;
    private int count = 0;//统计图片数量
    private int fail = 0;
    private Retrofit retrofit;
    private ApiService api;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //检查访问外存权限
        getExternalStoragePermission();
        //初始化Cos服务
        initCosService();
        //初始化Retrofit2请求
        initRetrofit();
        try {
            getLabels("[Cosdoki] 架乃ゆら Yura Kano kanoyura_pic_sailor1 写真集1.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//            getPicturePath();
//        }
    }


    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + bucketName + ".cos." + region + ".myqcloud.com")
                .addConverterFactory(SimpleXmlConverterFactory.create())
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
    private void getLabels(String PicName) throws IOException {
        long StartTime = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        long EndTime = StartTime + 7200;
        String StartTimestamp = String.valueOf(StartTime);
        String EndTimestamp = String.valueOf(EndTime);
        String KeyTime = StartTimestamp + ";" + EndTimestamp;
        String SignKey = null;
        try {
            SignKey = DigestUtils.getHmacSha1(KeyTime, SecretKey);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }
        String UrlParamList = "ci-process";
        String HttpParameters = "ci-process=detect-label";
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String Date = sdf.format(cd.getTime());
        String Host = "ai-album-1253931649.cos.ap-chengdu.myqcloud.com";
        String HeaderList = "date;host";
        String DateEncoded = null;
        try {
            DateEncoded = URLEncoder.encode(Date, "utf-8")
                    .replace(",", "%2C")
                    .replace("+", "%20")
                    .replace(":", "%3A");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String HostEncoded = null;
        try {
            HostEncoded = URLEncoder.encode(Host, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String HttpHeaders = "date=" + DateEncoded + "&host=" + HostEncoded;
        //路径
        String HttpString = "get\n" +
                "/" + PicName + "\n" +
                HttpParameters + "\n" +
                HttpHeaders + "\n";

        String StringToSign = null;
        try {
            StringToSign = "sha1\n" +
                    KeyTime + "\n" +
                    DigestUtils.getSha1(HttpString) + "\n";
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }

        String Signature = null;
        try {
            Signature = DigestUtils.getHmacSha1(StringToSign, SignKey);
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        }

        String res = "q-sign-algorithm=sha1" +
                "&q-ak=" + SecretId +
                "&q-sign-time=" + KeyTime +
                "&q-key-time=" + KeyTime +
                "&q-header-list=" + HeaderList +
                "&q-url-param-list=" + UrlParamList +
                "&q-signature=" + Signature;
        Call<Label> getLabel = api.ImageAnalyse(PicName, "detect-label", Date, res);
        //发同步请求
//        Response<ResponseBody> response = getLabel.execute();
//        response.body();

        //发异步请求
        getLabel.enqueue(new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                Log.e(TAG, "onResponse: " + response.code());
                Log.e(TAG, "onResponse: " + response.message());
                // Log.e(TAG, "onResponse: " + response.toString());
                Log.e(TAG, "onResponse: " + response.isSuccessful());
                // Log.e(TAG, "onResponse: " + response.errorBody());
                if (response.body() != null) {
                    Log.e(TAG, "onResponse: " + response.raw().toString());
                    Log.e(TAG, "onResponse: " + response.body().getLabelList().toString());
                    Log.e(TAG, "onResponse: " + response.body().getLabelList().size());
                    for (LabelsBean label : response.body().getLabelList()) {
                        Log.e(TAG, "onResponse: Name" + label.getName());
                        Log.e(TAG, "onResponse: Confidence" + label.getConfidence());
                        Log.e(TAG, "onResponse: First" + label.getFirstCategory());
                        Log.e(TAG, "onResponse: Second" + label.getSecondCategory());
                    }
                } else {
                    Log.e(TAG, "onResponse: body = null");
                }
            }

            @Override
            public void onFailure(Call<Label> call, Throwable throwable) {
                Log.e(TAG, "onFailure: " + throwable.toString());
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

        cosXmlService = new CosXmlService(MainActivity.this, serviceConfig, credentialProvider);
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
            }
        }
    }


    //权限请求回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    // request successfully, handle you transactions

                } else {

                    // permission denied
                    // request failed
                }
                return;
            }
            default:
                break;
        }
    }

    //获取图片路径
    private void getPicturePath() {
        final Cursor photoCursor = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);
        Observable.create(new ObservableOnSubscribe<Picture>() {
            @Override
            public void subscribe(ObservableEmitter<Picture> e) throws Exception {
                //获取本地图片信息
                try {
                    while (photoCursor.moveToNext()) {
                        String photoPath = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //照片日期
                        long photoDate = photoCursor.getLong(photoCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                        //照片标题
                        String photoTitle = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(photoPath, options);
                        //照片类型
                        String photoType = options.outMimeType;

                        //如果不是JPEG\JPG\PNG格式的则跳过
                        if (!photoType.equals("image/jpeg") && !photoType.equals("image/jpg") && !photoType.equals("image/png")) {
                            continue;
                        }

                        //数据库中已存在则跳过
                        List<Picture> pictureList = DataSupport.where("name=?", photoTitle).find(Picture.class);
                        if (pictureList.size() != 0) {
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

                        //存入数据库
                        Picture picture = new Picture();
                        picture.setLabelFirstCategory(null);
                        picture.setLabelSecondCategory(null);
                        picture.setLabelName(null);
                        picture.setName(photoTitle);
                        picture.setPath(photoPath);
                        picture.setPut(false);
                        //存入数据库失败 则跳过
                        if (!picture.save()) {
                            fail++;
                            continue;
                        }
                        e.onNext(picture);
                    }
                    photoCursor.close();
                    e.onComplete();
                } catch (Exception exc) {
                } finally {
                    if (photoCursor != null) photoCursor.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Picture, Picture>() {
                    @Override
                    public Picture apply(Picture picture) throws Exception {
                        boolean isSuccess = putObject(picture.getName(), picture.getPath());
                        if (isSuccess) {
                            return picture;
                        } else {
                            fail++;
                        }
                        return null;
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<Picture, Integer>() {
                    @Override
                    public Integer apply(Picture picture) throws Exception {
                        if (picture != null) {
                            //请求标签
                            //getLabels();
                        }
                        return count;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(MainActivity.this);
                        }
                        progressDialog.setTitle("扫描图片");
                        progressDialog.setMessage("已扫描到0张新图片...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.e(TAG, "onNext: " + value);
                        progressDialog.setMessage("已扫描" + value + "张新图片...");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        progressDialog.setMessage("扫描结束...");
                    }
                });
    }


    //上传对象
    private boolean putObject(final String picName, final String picPath) {
        String bucket = bucketName; //存储桶，格式：BucketName-APPID
        String cosPath = picName; //对象位于存储桶中的位置标识符，即对象键。例如 cosPath = "text.txt";
        String srcPath = picPath;//"本地文件的绝对路径";
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
        try {
            PutObjectResult putObjectResult = cosXmlService.putObject(putObjectRequest);
            if (putObjectResult.httpCode == 200) {
                return true;
            }
        } catch (CosXmlClientException e) {
            e.printStackTrace();
        } catch (CosXmlServiceException e) {
            e.printStackTrace();
        }
        return false;

//        // 使用异步回调上传
//        cosXmlService.putObjectAsync(putObjectRequest, new CosXmlResultListener() {
//            @Override
//            public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult result) {
//                PutObjectResult putObjectResult = (PutObjectResult) result;
//                Log.e(TAG, "putObject: " + picName + "  " + picPath + "  上传结果： " + putObjectResult.httpCode);
//            }
//
//            @Override
//            public void onFail(CosXmlRequest cosXmlRequest, CosXmlClientException clientException, CosXmlServiceException serviceException) {
//                // todo Put object failed because of CosXmlClientException or CosXmlServiceException...
//                Log.e(TAG, "onFail: " + serviceException.getMessage() + "\n" + serviceException.getErrorCode());
//            }
//        });
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
