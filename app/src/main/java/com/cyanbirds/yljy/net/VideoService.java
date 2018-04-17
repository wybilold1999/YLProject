package com.cyanbirds.yljy.net;

import android.support.v4.util.ArrayMap;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-06-17 17:48 GMT+8
 * @description
 */
public interface VideoService {
    @FormUrlEncoded
    @POST("video/sendRose")
    Call<ResponseBody> sendRose(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("video/videoList")
    Call<ResponseBody> getVideoList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("video/typeVideoList")
    Call<ResponseBody> getTypeVideoList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

}
