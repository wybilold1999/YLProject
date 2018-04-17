package com.cyanbirds.yljy.net.request;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.CityInfo;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by wangyb on 2017/5/17.
 * 描述：获取用户所在城市
 */

public class GetCityInfoRequest extends ResultPostExecute<CityInfo> {

    public void request() {
        Call<ResponseBody> call = AppManager.getUserService().getCityInfo();
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
            Gson gson = new Gson();
            CityInfo cityInfo = gson.fromJson(json, CityInfo.class);
            if ("1".equals(cityInfo.status) && "10000".equals(cityInfo.infocode)) {
                onPostExecute(cityInfo);
            } else {
                onErrorExecute("");
            }
        } catch (Exception e) {
            onErrorExecute("");
        }
    }

}
