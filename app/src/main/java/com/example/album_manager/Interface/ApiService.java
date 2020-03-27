package com.example.album_manager.Interface;

import com.example.album_manager.Bean.ImageLabels;
import com.example.album_manager.Bean.ImageUrl;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //获取标签接口
    @POST("/")
    Call<ImageLabels> ImageAnalyse(@Body ImageUrl url,
                                   @Header("Content-Type") String Content_Type,
                                   @Header("Host") String Host,
                                   @Header("X-TC-Action") String X_TC_Action,
                                   @Header("X-TC-Region") String X_TC_Region,
                                   @Header("X-TC-Version") String X_TC_Version,
                                   @Header("X-TC-Timestamp") String X_TC_Timestamp,
                                   @Header("Authorization") String Authorization);


    //获取标签接口
    @GET("/{ObjectKey}")
    Call<ResponseBody> test(@Path("ObjectKey") String picName,
                            @Query("ci-process") String process,
                            @Header("Date") String Date,
                            @Header("Authorization") String Authorization);


}
