package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.AppointmentModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.ApplyForAppointmentRequest;
import com.cyanbirds.yljy.net.request.OSSImagUploadRequest;
import com.cyanbirds.yljy.utils.DateUtil;
import com.cyanbirds.yljy.utils.ProgressDialogUtils;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cyanbirds.yljy.utils.DateUtil.DEFAULT_PATTERN;

/**
 * Created by wangyb on 2018/1/9.
 */

public class AppointmentActivity extends BaseActivity {

    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbarActionbar;
    @BindView(R.id.appointment_prj)
    TextView mAppointmentPrj;
    @BindView(R.id.appointment_prj_lay)
    RelativeLayout mAppointmentPrjLay;
    @BindView(R.id.appointment_time)
    TextView mAppointmentTime;
    @BindView(R.id.appointment_time_lay)
    RelativeLayout mAppointmentInfoLay;
    @BindView(R.id.appointment_long)
    TextView mAppointmentLong;
    @BindView(R.id.appointment_long_lay)
    RelativeLayout mAppointmentLongLay;
    @BindView(R.id.appointment_address)
    TextView mAppointmentAddress;
    @BindView(R.id.appointment_address_lay)
    RelativeLayout mAppointmentAddressLay;
    @BindView(R.id.appointment_remark)
    EditText mAppointmentRemark;
    @BindView(R.id.sure)
    TextView mSure;

    private String mApplyForUid;//向谁申请
    private String mApplyForUName;//向谁申请
    private String mApplyForFaceUrl;//向谁申请
    private View mDateTimeView;
    private DatePicker datePicker;
    private double latitude;
    private double longitude;
    private String mMapImgUrl;

    private boolean isAlreadyUpload = true;
    private AppointmentModel mModel;

