package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.location.DPoint;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.AppointmentModel;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.UpdateAppointmentRequest;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.MY_ACCEPT;
import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.MY_DECLINE;
import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.MY_WAIT_CALL_BACK;

/**
 * Created by wangyb on 2018/1/9.
 */

public class AppointmentInfoActivity extends BaseActivity implements GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnMapScreenShotListener{


    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbarActionbar;
    @BindView(R.id.portrait)
    SimpleDraweeView mPortrait;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.appointment_theme)
    TextView mAppointmentTheme;
    @BindView(R.id.applay_status)
    TextView mApplayStatus;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.time_long)
    TextView mTimeLong;
    @BindView(R.id.address)
    TextView mAddress;
    @BindView(R.id.remark)
    TextView mRemark;
    @BindView(R.id.map_url)
    SimpleDraweeView mMapUrl;
    @BindView(R.id.accept)
    FancyButton mAccept;
    @BindView(R.id.decline)
    FancyButton mDecline;
    @BindView(R.id.chat)
    FancyButton mChat;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.map_lay)
    FrameLayout mMapLay;
    @BindView(R.id.image_lay)
    FrameLayout mImageLay;

    private AppointmentModel mModel;

    private String from;//有值：约我的 没值：我约的

    private AMap aMap;
    private UiSettings mUiSettings;
    private GeocodeSearch geocoderSearch;

    private LatLonPoint mLatLonPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);
        ButterKnife.bind(this);
        mToolbarActionbar.setNavigationIcon(R.mipmap.ic_up);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        from = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);
        mModel = (AppointmentModel) getIntent().getSerializableExtra(ValueKey.DATA);
        initData();

        if (!TextUtils.isEmpty(from) && null != mModel && mModel.status == MY_WAIT_CALL_BACK) {
            mAccept.setVisibility(View.VISIBLE);
            mDecline.setVisibility(View.VISIBLE);
        } else {
            mAccept.setVisibility(View.GONE);
            mDecline.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(from)) {
            initMap();
            getLocation();
        }
    }

    private void initData() {
        mPortrait.setImageURI(Uri.parse(mModel.faceUrl));
        if (!TextUtils.isEmpty(from)) {
            mUserName.setText(mModel.userName);
        } else {
            mUserName.setText(mModel.userByName);
        }
        mAppointmentTheme.setText("主题：" + mModel.theme);
        mApplayStatus.setText(Html.fromHtml(AppointmentModel.getStatus(mModel.status)));
        mTime.setText("时间：" + mModel.appointTime);
        if (!TextUtils.isEmpty(mModel.remark)) {
            mRemark.setText("附言：" + mModel.remark);
        }
        if (!TextUtils.isEmpty(mModel.address)) {
            mAddress.setText("地点：" + mModel.address);
        }
        if (!TextUtils.isEmpty(mModel.imgUrl)) {
            mMapUrl.setImageURI(Uri.parse(mModel.imgUrl));
        }
        if (!TextUtils.isEmpty(mModel.appointTimeLong)) {
            mTimeLong.setText("时长：" + mModel.appointTimeLong);
        }
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);// 不显示缩放按钮
            mUiSettings.setLogoPosition(-50);
            mUiSettings.setZoomGesturesEnabled(true);
            aMap.moveCamera(CameraUpdateFactory.zoomTo(12));// 设置缩放比例
        }
        // 地理编码
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

    }

    /**
     * 展示用户地图
     */
    private void getLocation() {
        try {
            String myLatitude = AppManager.getClientUser().latitude;
            String myLongitude = AppManager.getClientUser().longitude;
            if (!TextUtils.isEmpty(myLatitude) &&
                    !TextUtils.isEmpty(myLongitude)) {
                LatLonPoint latLonPoint = null;
                LatLng latLng = null;
                if (!TextUtils.isEmpty(from)) {
                    latLonPoint = new LatLonPoint(Double.parseDouble(myLatitude) + mModel.latitude,
                            Double.parseDouble(myLongitude) + mModel.longitude);
                    latLng = new LatLng(Double.parseDouble(myLatitude) + mModel.latitude,
                            Double.parseDouble(myLongitude) + mModel.longitude);
                } else {
                    latLonPoint = new LatLonPoint(mModel.latitude, mModel.longitude);
                    latLng = new LatLng(mModel.latitude, mModel.longitude);
                }
                mLatLonPoint = latLonPoint;
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng));
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
            }
        } catch (Exception e) {

        }
    }

    @OnClick({R.id.portrait, R.id.map_url, R.id.accept, R.id.decline, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.portrait:
                if (null != mModel) {
                    Intent personIntent = new Intent(this, PersonalInfoActivity.class);
                    if (!TextUtils.isEmpty(from)) {
                        personIntent.putExtra(ValueKey.USER_ID, mModel.userId);
                    } else {
                        personIntent.putExtra(ValueKey.USER_ID, mModel.userById);
                    }
                    startActivity(personIntent);
                }
                break;
            case R.id.map_url:
                Intent intent = new Intent(this, LocationDetailActivity.class);
                intent.putExtra(ValueKey.LATITUDE, mModel.latitude);
                intent.putExtra(ValueKey.LONGITUDE, mModel.longitude);
                intent.putExtra(ValueKey.ADDRESS, mAddress.getText().toString());
                intent.putExtra(ValueKey.FROM_ACTIVITY, this.getClass().getSimpleName());
                startActivity(intent);
                break;
            case R.id.accept:
                mModel.status = MY_ACCEPT;
                new UpdateAppointmentTask().request(mModel);
                break;
            case R.id.decline:
                mModel.status = MY_DECLINE;
                new UpdateAppointmentTask().request(mModel);
                break;
            case R.id.chat:
                if (null != mModel) {
                    Intent chatIntent = new Intent(this, ChatActivity.class);
                    ClientUser clientUser = new ClientUser();
                    if (!TextUtils.isEmpty(from)) {
                        clientUser.user_name = mModel.userName;
                        clientUser.userId = mModel.userId;
                    } else {
                        clientUser.user_name = mModel.userByName;
                        clientUser.userId = mModel.userById;
                    }
                    clientUser.face_url = mModel.faceUrl;
                    chatIntent.putExtra(ValueKey.USER, clientUser);
                    startActivity(chatIntent);
                }
                break;
        }
    }

    class UpdateAppointmentTask extends UpdateAppointmentRequest {

        @Override
        public void onPostExecute(String s) {
            if (mModel.status == MY_ACCEPT) {
                ToastUtil.showMessage("您已经同意了" + mModel.userName + "约会请求");
            } else {
                ToastUtil.showMessage("您已经拒绝了" + mModel.userName + "约会请求");
            }
            finish();
        }

        @Override
        public void onErrorExecute(String error) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                PoiItem poiItem = new PoiItem("", mLatLonPoint, "", result
                        .getRegeocodeAddress().getFormatAddress());
                mAddress.setText("地点：" + poiItem.getSnippet());
                if (!TextUtils.isEmpty(from)) {
                    mImageLay.setVisibility(View.GONE);
                    mMapLay.setVisibility(View.VISIBLE);
                }
            } else {
                mMapLay.setVisibility(View.GONE);
            }
        } else {
            mMapLay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

}
