package com.cyanbirds.yljy.net;

import android.support.v4.util.ArrayMap;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-06-17 14:18 GMT+8
 * @description 动态相关接口
 */
public interface DynamicService {

    @FormUrlEncoded
    @POST("dynamic/publishDynamic")
    Call<ResponseBody> publishDynamic(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("dynamic/dynamicList")
    Call<ResponseBody> getDynamicList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

	/**
     * 我的圈子动态
     * @param token
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("dynamic/ringList")
    Call<ResponseBody> getFrindRingDynamicList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

	/**
     * 查看具体的某个动态
     * @param token
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("dynamic/getDynamicById")
    Call<ResponseBody> getDynamicById(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

}
