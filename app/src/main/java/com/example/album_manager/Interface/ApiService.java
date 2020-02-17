package com.example.album_manager.Interface;

import android.transition.Scene;

import com.example.album_manager.Bean.Label;

import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
//获取标签接口
@GET("/{ObjectKey}")
Call<Label> ImageAnalyse(@Path("ObjectKey") String picName,
                            @Query("ci-process") String process,
                            @Header("Date") String Date,
                            @Header("Authorization") String Authorization );
}
