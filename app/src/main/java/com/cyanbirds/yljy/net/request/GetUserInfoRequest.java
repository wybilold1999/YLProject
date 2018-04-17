package com.cyanbirds.yljy.net.request;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-04-29 16:56 GMT+8
 * @email 395044952@qq.com
 */
public class GetUserInfoRequest extends ResultPostExecute<ClientUser> {
    public void request(final String userId) {
        Call<ResponseBody> call = AppManager.getUserService().getUserInfo(
                AppManager.getClientUser().sessionId, userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()){
                        String data = response.body().string();
                        parserJson(data);
                    } else {
                        onErrorExecute(CSApplication.getInstance()
                                .getResources()
                                .getString(R.string.network_requests_error));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onErrorExecute(CSApplication.getInstance().getResources()
                            .getString(R.string.data_load_error));
                } finally {
                    response.body().close();
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

    private void parserJson(String json){
        try {
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code != 0) {
                onErrorExecute(CSApplication.getInstance().getResources()
                        .getString(R.string.data_load_error));
                return;
            }
            String dataString = obj.get("data").getAsString();
            JsonObject data = new JsonParser().parse(dataString).getAsJsonObject();
            ClientUser clientUser = new ClientUser();
            clientUser.userId = data.get("uid").getAsString();
            clientUser.sex = data.get("sex").getAsInt() == 1 ? "男" : "女";
            clientUser.user_name = data.get("nickname").getAsString();
            clientUser.city = data.get("city").getAsString();
            clientUser.distance = data.get("distance").getAsString();
            clientUser.tall = data.get("heigth").getAsString();
            clientUser.weight = data.get("weight").getAsString();
            clientUser.is_vip = data.get("isVip").getAsBoolean();
            clientUser.isFollow = data.get("isFollow").getAsBoolean();
            clientUser.state_marry = data.get("emotionStatus").getAsString();
            clientUser.face_url = data.get("faceUrl").getAsString();
            clientUser.age = data.get("age").getAsInt();
            clientUser.signature = data.get("signature").getAsString();
            clientUser.constellation = data.get("constellation").getAsString();
            clientUser.qq_no = data.get("qq").getAsString();
            clientUser.weixin_no = data.get("wechat").getAsString();
            clientUser.occupation = data.get("occupation").getAsString();
            clientUser.education = data.get("education").getAsString();
            clientUser.intrest_tag = data.get("intrestTag").getAsString();
            clientUser.personality_tag = data.get("personalityTag").getAsString();
            clientUser.part_tag = data.get("partTag").getAsString();
            clientUser.purpose = data.get("purpose").getAsString();
            clientUser.love_where = data.get("loveWhere").getAsString();
            clientUser.do_what_first = data.get("doWhatFirst").getAsString();
            clientUser.conception = data.get("conception").getAsString();
            clientUser.imgUrls = data.get("picturesUrls").getAsString();
            clientUser.gifts = data.get("gifts").getAsString();
            clientUser.latitude = data.get("latitude").getAsString();
            clientUser.longitude = data.get("longitude").getAsString();
            onPostExecute(clientUser);
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.data_parser_error));
        }
    }
}
