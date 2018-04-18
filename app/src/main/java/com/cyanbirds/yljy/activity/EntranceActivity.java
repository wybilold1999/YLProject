package com.cyanbirds.yljy.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.CityInfo;
import com.cyanbirds.yljy.entity.ShowClient;
import com.cyanbirds.yljy.eventtype.LocationEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetCityInfoRequest;
import com.cyanbirds.yljy.net.request.GetPermissionRequest;
import com.cyanbirds.yljy.net.request.UploadCityInfoRequest;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @ClassName:EntranceActivity
 * @Description:登录和注册引导入口
 * @Author:wangyb
 * @Date:2015年5月5日下午5:26:39
 */
public class EntranceActivity extends BaseActivity implements AMapLocationListener {

    @BindView(R.id.login)
    FancyButton mLogin;
    @BindView(R.id.register)
    FancyButton mRegister;

    private final int REQUEST_LOCATION_PERMISSION = 1000;
    private boolean isSecondAccess = false;

    private AMapLocationClientOption mLocationOption;
    private AMapLocationClient mlocationClient;
    private String mCurrrentCity;//定位到的城市
    private CityInfo mCityInfo;//web api返回的城市信息
    private String curLat;
    private String curLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        ButterKnife.bind(this);
        new GetPermission().request();
        saveFirstLauncher();
        setupViews();
        new GetCityInfoTask().request();
        initLocationClient();
        AppManager.requestLocationPermission(this);
        requestLocationPermission();
    }

    /**
     * 设置视图
     */
    private void setupViews() {
        mLogin = (FancyButton) findViewById(R.id.login);
        mRegister = (FancyButton) findViewById(R.id.register);
    }

    /**
     * 保存是否第一次启动
     */
    private void saveFirstLauncher() {
        try {
            PreferencesUtils.setIsFirstLauncher(this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class GetPermission extends GetPermissionRequest {

        @Override
        public void onPostExecute(ShowClient showClient) {
            if (showClient != null) {
                AppConstants.IS_SHOW_WECHAT_LOGO = showClient.isShowGold;
                EventBus.getDefault().post(showClient);
            }
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    /**
     * 获取用户所在城市
     */
    class GetCityInfoTask extends GetCityInfoRequest {

        @Override
        public void onPostExecute(CityInfo cityInfo) {
            mCityInfo = cityInfo;
            mCurrrentCity = cityInfo.city;
            PreferencesUtils.setCurrentCity(EntranceActivity.this, mCurrrentCity);
            PreferencesUtils.setCurrentProvince(EntranceActivity.this, cityInfo.province);
            EventBus.getDefault().post(new LocationEvent(mCurrrentCity));
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    /**
     * 初始化定位
     */
    private void initLocationClient() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && !TextUtils.isEmpty(aMapLocation.getCity())) {
            curLat = String.valueOf(aMapLocation.getLatitude());
            curLon = String.valueOf(aMapLocation.getLongitude());
            mCurrrentCity = aMapLocation.getCity();
            PreferencesUtils.setCurrentCity(this, mCurrrentCity);
            PreferencesUtils.setCurrentProvince(EntranceActivity.this, aMapLocation.getProvince());
            EventBus.getDefault().post(new LocationEvent(mCurrrentCity));
            new UploadCityInfoRequest().request(aMapLocation.getCity(), String.valueOf(aMapLocation.getLatitude()),
                    String.valueOf(aMapLocation.getLongitude()));
        } else {
            if (mCityInfo != null) {
                try {
                    String[] rectangle = mCityInfo.rectangle.split(";");
                    String[] leftBottom = rectangle[0].split(",");
                    String[] rightTop = rectangle[1].split(",");

                    double lat = Double.parseDouble(leftBottom[1]) + (Double.parseDouble(rightTop[1]) - Double.parseDouble(leftBottom[1])) / 5;
                    curLat = String.valueOf(lat);

                    double lon = Double.parseDouble(leftBottom[0]) + (Double.parseDouble(rightTop[0]) - Double.parseDouble(leftBottom[0])) / 5;
                    curLon = String.valueOf(lon);
                } catch (Exception e) {

                }
            }
        }
        PreferencesUtils.setLatitude(this, curLat);
        PreferencesUtils.setLongitude(this, curLon);
    }

    @OnClick({R.id.login, R.id.register})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.login:
                intent.setClass(this, LoginActivity.class);
                if (!TextUtils.isEmpty(AppManager.getClientUser().mobile)) {
                    intent.putExtra(ValueKey.PHONE_NUMBER, AppManager.getClientUser().mobile);
                }
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.LATITUDE, curLat);
                intent.putExtra(ValueKey.LONGITUDE, curLon);
                break;
            case R.id.register:
                intent.setClass(this, RegisterActivity.class);
                intent.putExtra(ValueKey.LOCATION, mCurrrentCity);
                intent.putExtra(ValueKey.LATITUDE, curLat);
                intent.putExtra(ValueKey.LONGITUDE, curLon);
                break;
        }
        startActivity(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // 拒绝授权
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // 勾选了不再提示
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                if (!isSecondAccess) {
                    showAccessLocationDialog();
                }
            }
        }
    }

    private void requestLocationPermission() {
        PackageManager pkgManager = CSApplication.getInstance().getPackageManager();
        boolean ACCESS_COARSE_LOCATION =
                pkgManager.checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        boolean ACCESS_FINE_LOCATION =
                pkgManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= 23 && !ACCESS_COARSE_LOCATION || !ACCESS_FINE_LOCATION) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void showAccessLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.access_location);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                isSecondAccess = true;
                if (Build.VERSION.SDK_INT >= 23) {
                    ActivityCompat.requestPermissions(EntranceActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_LOCATION_PERMISSION);
                }
            }
        });
        builder.show();
    }
}