package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.CheckSmsCodeRequest;
import com.cyanbirds.yljy.net.request.UpdateUserInfoRequest;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import cn.smssdk.SMSSDK;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 
 * @ClassName:CaptchaActivity
 * @Description:注册验证码
 * @Author:wangyb
 * @Date:2015年5月11日下午4:14:15
 *
 */
public class RegisterCaptchaActivity extends BaseActivity implements
		OnClickListener {

	private EditText mSmsCode;
	private FancyButton mNext;
	private TextView mPhoneNumber;
	private TextView mCountTimer;

	private int mPhoneType;
	private String mPhone;
	private CountDownTimer timer;
	private FancyButton mReGetCap;

	private ClientUser mClientUser;
	private String mCurrrentCity;//定位到的城市

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_captcha);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupViews();
		setupData();
		setEvent();
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		mSmsCode = (EditText) findViewById(R.id.sms_code);
		mPhoneNumber = (TextView) findViewById(R.id.phone_number);
		mCountTimer = (TextView) findViewById(R.id.count_timer);
		mNext = (FancyButton) findViewById(R.id.next);
		mReGetCap = (FancyButton) findViewById(R.id.re_get_cap);
	}

	/**
	 * 设置数据
	 */
	private void setupData() {
		mPhoneType = getIntent().getIntExtra(ValueKey.INPUT_PHONE_TYPE, -1);
		mPhone = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
		mClientUser = (ClientUser) getIntent().getSerializableExtra(ValueKey.USER);
		mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);

		mPhoneNumber.setText("+86" + " " + mPhone);
		// 验证码有效日期
		timer = new CountDownTimer(90000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				mCountTimer.setText((millisUntilFinished / 1000) + "秒后可重发");
			}

			@Override
			public void onFinish() {
				mCountTimer.setEnabled(true);
				mCountTimer.setText(R.string.get_capt_cha);
				mCountTimer.setVisibility(View.GONE);
				mReGetCap.setText("获取验证码");
				mReGetCap.setVisibility(View.VISIBLE);
			}

		};
		timer.start();
	}

	/**
	 * 设置事件
	 */
	private void setEvent() {
		mNext.setOnClickListener(this);
		mReGetCap.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			if (checkInput()) {
				//验证 验证码
				ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_sms_check);
				new CheckSmsCodeTask().request(mSmsCode.getText().toString().trim(), mPhone, mPhoneType);
			}
			break;
		case R.id.count_timer:
		case R.id.re_get_cap:
			mCountTimer.setVisibility(View.VISIBLE);
			mReGetCap.setVisibility(View.GONE);
			//请求验证码
			SMSSDK.getVerificationCode("86", mPhone);
			timer.start();
			break;
		}
	}

	class CheckSmsCodeTask extends CheckSmsCodeRequest {
		@Override
		public void onPostExecute(String s) {
			ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
			Intent intent = new Intent();
			if(mPhoneType == 0){//注册
				intent.setClass(RegisterCaptchaActivity.this, RegisterSubmitActivity.class);
				intent.putExtra(ValueKey.USER, mClientUser);
				startActivity(intent);
			} else if(mPhoneType == 1){//找回密码
				intent.setClass(RegisterCaptchaActivity.this, InputNewPwdActivity.class);
				intent.putExtra(ValueKey.SMS_CODE, mSmsCode.getText().toString().trim());
				intent.putExtra(ValueKey.PHONE_NUMBER, mPhone);
				intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
				startActivity(intent);
			} else {
				AppManager.getClientUser().isCheckPhone = true;
				AppManager.getClientUser().mobile = mPhone;
				AppManager.setClientUser(AppManager.getClientUser());
				AppManager.saveUserInfo();
				new UpdateUserInfoTask().request(AppManager.getClientUser());
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
			ToastUtil.showMessage(error);
		}
	}

	class UpdateUserInfoTask extends UpdateUserInfoRequest {
		@Override
		public void onPostExecute(String s) {
			ToastUtil.showMessage(R.string.bangding_success);
			ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
			finish();
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(R.string.bangding_faile);
			ProgressDialogUtils.getInstance(RegisterCaptchaActivity.this).dismiss();
			finish();
		}
	}


	/**
	 * 验证输入
	 */
	private boolean checkInput() {
		String message = "";
		boolean bool = true;
		if (TextUtils.isEmpty(mSmsCode.getText().toString())) {
			message = getResources().getString(R.string.input_captcha);
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
