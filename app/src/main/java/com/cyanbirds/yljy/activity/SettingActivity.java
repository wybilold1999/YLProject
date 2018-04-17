package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.db.ConversationSqlManager;
import com.cyanbirds.yljy.db.IMessageDaoManager;
import com.cyanbirds.yljy.db.MyGoldDaoManager;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.manager.NotificationManager;
import com.cyanbirds.yljy.net.request.LogoutRequest;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author: wangyb
 * @datetime: 2016-01-23 16:58 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.switch_msg)
    SwitchCompat mSwitchMsg;
    @BindView(R.id.switch_msg_content)
    SwitchCompat mSwitchMsgContent;
    @BindView(R.id.switch_voice)
    SwitchCompat mSwitchVoice;
    @BindView(R.id.switch_vibrate)
    SwitchCompat mSwitchVibrate;
    @BindView(R.id.is_bangding_phone)
    TextView mIsBangdingPhone;
    @BindView(R.id.banding_phone_lay)
    RelativeLayout mBandingPhoneLay;
    @BindView(R.id.modify_pwd_lay)
    RelativeLayout mModifyPwdLay;
    @BindView(R.id.get_fare_info_lay)
    RelativeLayout mGetFareInfoLay;
    @BindView(R.id.quit)
    RelativeLayout mQuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.setting);
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
    }

    private void setupEvent() {
    }

    private void setupData() {
        if (AppManager.getClientUser().isCheckPhone) {
            mIsBangdingPhone.setText(R.string.already_bangding);
        } else {
            mIsBangdingPhone.setText(R.string.un_bangding);
        }
        if (PreferencesUtils.getNewMessageNotice(this)) {
            mSwitchMsg.setChecked(true);
        } else {
            mSwitchMsg.setChecked(false);
        }
        if (PreferencesUtils.getShowMessageInfo(this)) {
            mSwitchMsgContent.setChecked(true);
        } else {
            mSwitchMsgContent.setChecked(false);
        }
        if (PreferencesUtils.getNoticeVoice(this)) {
            mSwitchVoice.setChecked(true);
        } else {
            mSwitchVoice.setChecked(false);
        }
        if (PreferencesUtils.getNoticeShock(this)) {
            mSwitchVibrate.setChecked(true);
        } else {
            mSwitchVibrate.setChecked(false);
        }
        if (PreferencesUtils.getIsHasGetFareActivity(this)) {
            mGetFareInfoLay.setVisibility(View.VISIBLE);
        } else {
            mGetFareInfoLay.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.switch_msg, R.id.switch_msg_content, R.id.switch_voice, R.id.switch_vibrate,
            R.id.banding_phone_lay, R.id.modify_pwd_lay, R.id.get_fare_info_lay, R.id.quit})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.switch_msg:
                if (PreferencesUtils.getNewMessageNotice(this)) {
                    mSwitchMsg.setChecked(false);
                    PreferencesUtils.setNewMessageNotice(this, false);
                } else {
                    mSwitchMsg.setChecked(true);
                    PreferencesUtils.setNewMessageNotice(this, true);
                }
                break;
            case R.id.switch_msg_content:
                if (PreferencesUtils.getShowMessageInfo(this)) {
                    mSwitchMsgContent.setChecked(false);
                    PreferencesUtils.setShowMessageInfo(this, false);
                } else {
                    mSwitchMsgContent.setChecked(true);
                    PreferencesUtils.setShowMessageInfo(this, true);
                }
                break;
            case R.id.switch_voice:
                if (PreferencesUtils.getNoticeVoice(this)) {
                    mSwitchVoice.setChecked(false);
                    PreferencesUtils.setNoticeVoice(this, false);
                } else {
                    mSwitchVoice.setChecked(true);
                    PreferencesUtils.setNoticeVoice(this, true);
                }
                break;
            case R.id.switch_vibrate:
                if (PreferencesUtils.getNoticeShock(this)) {
                    mSwitchVibrate.setChecked(false);
                    PreferencesUtils.setNoticeShock(this, false);
                } else {
                    mSwitchVibrate.setChecked(true);
                    PreferencesUtils.setNoticeShock(this, true);
                }
                break;
            case R.id.banding_phone_lay:
                //0=注册1=找回密码2=验证绑定手机
                intent.setClass(this, FindPwdActivity.class);
                intent.putExtra(ValueKey.INPUT_PHONE_TYPE, 2);
                startActivity(intent);
                finish();
                break;
            case R.id.modify_pwd_lay:
                intent.setClass(this, ModifyPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.get_fare_info_lay:
                intent.setClass(this, GetTelFareRuleActivity.class);
                startActivity(intent);
                break;
            case R.id.quit:
                showQuitDialog();
                break;
        }
    }

    class LogoutTask extends LogoutRequest {
        @Override
        public void onPostExecute(String s) {
            ProgressDialogUtils.getInstance(SettingActivity.this).dismiss();
            MobclickAgent.onProfileSignOff();
            release();
            NotificationManager.getInstance().cancelNotification();
            finishAll();
            PreferencesUtils.setIsLogin(SettingActivity.this, false);
            Intent intent = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(
                            getBaseContext().getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        @Override
        public void onErrorExecute(String error) {
            ProgressDialogUtils.getInstance(SettingActivity.this).dismiss();
            ToastUtil.showMessage(error);
        }
    }

    /**
     * 显示退出dialog
     */
    private void showQuitDialog() {
        new AlertDialog.Builder(this)
                .setItems(R.array.quit_items,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        ProgressDialogUtils.getInstance(SettingActivity.this).show(R.string.dialog_logout_tips);
                                        new LogoutTask().request();
                                        break;
                                    case 1:
                                        exitApp();
                                        break;
                                }
                            }
                        }).setTitle(R.string.quit).show();
    }


    /**
     * 释放数据库
     */
    private static void release() {
        IMessageDaoManager.reset();
        ConversationSqlManager.reset();
        MyGoldDaoManager.reset();
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
