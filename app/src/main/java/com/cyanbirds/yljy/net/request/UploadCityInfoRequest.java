package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.cyanbirds.yljy.utils.CheckUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * 作者：wangyb
 * 时间：2017/5/13 14:47
 * 描述：
 */
public class UploadCityInfoRequest extends ResultPostExecute<String> {
	public void request(final String city, final String latitude, final String longitude){
		ArrayMap<String, String> params = new ArrayMap<>();
		params.put("channel", CheckUtil.getAppMetaData(CSApplication.getInstance(), "UMENG_CHANNEL"));
		if (!TextUtils.isEmpty(city)) {
			params.put("currentCity", city);
		} else {
			params.put("currentCity", "");
		}
		if (!TextUtils.isEmpty(latitude)) {
			params.put("latitude", latitude);
		} else {
			params.put("latitude", "");
		}
		if (!TextUtils.isEmpty(longitude)) {
			params.put("longitude", longitude);
		} else {
			params.put("longitude", "");
		}
		Call<ResponseBody> call = AppManager.getUserService().uploadCityInfo(params, AppManager.getClientUser().sessionId);
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
			onPostExecute(json);
		} catch (Exception e) {
			onErrorExecute(CSApplication.getInstance().getResources()
					.getString(R.string.data_parser_error));
		}
	}
}
