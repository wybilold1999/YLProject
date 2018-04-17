package com.cyanbirds.yljy.net.request;

import android.support.v4.util.ArrayMap;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.base.ResultPostExecute;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * 
 * @ClassName:CheckSmsCodeRequest
 * @Description:验证短信验证码
 * @Author:wangyb
 * @Date:2015年5月11日下午4:35:22
 *
 */
public class CheckSmsCodeRequest extends ResultPostExecute<String> {

	public void request(final String code, final String phoneNum, final int mPhoneType) {
		ArrayMap<String, String> params = new ArrayMap<>();
		params.put("phone", phoneNum);
		params.put("zone", "86");
		params.put("code", code);
		params.put("type", String.valueOf(mPhoneType)); //0:注册  1:找回密码
		params.put("device", "android");
		Call<ResponseBody> call = AppManager.getUserService().checkSmsCode(AppManager.getClientUser().sessionId, params);
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
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		if (obj.get("status").getAsInt() == 200) {
			onPostExecute("验证成功");
		} else {
			onErrorExecute("验证失败");
		}
	}
}
