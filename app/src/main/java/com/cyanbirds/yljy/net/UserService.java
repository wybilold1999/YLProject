package com.cyanbirds.yljy.net;

import android.support.v4.util.ArrayMap;

import com.cyanbirds.yljy.config.AppConstants;

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
 * @description 用户相关接口
 */
public interface UserService {

    @FormUrlEncoded
    @POST("user/login")
    Call<ResponseBody> userLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/qq_login")
    Call<ResponseBody> qqLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/weibo_login")
    Call<ResponseBody> weiboLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/wechat_login")
    Call<ResponseBody> wxLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/xm_login")
    Call<ResponseBody> xmLogin(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/updatePerson")
    Call<ResponseBody> updateUserInfo(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/checkIsRegister")
    Call<ResponseBody> checkIsRegister(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("user/register")
    Call<ResponseBody> userRegister(@FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/userInfo")
    Call<ResponseBody> getUserInfo(@Header("token") String token, @Field("uid") String uid);

    @FormUrlEncoded
    @POST("user/uploadPic")
    Call<ResponseBody> uploadPic(@FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/homeList")
    Call<ResponseBody> getFindLoveInfo(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/logoutLogin")
    Call<ResponseBody> userLogout(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/newPasswordSetting")
    Call<ResponseBody> findPwd(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/updatePassword")
    Call<ResponseBody> modifyPwd(@Header("token") String token, @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("user/realUserHomeList")
    Call<ResponseBody> getRealUserHomeList(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("user/getUserName")
    Call<ResponseBody> getUserName(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @FormUrlEncoded
    @POST("captch/smsCode")
    Call<ResponseBody> checkSmsCode(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @GET("oss/distribute-token")
    Call<ResponseBody> getOSSToken();

	/**
     * 缘分卡片
     * @param token
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST("user/yuanFenUser")
    Call<ResponseBody> getYuanFenUser(@Header("token") String token, @FieldMap ArrayMap<String, Integer> params);

	/**
     * 会员商品信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("buy/buyList")
    Call<ResponseBody> getBuyList(@Header("token") String token, @Field("type") int type);

    /**
     * app列表信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("buy/apkList")
    Call<ResponseBody> getApkList(@Header("token") String token, @Field("pageNo") int pageNo, @Field("pageSize") int pageSize);

    /**
     * 购买金币
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("buy/updateGold")
    Call<ResponseBody> updateGold(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    @GET("buy/getBetweenLoversInfo")
    Call<ResponseBody> getBetweenLoversInfo(@Header("token") String token);

    @GET("buy/getSuccessCaseList")
    Call<ResponseBody> getSuccessCaseList(@Header("token") String token);

    @GET("buy/getLovePartyList")
    Call<ResponseBody> getLovePartyList(@Header("token") String token);

    /**
     * app列表信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/updateVip")
    Call<ResponseBody> updateVip(@Header("token") String token, @Field("type") Integer type);

    /**
     * 上传crash信息
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadCrash")
    Call<ResponseBody> uploadCrash(@Field("crashInfo") String crashInfo);

    @FormUrlEncoded
    @POST("memberOrders/createOrder")
    Call<ResponseBody> createOrder(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 获取支付成功之后用户的vip信息
     * @param token
     * @return
     */
    @GET("user/getPayResult")
    Call<ResponseBody> getPayResult(@Header("token") String token);

    /**
     * 提交身份认证
     * @return
     */
    @FormUrlEncoded
    @POST("user/identify")
    Call<ResponseBody> identify(@FieldMap ArrayMap<String, String> params,  @Header("token") String token);

	/**
     * 获取身份认证信息
     * @param token
     * @return
     */
    @GET("user/getIdentify")
    Call<ResponseBody> getIdentify(@Header("token") String token);

    /**
     * 获取礼物信息
     * @param token
     * @return
     */
    @GET("user/getGift")
    Call<ResponseBody> getGift(@Header("token") String token);

    /**
     * 获取礼物信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/sendGift")
    Call<ResponseBody> sendGift(@FieldMap ArrayMap<String, String> params, @Header("token") String token);

    /**
     * 上传token
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadToken")
    Call<ResponseBody> uploadToken(@FieldMap ArrayMap<String, String> params, @Header("token") String token);

    /**
     * 上传城市信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("user/uploadCityInfo")
    Call<ResponseBody> uploadCityInfo(@FieldMap ArrayMap<String, String> params, @Header("token") String token);

    /**
     * 获取用户所在城市
     * @return
     */
    @GET("http://restapi.amap.com/v3/ip?key=" + AppConstants.WEB_KEY)
    Call<ResponseBody> getCityInfo();

    /**
     * 获取微信登录和支付id
     * @return
     */
    @GET("user/getIdKeys")
    Call<ResponseBody> getIdKeys();

    @FormUrlEncoded
    @POST("memberOrders/outputMoney")
    Call<ResponseBody> outputMoney(@Header("token") String token, @FieldMap ArrayMap<String, String> params);

    /**
     * 获取用户信息、视频信息、相册信息
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST("video/getUserVideoPhotoList")
    Call<ResponseBody> getUserVideoPhotoList(@Field("userId") String userId, @Header("token") String token);

    /**
     * 获取返话费活动条件
     * @param token
     * @return
     */
    @GET("buy/getFareActivityInfo")
    Call<ResponseBody> getFareActivityInfo(@Header("token") String token);

    @FormUrlEncoded
    @POST("user/getPermission")
    Call<ResponseBody> getPermission(@Field("channel") String channel);
}
