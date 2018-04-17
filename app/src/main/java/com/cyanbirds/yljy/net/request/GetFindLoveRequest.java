package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Administrator on 2016/5/1.
 */
public class GetFindLoveRequest extends ResultPostExecute<List<ClientUser>> {
    public void request(final int pageNo, final int pageSize,
                        final String gender, final String mUserScopeType) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("pageNo", String.valueOf(pageNo));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("gender", gender);
        params.put("user_scope_type", mUserScopeType);
        Call<ResponseBody> call = AppManager.getUserService().getFindLoveInfo(AppManager.getClientUser().sessionId, params);
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
            String decryptData = AESOperator.getInstance().decrypt(json);
            JsonObject obj = new JsonParser().parse(decryptData).getAsJsonObject();
            int code = obj.get("code").getAsInt();
            if (code == 1) {
                onErrorExecute(obj.get("msg").getAsString());
                return;
            } else if(code == 3){
                onErrorExecute(obj.get("msg").getAsString());
                return;
            }
            String strData = obj.get("data").getAsString();
            JsonArray data = new JsonParser().parse(strData).getAsJsonArray();
            List<ClientUser> userList = new ArrayList<>();
            for(int i = 0; i < data.size(); i++){
                ClientUser clientUser = new ClientUser();
                JsonObject jsonObject = data.get(i).getAsJsonObject();
                clientUser.userId = jsonObject.get("uid").getAsString();
                clientUser.sex = jsonObject.get("sex").getAsInt() == 1 ? "男" : "女";
                clientUser.user_name = jsonObject.get("nickname").getAsString();
                clientUser.is_vip = jsonObject.get("isVip").getAsBoolean();
                clientUser.state_marry = jsonObject.get("emotionStatus").getAsString();
                clientUser.face_url = jsonObject.get("faceUrl").getAsString();
                clientUser.age = jsonObject.get("age").getAsInt();
                clientUser.signature = jsonObject.get("signature").getAsString();
                clientUser.constellation = jsonObject.get("constellation").getAsString();
                clientUser.city = jsonObject.get("city").getAsString();
                Object o = jsonObject.get("distance");
                if (!(o instanceof JsonNull)) {
                    clientUser.distance = jsonObject.get("distance").getAsString();
                }
                clientUser.personality_tag = jsonObject.get("personalityTag").getAsString();
                userList.add(clientUser);
            }
            onPostExecute(userList);
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.data_parser_error));
        }
    }
}
