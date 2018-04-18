package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-04-29 11:01 GMT+8
 * @email 395044952@qq.com
 */
public class RegisterRequest extends ResultPostExecute<ClientUser> {
    public void request(final ClientUser clientUser, String channel){
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("upwd", clientUser.userPwd);
        params.put("nickname", clientUser.user_name);
        params.put("phone", clientUser.mobile);
        params.put("sex", "男".equals(clientUser.sex) ? "1" : "0");
        params.put("age", String.valueOf(clientUser.age));
        params.put("channel", channel);
        params.put("regDeviceName", AppManager.getDeviceName());
        params.put("regVersion", String.valueOf(AppManager.getVersionCode()));
        params.put("regPlatform", "phone");
        params.put("reg_the_way", "0");
        params.put("regSystemVersion", AppManager.getDeviceSystemVersion());
        params.put("deviceId", AppManager.getDeviceId());
        if (!TextUtils.isEmpty(clientUser.currentCity)) {
            params.put("currentCity", clientUser.currentCity);
        } else {
            params.put("currentCity", "");
        }
        params.put("province", PreferencesUtils.getCurrentProvince(CSApplication.getInstance()));
        params.put("latitude", PreferencesUtils.getLatitude(CSApplication.getInstance()));
        params.put("longitude", PreferencesUtils.getLongitude(CSApplication.getInstance()));
        params.put("clientName", "yljy");
        Call<ResponseBody> call = AppManager.getUserService().userRegister(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        parseJson(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        response.body().close();
                    }
                } else {
                    onErrorExecute(CSApplication.getInstance()
                            .getResources()
                            .getString(R.string.network_requests_error));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onErrorExecute(CSApplication.getInstance()
                        .getResources()
                        .getString(R.string.network_requests_error));
            }
        });
    }

    private void parseJson(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                onErrorExecute(CSApplication.getInstance().getResources()
                        .getString(R.string.data_load_error));
                return;
            }
            JsonObject data = obj.get("data").getAsJsonObject();
            ClientUser clientUser = new ClientUser();
            clientUser.userId = data.get("uid").getAsString();
            clientUser.userPwd = data.get("upwd").getAsString();
            clientUser.sex = data.get("sex").getAsInt() == 1 ? "男" : "女";
            clientUser.user_name = data.get("nickname").getAsString();
            clientUser.tall = data.get("heigth").getAsString();
            clientUser.weight = data.get("weight").getAsString();
            clientUser.isCheckPhone = data.get("isCheckPhone").getAsBoolean();
            clientUser.is_vip = data.get("isVip").getAsBoolean();
            clientUser.is_download_vip = data.get("isDownloadVip").getAsBoolean();
            clientUser.gold_num = data.get("goldNum").getAsInt();
            JsonObject jsonObject = data.get("showClient").getAsJsonObject();
            clientUser.isShowVip = jsonObject.get("isShowVip").getAsBoolean();
            clientUser.isShowDownloadVip = jsonObject.get("isShowDownloadVip").getAsBoolean();
            clientUser.isShowGold = jsonObject.get("isShowGold").getAsBoolean();
            clientUser.isShowLovers = jsonObject.get("isShowLovers").getAsBoolean();
            clientUser.isShowVideo = jsonObject.get("isShowVideo").getAsBoolean();
            clientUser.isShowMap = jsonObject.get("isShowMap").getAsBoolean();
            clientUser.isShowRpt = jsonObject.get("isShowRpt").getAsBoolean();
            clientUser.isShowTd = jsonObject.get("isShowTd").getAsBoolean();
            clientUser.isShowAppointment = jsonObject.get("isShowAppointment").getAsBoolean();
            clientUser.state_marry = data.get("emotionStatus").getAsString();
            clientUser.face_url = data.get("faceUrl").getAsString();
            clientUser.age = data.get("age").getAsInt();
            clientUser.signature = data.get("signature").getAsString();
            clientUser.constellation = data.get("constellation").getAsString();
            clientUser.distance = data.get("distance").getAsString();
            clientUser.occupation = data.get("occupation").getAsString();
            clientUser.education = data.get("education").getAsString();
            clientUser.city = data.get("city").getAsString();
            clientUser.intrest_tag = data.get("intrestTag").getAsString();
            clientUser.personality_tag = data.get("personalityTag").getAsString();
            clientUser.part_tag = data.get("partTag").getAsString();
            clientUser.purpose = data.get("purpose").getAsString();
            clientUser.love_where = data.get("loveWhere").getAsString();
            clientUser.do_what_first = data.get("doWhatFirst").getAsString();
            clientUser.conception = data.get("conception").getAsString();
            clientUser.mobile = data.get("phone") == null ? "" : data.get("phone").getAsString();
            clientUser.sessionId = data.get("sessionId").getAsString();
            clientUser.versionCode = data.get("versionCode").getAsInt();
            clientUser.apkUrl = data.get("apkUrl").getAsString();
            clientUser.versionUpdateInfo = data.get("versionUpdateInfo").getAsString();
            clientUser.isForceUpdate = data.get("isForceUpdate").getAsBoolean();
            onPostExecute(clientUser);
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.data_parser_error));
        }
    }
}
