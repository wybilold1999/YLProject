package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.helper.IMChattingHelper;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.RegisterRequest;
import com.cyanbirds.yljy.utils.AESEncryptorUtil;
import com.cyanbirds.yljy.utils.CheckUtil;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 
 * @Description:注册提交
 * @Author:wangyb
 * @Date:2015年5月12日上午11:43:42
 *
 */
public class RegisterSubmitActivity extends BaseActivity implements
		OnClickListener {

	private EditText mNickname;
	private EditText mPassword;
	private EditText mConfirmPassword;
	private FancyButton mRegister;

	private ClientUser mClientUser;
	private String channelId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_submit);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setEvent();
		setupData();
		channelId = CheckUtil.getAppMetaData(this, "UMENG_CHANNEL");
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mNickname = (EditText) findViewById(R.id.nickname);
		mPassword = (EditText) findViewById(R.id.password);
		mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
		mRegister = (FancyButton) findViewById(R.id.register);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mClientUser = (ClientUser) getIntent().getSerializableExtra(ValueKey.USER);
	}

	/**
	 * 设置事件
	 */
	private void setEvent() {
		mRegister.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			if (checkInput()) {
				mClientUser.user_name = mNickname.getText().toString().trim();
				String securityPwd = AESEncryptorUtil.crypt(mPassword.getText().toString().trim(),
						AppConstants.SECURITY_KEY);
				mClientUser.userPwd = securityPwd;
				new RegisterTask().request(mClientUser, channelId);
				ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_register_login);
			}
			break;
		}
	}

	class RegisterTask extends RegisterRequest {
		@Override
		public void onPostExecute(ClientUser clientUser) {
			ProgressDialogUtils.getInstance(RegisterSubmitActivity.this).dismiss();
			hideSoftKeyboard();
			MobclickAgent.onProfileSignIn(String.valueOf(AppManager
					.getClientUser().userId));
			clientUser.userPwd = mClientUser.userPwd;
			clientUser.currentCity = mClientUser.currentCity;
			AppManager.setClientUser(clientUser);
			AppManager.saveUserInfo();
			AppManager.getClientUser().loginTime = System.currentTimeMillis();
			PreferencesUtils.setLoginTime(RegisterSubmitActivity.this, System.currentTimeMillis());
			IMChattingHelper.getInstance().sendInitLoginMsg();
			Intent intent = new Intent(RegisterSubmitActivity.this, MainActivity.class);
			startActivity(intent);
			finishAll();
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(RegisterSubmitActivity.this).dismiss();
			ToastUtil.showMessage(error);
		}
	}


	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mNickname.getText().toString())) {
			message = getResources().getString(R.string.input_nickname);
			bool = false;
		} else if (TextUtils.isEmpty(mPassword.getText().toString())) {
			message = getResources().getString(R.string.input_password);
			bool = false;
		} else if (!mPassword.getText().toString()
				.equals(mConfirmPassword.getText().toString())) {
			message = getResources().getString(
					R.string.input_password_different);
			bool = false;
		}
		if (!bool)
			ToastUtil.showMessage(message);
		return bool;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
		MobclickAgent.onPause(this);
	}
	
}
