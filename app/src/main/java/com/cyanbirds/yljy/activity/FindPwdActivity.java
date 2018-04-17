package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.net.request.CheckIsRegisterByPhoneRequest;
import com.cyanbirds.yljy.utils.CheckUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2016/4/26.
 */
public class FindPwdActivity extends BaseActivity {
    @BindView(R.id.tips)
    TextView mTips;
    @BindView(R.id.phone_num)
    EditText phoneNum;
    @BindView(R.id.next)
    FancyButton next;

    private int mInputPhoneType;
    private String mCurrrentCity;//定位到的城市

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        mInputPhoneType = getIntent().getIntExtra(ValueKey.INPUT_PHONE_TYPE, -1);
        mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);
        setupView();
    }

    private void setupView() {
        if (mInputPhoneType == 2) {//绑定手机
            mTips.setVisibility(View.INVISIBLE);
            getSupportActionBar().setTitle(R.string.bangding_phone);
        }
    }

    @OnClick(R.id.next)
    public void onClick() {
        if(checkInput()){
            new CheckPhoneIsRegisterTask().request(phoneNum.getText().toString().trim());
        }
    }

    class CheckPhoneIsRegisterTask extends CheckIsRegisterByPhoneRequest {
        @Override
        public void onPostExecute(Boolean s) {
            if(s){
                SMSSDK.getVerificationCode("86", phoneNum.getText().toString().trim());
                Intent intent = new Intent(FindPwdActivity.this, RegisterCaptchaActivity.class);
                intent.putExtra(ValueKey.INPUT_PHONE_TYPE, mInputPhoneType);
                intent.putExtra(ValueKey.PHONE_NUMBER, phoneNum.getText().toString().trim());
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                startActivity(intent);
                finish();
            } else {
                ToastUtil.showMessage(R.string.phone_un_register);
            }
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    /**
     * 验证输入
     */
    private boolean checkInput() {
        String message = "";
        boolean bool = true;
        if (TextUtils.isEmpty(phoneNum.getText().toString())) {
            message = getResources().getString(R.string.input_phone);
            bool = false;
        } else if (!CheckUtil.isMobileNO(phoneNum.getText().toString())) {
            message = getResources().getString(
                    R.string.input_phone_number_error);
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
