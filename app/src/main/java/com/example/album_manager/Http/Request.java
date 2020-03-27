package com.example.album_manager.Http;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.album_manager.Bean.ImageLabels;
import com.example.album_manager.Bean.ImageUrl;
import com.example.album_manager.Interface.ApiService;
import com.example.album_manager.Util.HexUtils;
import com.example.album_manager.Util.Sha256;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Request {
    private final static Charset UTF8 = StandardCharsets.UTF_8;
    private static String region = "ap-chengdu";
    private static String bucketName = "ai-album-1253931649";
    private static String SecretId = "AKIDf7LK4CUmGUUEiw9mRt68ub7PZWM1q0B7";
    private static String SecretKey = "nNjuwjHadYFh7PWO1OXXM1QaZO4VhnhL";
    private static Retrofit retrofit;
    private static ApiService api;

    private static final String TAG = "test";

    public Request() {
        initRetrofit();
    }

    void getLabel(String ImgUrl) throws Exception {
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
        call.enqueue(new Callback<ImageLabels>() {
            @Override
            public void onResponse(Call<ImageLabels> call, Response<ImageLabels> response) {
                System.out.println("success");
                //System.out.println(response.body().getResponse().getAlbumLabels().get(0).getFirstCategory());
                System.out.println(response.code());
                System.out.println(response.message());
                String s1 = response.body().getResponse().getLabels().get(0).getFirstCategory();
                String s2 = response.body().getResponse().getLabels().get(0).getSecondCategory();
                String s3 = response.body().getResponse().getLabels().get(0).getName();
                int s4 = response.body().getResponse().getLabels().get(0).getConfidence();
                System.out.println(s1);
                System.out.println(s2);
                System.out.println(s3);
                System.out.println(s4);
            }


            @Override
            public void onFailure(Call<ImageLabels> call, Throwable t) {
                System.out.println("fail");
                System.out.println(t.getMessage());
            }
        });
    }


    public static byte[] hmac256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(UTF8));
    }


    public static void initRetrofit() {
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
}
