package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.UpdateUserInfoRequest;
import com.cyanbirds.yljy.utils.CheckUtil;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author wangyb
 * @Description:绑定手机
 * @Date:2015年7月13日下午2:21:46
 */
public class BandPhoneActivity extends BaseActivity {

    @BindView(R.id.phone_num)
    EditText mPhoneNum;
    @BindView(R.id.next)
    FancyButton mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_phone);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
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

    @OnClick(R.id.next)
    public void onViewClicked() {
        if (checkInput()) {
            ProgressDialogUtils.getInstance(this).show(R.string.wait);
            AppManager.getClientUser().isCheckPhone = true;
            AppManager.getClientUser().mobile = mPhoneNum.getText().toString();
            AppManager.setClientUser(AppManager.getClientUser());
            AppManager.saveUserInfo();
            new UpdateUserInfoTask().request(AppManager.getClientUser());
        }
    }

    /**
     * 验证输入
     */
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

    class UpdateUserInfoTask extends UpdateUserInfoRequest {
        @Override
        public void onPostExecute(String s) {
            ToastUtil.showMessage(R.string.bangding_success);
            ProgressDialogUtils.getInstance(BandPhoneActivity.this).dismiss();
            finish();
        }

        @Override
        public void onErrorExecute(String error) {
            ToastUtil.showMessage(R.string.bangding_faile);
            ProgressDialogUtils.getInstance(BandPhoneActivity.this).dismiss();
            finish();
        }
    }
}
