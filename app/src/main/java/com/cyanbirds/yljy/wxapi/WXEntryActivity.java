package com.cyanbirds.yljy.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.eventtype.WeinXinEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI 是第三方app和微信通信的openapi接口
//	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
//		api = WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_ID, true);
//		api.handleIntent(getIntent(), this);
		handleIntent(getIntent());
	}

	private void handleIntent(Intent paramIntent) {
		if (null != AppManager.getIWXAPI()) {
			AppManager.getIWXAPI().handleIntent(paramIntent, this);
		} else {
			CSApplication.api.handleIntent(paramIntent, this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
//		api.handleIntent(intent, this);
		handleIntent(intent);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
//				ToastUtil.showMessage("goToGetMsg");
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
//				ToastUtil.showMessage("goToShowMsg");
				break;
			default:
				break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		SendAuth.Resp sendResp = (SendAuth.Resp) resp;
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				result = R.string.errcode_success;
				EventBus.getDefault().post(new WeinXinEvent(sendResp.code));
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				result = R.string.errcode_cancel;
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = R.string.errcode_deny;
				break;
			default:
				ToastUtil.showMessage(resp.transaction);
				result = R.string.errcode_unknown;
				break;
		}
		finish();
//		ToastUtil.showMessage(result);
	}
}