    /**
     * 分享位置
     */
    public final static int SHARE_LOCATION_RESULT = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        ButterKnife.bind(this);
        mToolbarActionbar.setNavigationIcon(R.mipmap.ic_up);
        initData();
    }

    private void initData() {
        mApplyForUid = getIntent().getStringExtra(ValueKey.USER_ID);
        mApplyForUName = getIntent().getStringExtra(ValueKey.USER_NAME);
        mApplyForFaceUrl = getIntent().getStringExtra(ValueKey.IMAGE_URL);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        mAppointmentTime.setText(DateUtil.formatDateByFormat(calendar.getTime(), DEFAULT_PATTERN));

        mModel = new AppointmentModel();
    }


    @OnClick({R.id.appointment_prj_lay, R.id.appointment_time_lay, R.id.appointment_long_lay, R.id.appointment_address_lay, R.id.sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.appointment_prj_lay:
                showPrjDialog();
                break;
            case R.id.appointment_time_lay:
                showTimeDialog();
                break;
            case R.id.appointment_long_lay:
                showTimeLongDialog();
                break;
            case R.id.appointment_address_lay:
                toShareLocation();
                break;
            case R.id.sure:
                if (!TextUtils.isEmpty(mApplyForUid)) {
                    if (!TextUtils.isEmpty(mAppointmentAddress.getText().toString())) {
                        initAppointmentModel();
                        ProgressDialogUtils.getInstance(this).show(R.string.dialog_apply_for);
                        if (!TextUtils.isEmpty(mMapImgUrl)) {
                            mModel.imgUrl = mMapImgUrl;
                            isAlreadyUpload = true;
                            new ApplyForAppointmentTask().request(mModel);
                        } else {
                            isAlreadyUpload = false;
                        }
                    } else {
                        ToastUtil.showMessage(R.string.select_appointment_location_tips);
                    }
                }
                break;
        }
    }

    private void initAppointmentModel() {
        mModel.remark = mAppointmentRemark.getText().toString();
        mModel.theme = mAppointmentPrj.getText().toString();
        mModel.appointTime = mAppointmentTime.getText().toString();
        mModel.latitude = latitude;
        mModel.longitude = longitude;
        mModel.userById = mApplyForUid;
        mModel.userByName = mApplyForUName;
        mModel.faceUrl = mApplyForFaceUrl;
        mModel.status = AppointmentModel.AppointStatus.WAIT_CALL_BACK;
        mModel.appointTimeLong = mAppointmentLong.getText().toString();
        mModel.userId = AppManager.getClientUser().userId;
        mModel.userName = AppManager.getClientUser().user_name;
        mModel.address = mAppointmentAddress.getText().toString();
    }

    class ApplyForAppointmentTask extends ApplyForAppointmentRequest {
        @Override
        public void onPostExecute(String s) {
            ProgressDialogUtils.getInstance(AppointmentActivity.this).dismiss();
            ToastUtil.showMessage(R.string.appointment_apply_for_success);
            finish();
        }

        @Override
        public void onErrorExecute(String error) {
            ProgressDialogUtils.getInstance(AppointmentActivity.this).dismiss();
            ToastUtil.showMessage(R.string.appointment_apply_for_faiure);
            finish();
        }
    }

    /**
     * 显示约会项目
     */
    private void showPrjDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tv_project);
        final String[] array = getResources().getStringArray(R.array.appointment_content);
        builder.setItems(R.array.appointment_content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mAppointmentPrj.setText(array[i]);
            }
        });
        builder.show();
    }

    /**
     * 显示约会时长
     */
    private void showTimeLongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tv_appointment_time);
        final String[] array = getResources().getStringArray(R.array.appointment_long);
        builder.setItems(R.array.appointment_long, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mAppointmentLong.setText(array[i]);
            }
        });
        builder.show();
    }

    /**
     * 跳到分享位置界面
     */
    private void toShareLocation() {
        Intent intent = new Intent(this, ShareLocationActivity.class);
        intent.putExtra(ValueKey.FROM_ACTIVITY, this.getClass().getSimpleName());
        startActivityForResult(intent, SHARE_LOCATION_RESULT);
    }

    /**
     * 显示约会时间
     */
    private void showTimeDialog() {
        initDateTimeView();
        final AlertDialog mDialog = new AlertDialog.Builder(this).setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null).create();
        mDialog.setTitle(R.string.tv_appointment_time);
        mDialog.setView(mDateTimeView);
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuilder date = new StringBuilder();
                        date.append(datePicker.getYear())
                                .append("-")
                                .append(datePicker.getMonth() + 1)
                                .append("-")
                                .append(datePicker.getDayOfMonth());
                        Date selectTime = DateUtil.parseDate(date.toString(), DEFAULT_PATTERN);
                        Calendar calendar = Calendar.getInstance();
                        Date curTime = calendar.getTime();
                        if (selectTime.getTime() <= curTime.getTime()) {
                            ToastUtil.showMessage(R.string.appointment_time);
                        } else {
                            mDialog.dismiss();
                            mAppointmentTime.setText(date.toString());
                        }
                    }
                });
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        });
        mDialog.show();
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tv_appointment_time);
        builder.setView(mDateTimeView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder date = new StringBuilder();
                date.append(datePicker.getYear())
                        .append("-")
                        .append(datePicker.getMonth() + 1)
                        .append("-")
                        .append(datePicker.getDayOfMonth());
                Date selectTime = DateUtil.parseDate(date.toString(), DEFAULT_PATTERN);
                Date curTime = DateUtil.parseDate(mAppointmentTime.getText().toString(), DEFAULT_PATTERN);
                if (selectTime.getTime() <= curTime.getTime()) {
                    ToastUtil.showMessage(R.string.appointment_time);
                } else {
                    dialogInterface.dismiss();
                    mAppointmentTime.setText(date.toString());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();*/

    }

    private void initDateTimeView() {
        mDateTimeView = LayoutInflater.from(this).inflate(R.layout.date_time_picker_layout, null);
        datePicker = (DatePicker) mDateTimeView.findViewById(R.id.date_picker);
        datePicker.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == SHARE_LOCATION_RESULT) {
            String address = data.getStringExtra(ValueKey.ADDRESS);
            latitude = data.getDoubleExtra(ValueKey.LATITUDE, 0);
            longitude = data.getDoubleExtra(ValueKey.LONGITUDE, 0);
            if (!TextUtils.isEmpty(address)) {
                mAppointmentAddress.setText(address);
            }
            String path = data.getStringExtra(ValueKey.IMAGE_URL);
            if (!TextUtils.isEmpty(path)) {
                new OSSImgUploadTask().request(AppManager.getFederationToken().bucketName,
                        AppManager.getOSSFacePath(), path);
            }
        }
    }

    /**
     * 上传图片至OSS
     */
    class OSSImgUploadTask extends OSSImagUploadRequest {

        @Override
        public void onPostExecute(String s) {
            mMapImgUrl = AppConstants.OSS_IMG_ENDPOINT + s;
            mModel.imgUrl = mMapImgUrl;
            if (!isAlreadyUpload) {
                initAppointmentModel();
                new ApplyForAppointmentTask().request(mModel);
            }
        }

        @Override
        public void onErrorExecute(String error) {
            if (!isAlreadyUpload) {
                ProgressDialogUtils.getInstance(AppointmentActivity.this).dismiss();
                ToastUtil.showMessage(R.string.appointment_apply_for_faiure);
            }
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


}
