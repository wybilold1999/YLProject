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
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.FindPwdRequest;
import com.cyanbirds.yljy.net.request.UserLoginRequest;
import com.cyanbirds.yljy.utils.AESEncryptorUtil;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 
 * @Description:输入新的密码
 * @Author:wangyb
 * @Date:2015年5月12日上午11:43:42
 *
 */
public class InputNewPwdActivity extends BaseActivity implements
		OnClickListener {

	private EditText mPassword;
	private EditText mConfirmPassword;
	private FancyButton mLogin;

	private String mPhone;
	private String mSmsCode;
	private String mCurrrentCity;//定位到的城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_newpwd);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupData();
		setupEvent();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mPassword = (EditText) findViewById(R.id.password);
		mConfirmPassword = (EditText) findViewById(R.id.confirm_password);
		mLogin = (FancyButton) findViewById(R.id.login);
	}

	/**
	 * 设置事件
	 */
	private void setupEvent() {
		mLogin.setOnClickListener(this);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mPhone = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
		mSmsCode = getIntent().getStringExtra(ValueKey.SMS_CODE);
		mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login:
				if (checkInput()) {
					String cryptPwd = AESEncryptorUtil.crypt(
							mConfirmPassword.getText().toString().trim(), AppConstants.SECURITY_KEY);
					AppManager.getClientUser().userPwd = cryptPwd;
					ProgressDialogUtils.getInstance(this).show(R.string.dialog_modifying);
					new ModifyPwdTask().request(cryptPwd, mPhone, mSmsCode);
				}
				break;
		}
	}

	class ModifyPwdTask extends FindPwdRequest {
		@Override
		public void onPostExecute(String s) {
			ProgressDialogUtils.getInstance(InputNewPwdActivity.this).dismiss();
			ProgressDialogUtils.getInstance(InputNewPwdActivity.this).show(R.string.dialog_request_login);
			new UserLoginTask().request(mPhone, AppManager.getClientUser().userPwd, mCurrrentCity);
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(InputNewPwdActivity.this).dismiss();
			ToastUtil.showMessage(error);
		}
	}

	class UserLoginTask extends UserLoginRequest {
		@Override
		public void onPostExecute(ClientUser clientUser) {
			ProgressDialogUtils.getInstance(InputNewPwdActivity.this).dismiss();
			hideSoftKeyboard();
			if(!TextUtils.isEmpty(AppManager.getClientUser().face_local)){
				clientUser.face_local = AppManager.getClientUser().face_local;
			}
			clientUser.currentCity = mCurrrentCity;
			AppManager.setClientUser(clientUser);
			AppManager.saveUserInfo();
			Intent intent = new Intent();
			intent.setClass(InputNewPwdActivity.this, MainActivity.class);
			startActivity(intent);
			finishAll();
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(InputNewPwdActivity.this).dismiss();
			ToastUtil.showMessage(error);
		}
	}

	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mPassword.getText().toString())) {
			message = getResources().getString(R.string.input_new_password);
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
