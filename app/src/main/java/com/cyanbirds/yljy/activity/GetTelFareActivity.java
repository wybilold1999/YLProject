package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.utils.CheckUtil;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by wangyb on 2018/2/23.
 * 领取话费界面(输入手机号码)
 */

public class GetTelFareActivity extends BaseActivity {
    
    @BindView(R.id.phone_num)
    EditText mPhoneNum;
    @BindView(R.id.btn_get_fare)
    FancyButton mBtnGetFare;

    private static Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_tel_fare);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
    }

    @OnClick(R.id.btn_get_fare)
    public void onViewClicked() {
        if (checkInput()) {
            ProgressDialogUtils.getInstance(this).show(R.string.dialog_geting_tel_fare);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProgressDialogUtils.getInstance(GetTelFareActivity.this).dismiss();
                    PreferencesUtils.setLoginCount(GetTelFareActivity.this, 0);
                    PreferencesUtils.setIsCanGetFare(GetTelFareActivity.this, false);
                    showDialog();
                }
            }, 3000);
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.get_tel_fare_success);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    private boolean checkInput() {
        String message = "";
        boolean bool = true;
        if (TextUtils.isEmpty(mPhoneNum.getText().toString())) {
            message = getResources().getString(R.string.input_phone);
            bool = false;
        } else if (!CheckUtil.isMobileNO(mPhoneNum.getText().toString())) {
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
