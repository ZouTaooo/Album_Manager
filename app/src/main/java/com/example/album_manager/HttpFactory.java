package com.example.album_manager;

import android.util.Log;

import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.utils.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class HttpFactory {

    private static final String TAG = "HttpFactory";

    public static void getSignature() throws UnsupportedEncodingException, CosXmlClientException {
        String SecretId = "AKIDjf9TddQFqGYgh0qCSbbQDrc5QK5nQZdd";
        String SecretKey = "42hEbpQrFbsDQVmtshM2Z0MZqywlrXGp";

        long StartTime = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        long EndTime = StartTime + 7200;
        String StartTimestamp = String.valueOf(StartTime);
        String EndTimestamp = String.valueOf(EndTime);
        String KeyTime = StartTimestamp + ";" + EndTimestamp;
        System.out.println(KeyTime);

        String SignKey = DigestUtils.getHmacSha1(KeyTime, SecretKey);
        System.out.println(SignKey);

        //ci-process=detect-label
        String UrlParamList = "ci-process";
        String HttpParameters = "ci-process=detect-label";
        System.out.println(UrlParamList);
        System.out.println(HttpParameters);


        //Host: ai-album-1253931649.cos.ap-chengdu.myqcloud.com
        //Date: GMT Date
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); // 设置时区为GMT
        String Date = sdf.format(cd.getTime());
        String Host = "ai-album-1253931649.cos.ap-chengdu.myqcloud.com";
        String HeaderList = "date;host";
        System.out.println(HeaderList);
        System.out.println(Date);


        String DateEncoded = URLEncoder.encode(Date, "utf-8")
                .replace(",", "%2C")
                .replace("+", "%20")
                .replace(":", "%3A");
        String HostEncoded = URLEncoder.encode(Host, "utf-8");
        String HttpHeaders = "date=" + DateEncoded + "&host=" + HostEncoded;
        System.out.println(HttpHeaders);


        //路径
        String HttpString = "get\n" +
                "/abcde123\n" +
                HttpParameters + "\n" +
                HttpHeaders + "\n";
        System.out.println(HttpString);


        String StringToSign = "sha1\n" +
                KeyTime + "\n" +
                DigestUtils.getSha1(HttpString) + "\n";
        System.out.println(StringToSign);


        String Signature = DigestUtils.getHmacSha1(StringToSign, SignKey);
        System.out.println(Signature);


        String res = "q-sign-algorithm=sha1" +
                "&q-ak=" + SecretId +
                "&q-sign-time=" + KeyTime +
                "&q-key-time=" + KeyTime +
                "&q-header-list=" + HeaderList +
                "&q-url-param-list=" + UrlParamList +
                "&q-signature=" + Signature;

        System.out.println(res);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, CosXmlClientException {
        getSignature();
    }

}
