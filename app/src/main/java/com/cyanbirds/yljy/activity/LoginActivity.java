package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.ShowClient;
import com.cyanbirds.yljy.eventtype.LocationEvent;
import com.cyanbirds.yljy.eventtype.WeinXinEvent;
import com.cyanbirds.yljy.helper.IMChattingHelper;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.DownloadFileRequest;
import com.cyanbirds.yljy.net.request.QqLoginRequest;
import com.cyanbirds.yljy.net.request.UserLoginRequest;
import com.cyanbirds.yljy.net.request.WXLoginRequest;
import com.cyanbirds.yljy.utils.AESEncryptorUtil;
import com.cyanbirds.yljy.utils.CheckUtil;
import com.cyanbirds.yljy.utils.FileAccessorUtils;
import com.cyanbirds.yljy.utils.Md5Util;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.cyanbirds.yljy.utils.Util;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Administrator on 2016/4/23.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    EditText loginAccount;
    EditText loginPwd;
    FancyButton btnLogin;
    TextView forgetPwd;
    ImageView weiXinLogin;
    ImageView qqLogin;

    public static Tencent mTencent;
    private UserInfo mInfo;
    private String token;
    private String openId;

    private String mPhoneNum;
    private String channelId;
    private boolean activityIsRunning;
    private String mCurrrentCity;//定位到的城市
    private String curLat;
    private String curLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        channelId = CheckUtil.getAppMetaData(this, "UMENG_CHANNEL");
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
        loginAccount = (EditText) findViewById(R.id.login_account);
        loginPwd = (EditText) findViewById(R.id.login_pwd);
        btnLogin = (FancyButton) findViewById(R.id.btn_login);
        forgetPwd = (TextView) findViewById(R.id.forget_pwd);
        weiXinLogin = (ImageView) findViewById(R.id.weixin_login);
        qqLogin = (ImageView) findViewById(R.id.qq_login);
        if (AppConstants.IS_SHOW_WECHAT_LOGO) {
            weiXinLogin.setVisibility(View.VISIBLE);
        } else {
            weiXinLogin.setVisibility(View.GONE);
        }
    }

    private void setupEvent() {
        btnLogin.setOnClickListener(this);
        forgetPwd.setOnClickListener(this);
        qqLogin.setOnClickListener(this);
        weiXinLogin.setOnClickListener(this);
    }

    private void setupData(){
        if (mTencent == null) {
            mTencent = Tencent.createInstance(AppConstants.mAppid, this);
        }
        mPhoneNum = getIntent().getStringExtra(ValueKey.PHONE_NUMBER);
        if(!TextUtils.isEmpty(mPhoneNum)){
            loginAccount.setText(mPhoneNum);
            loginAccount.setSelection(mPhoneNum.length());
        }
        mCurrrentCity = getIntent().getStringExtra(ValueKey.LOCATION);
        curLat = getIntent().getStringExtra(ValueKey.LATITUDE);
        curLon = getIntent().getStringExtra(ValueKey.LONGITUDE);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.btn_login:
                if(checkInput()){
                    String cryptLoginPwd = AESEncryptorUtil.crypt(loginPwd.getText().toString().trim(),
                            AppConstants.SECURITY_KEY);
                    ProgressDialogUtils.getInstance(this).show(R.string.dialog_request_login);
                    new UserLoginTask().request(loginAccount.getText().toString().trim(),
                            cryptLoginPwd, mCurrrentCity);
                }
                break;
            case R.id.forget_pwd:
                //0=注册1=找回密码2=验证绑定手机
                intent.setClass(this, FindPwdActivity.class);
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.INPUT_PHONE_TYPE, 1);
                startActivity(intent);
                break;
            case R.id.qq_login:
                ProgressDialogUtils.getInstance(this).show(R.string.wait);
                if (!mTencent.isSessionValid() &&
                        mTencent.getQQToken().getOpenId() == null) {
                    mTencent.login(this, "all", loginListener);
                }
                break;
            case R.id.weixin_login:
                ProgressDialogUtils.getInstance(this).show(R.string.wait);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                if (null != AppManager.getIWXAPI()) {
                    AppManager.getIWXAPI().sendReq(req);
                } else {
                    CSApplication.api.sendReq(req);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCity(LocationEvent event) {
        mCurrrentCity = event.city;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weiXinLogin(WeinXinEvent event) {
        ProgressDialogUtils.getInstance(LoginActivity.this).show(R.string.dialog_request_login);
        new WXLoginTask().request(event.code, channelId, mCurrrentCity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getPermission(ShowClient showClient) {
        if (showClient.isShowGold) {
            weiXinLogin.setVisibility(View.VISIBLE);
        } else {
            weiXinLogin.setVisibility(View.GONE);
        }
    }

    class WXLoginTask extends WXLoginRequest {
        @Override
        public void onPostExecute(ClientUser clientUser) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
            MobclickAgent.onProfileSignIn(String.valueOf(AppManager
                    .getClientUser().userId));
            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                    Md5Util.md5(clientUser.face_url) + ".jpg");
            if(!faceLocalFile.exists()
                    && !TextUtils.isEmpty(clientUser.face_url)){
                new DownloadPortraitTask().request(clientUser.face_url,
                        FileAccessorUtils.FACE_IMAGE,
                        Md5Util.md5(clientUser.face_url) + ".jpg");
            } else {
                clientUser.face_local = faceLocalFile.getAbsolutePath();
            }
            clientUser.currentCity = mCurrrentCity;
            clientUser.latitude = curLat;
            clientUser.longitude = curLon;
            AppManager.setClientUser(clientUser);
            AppManager.saveUserInfo();
            AppManager.getClientUser().loginTime = System.currentTimeMillis();
            PreferencesUtils.setLoginTime(LoginActivity.this, System.currentTimeMillis());
            IMChattingHelper.getInstance().sendInitLoginMsg();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finishAll();
        }

        @Override
        public void onErrorExecute(String error) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
            ToastUtil.showMessage(error);
        }
    }

    class UserLoginTask extends UserLoginRequest {
        @Override
        public void onPostExecute(ClientUser clientUser) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
            hideSoftKeyboard();
            MobclickAgent.onProfileSignIn(String.valueOf(AppManager
                    .getClientUser().userId));
            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                    Md5Util.md5(clientUser.face_url) + ".jpg");
            if(!faceLocalFile.exists()
                    && !TextUtils.isEmpty(clientUser.face_url)){
                new DownloadPortraitTask().request(clientUser.face_url,
                        FileAccessorUtils.FACE_IMAGE,
                        Md5Util.md5(clientUser.face_url) + ".jpg");
            } else {
                clientUser.face_local = faceLocalFile.getAbsolutePath();
            }
            clientUser.currentCity = mCurrrentCity;
            clientUser.latitude = curLat;
            clientUser.longitude = curLon;
            AppManager.setClientUser(clientUser);
            AppManager.saveUserInfo();
            AppManager.getClientUser().loginTime = System.currentTimeMillis();
            PreferencesUtils.setLoginTime(LoginActivity.this, System.currentTimeMillis());
            IMChattingHelper.getInstance().sendInitLoginMsg();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finishAll();
        }

        @Override
        public void onErrorExecute(String error) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
            ToastUtil.showMessage(error);
        }
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                ToastUtil.showMessage("登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                ToastUtil.showMessage("登录失败");
                return;
            }
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
            Util.dismissDialog();
        }

        @Override
        public void onCancel() {
            Util.toastMessage(LoginActivity.this, "取消授权");
        }
    }

    public  void initOpenidAndToken(JSONObject jsonObject) {
        try {
            token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }

    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                }
                @Override
                public void onComplete(final Object response) {
                    if (activityIsRunning) {
                        ProgressDialogUtils.getInstance(LoginActivity.this).show(R.string.dialog_request_login);
                    }
                    new QqLoginTask().request(token, openId, channelId, mCurrrentCity);
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(this, mTencent.getQQToken());
            mInfo.getUserInfo(listener);
        } else {
        }
    }

    class QqLoginTask extends QqLoginRequest {
        @Override
        public void onPostExecute(ClientUser clientUser) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
            MobclickAgent.onProfileSignIn(String.valueOf(AppManager
                    .getClientUser().userId));
            File faceLocalFile = new File(FileAccessorUtils.FACE_IMAGE,
                    Md5Util.md5(clientUser.face_url) + ".jpg");
            if(!faceLocalFile.exists()
                    && !TextUtils.isEmpty(clientUser.face_url)){
                new DownloadPortraitTask().request(clientUser.face_url,
                        FileAccessorUtils.FACE_IMAGE,
                        Md5Util.md5(clientUser.face_url) + ".jpg");
            } else {
                clientUser.face_local = faceLocalFile.getAbsolutePath();
            }
            clientUser.currentCity = mCurrrentCity;
            clientUser.latitude = curLat;
            clientUser.longitude = curLon;
            AppManager.setClientUser(clientUser);
            AppManager.saveUserInfo();
            AppManager.getClientUser().loginTime = System.currentTimeMillis();
            PreferencesUtils.setLoginTime(LoginActivity.this, System.currentTimeMillis());
            IMChattingHelper.getInstance().sendInitLoginMsg();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finishAll();
        }

        @Override
        public void onErrorExecute(String error) {
            ProgressDialogUtils.getInstance(LoginActivity.this).dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }


    class DownloadPortraitTask extends DownloadFileRequest {
        @Override
        public void onPostExecute(String s) {
            AppManager.getClientUser().face_local = s;
            PreferencesUtils.setFaceLocal(LoginActivity.this, s);
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
        if (TextUtils.isEmpty(loginAccount.getText().toString())) {
            message = getResources().getString(R.string.input_phone_or_account);
            bool = false;
        } else if (TextUtils.isEmpty(loginPwd.getText().toString())) {
            message = getResources().getString(R.string.input_password);
            bool = false;
        }
        if (!bool)
            ToastUtil.showMessage(message);
        return bool;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityIsRunning = true;
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityIsRunning = false;
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressDialogUtils.getInstance(this).dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
