package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-04-28 09:57 GMT+8
 * @email 395044952@qq.com
 */
public class UpdateUserInfoRequest extends ResultPostExecute<String> {

    public void request(final ClientUser clientUser){
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("sex", clientUser.sex);
        params.put("nickName", clientUser.user_name);
        params.put("faceurl", clientUser.face_url);
        if(!TextUtils.isEmpty(clientUser.personality_tag)){
            params.put("personalityTag", clientUser.personality_tag);
        }
        if(!TextUtils.isEmpty(clientUser.part_tag)){
            params.put("partTag", clientUser.part_tag);
        }
        if(!TextUtils.isEmpty(clientUser.intrest_tag)){
            params.put("intrestTag", clientUser.intrest_tag);
        }
        params.put("age", String.valueOf(clientUser.age));
        params.put("signature", clientUser.signature == null ? "" : clientUser.signature);
        params.put("qq", clientUser.qq_no == null ? "" : clientUser.qq_no);
        params.put("wechat", clientUser.weixin_no == null ? "" : clientUser.weixin_no);
        params.put("publicSocialNumber", String.valueOf(clientUser.publicSocialNumber));
        params.put("emotionStatus", clientUser.state_marry == null ? "" : clientUser.state_marry);
        params.put("tall", clientUser.tall == null ? "" : clientUser.tall);
        params.put("weight", clientUser.weight == null ? "" : clientUser.weight);
        params.put("constellation", clientUser.constellation == null ? "" : clientUser.constellation);
        params.put("occupation", clientUser.occupation == null ? "" : clientUser.occupation);
        params.put("education", clientUser.education == null ? "" : clientUser.education);
        params.put("purpose", clientUser.purpose == null ? "" : clientUser.purpose);
        params.put("loveWhere", clientUser.love_where == null ? "" : clientUser.love_where);
        params.put("doWhatFirst", clientUser.do_what_first == null ? "" : clientUser.do_what_first);
        params.put("conception", clientUser.conception == null ? "" : clientUser.conception);
        params.put("isDownloadVip", String.valueOf(clientUser.is_download_vip));
        params.put("goldNum", String.valueOf(clientUser.gold_num));
        Call<ResponseBody> call = AppManager.getUserService().updateUserInfo(AppManager.getClientUser().sessionId, params);
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

    private void parseJson(String json) {
        try {
            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                onErrorExecute(CSApplication.getInstance().getResources()
                        .getString(R.string.save_fail));
                return;
            }
            onPostExecute(CSApplication.getInstance().getResources()
                        .getString(R.string.save_success));
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.save_fail));
        }
    }
}
