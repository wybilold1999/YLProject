package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.AESOperator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * 作者：wangyb
 * 时间：2016/9/16 10:35
 * 描述：
 */
public class PublishDynamicRequest extends ResultPostExecute<String> {
	public void request(String pictures, String content){
		ArrayMap<String, String> params = new ArrayMap<>();
		params.put("pictures", pictures);
		params.put("content", content);
		Call<ResponseBody> call = AppManager.getDynamicService().publishDynamic(AppManager.getClientUser().sessionId, params);
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
			onPostExecute(decryptData);
		} catch (Exception e) {
            onErrorExecute(CSApplication.getInstance().getResources()
                    .getString(R.string.data_parser_error));
		}
	}
}
