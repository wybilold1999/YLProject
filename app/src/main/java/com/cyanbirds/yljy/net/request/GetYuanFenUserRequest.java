package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.YuanFenModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-04-29 16:56 GMT+8
 * @email 395044952@qq.com
 */
public class GetYuanFenUserRequest extends ResultPostExecute<List<YuanFenModel>> {
    public void request(int pageNo, int pageSize) {
        ArrayMap<String, Integer> map = new ArrayMap<>(2);
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        Call<ResponseBody> call = AppManager.getUserService().getYuanFenUser(AppManager.getClientUser().sessionId, map);
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
            Type listType = new TypeToken<ArrayList<YuanFenModel>>() {
            }.getType();
            Gson gson = new Gson();
            List<YuanFenModel> models = gson.fromJson(dataString, listType);
            onPostExecute(models);
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.recommend));
        }
    }
}
