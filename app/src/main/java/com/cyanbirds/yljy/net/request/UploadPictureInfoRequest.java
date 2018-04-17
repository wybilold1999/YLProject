package com.cyanbirds.yljy.net.request;


import android.support.v4.util.ArrayMap;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.entity.Picture;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-04-29 16:56 GMT+8
 * @email 395044952@qq.com
 */
public class UploadPictureInfoRequest extends ResultPostExecute<List<String>> {
    public void request(final List<Picture> picturesInfo) {
        ArrayMap<String, String> params = new ArrayMap<>();
        Gson gson = new Gson();
        params.put("sessionId", AppManager.getClientUser().sessionId);
        params.put("pictures", gson.toJson(picturesInfo));
        Call<ResponseBody> call = AppManager.getUserService().uploadPic(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {
                        parserJson(response.body().string());
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
            //用户下所有图片的url
            ArrayList<String> url = new ArrayList<>();
            String data = obj.get("data").getAsString();
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();
            for(int i = 0; i < array.size(); i++){
                JsonObject jsonObject = array.get(i).getAsJsonObject();
                url.add(AppConstants.OSS_IMG_ENDPOINT + jsonObject.get("path").getAsString());
            }
            onPostExecute(url);
        } catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.data_parser_error));
        }
    }
}
