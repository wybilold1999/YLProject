package com.cyanbirds.yljy.net;

import android.support.v4.util.ArrayMap;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-06-17 17:48 GMT+8
 * @description
 */
public interface FollowService {
    @FormUrlEncoded
    @POST("follow/addFollow")
    Call<ResponseBody> addFollow(@Header("token") String token, @Field("followId") String followId);

    @FormUrlEncoded
    @POST("follow/{listUrl}")
    Call<ResponseBody> getFollowList(@Path("listUrl") String listUrl, @Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("follow/getFollowAndLoveInfo")
    Call<ResponseBody> getFollowAndLoveInfo(@Header("token") String token, @Field("uid") String uid);

    @FormUrlEncoded
    @POST("follow/giftsList")
    Call<ResponseBody> getGiftsList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);
}
