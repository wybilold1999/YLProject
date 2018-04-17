package com.cyanbirds.yljy.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.ViewPagerAdapter;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.db.ConversationSqlManager;
import com.cyanbirds.yljy.entity.AppointmentModel;
import com.cyanbirds.yljy.entity.CityInfo;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.FederationToken;
import com.cyanbirds.yljy.entity.FollowModel;
import com.cyanbirds.yljy.entity.LoveModel;
import com.cyanbirds.yljy.entity.ReceiveGiftModel;
import com.cyanbirds.yljy.fragment.FoundFragment;
import com.cyanbirds.yljy.fragment.HomeLoveFragment;
import com.cyanbirds.yljy.fragment.MessageFragment;
import com.cyanbirds.yljy.fragment.PersonalFragment;
import com.cyanbirds.yljy.helper.BottomNavigationViewHelper;
import com.cyanbirds.yljy.helper.SDKCoreHelper;
import com.cyanbirds.yljy.listener.MessageUnReadListener;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.manager.NotificationManager;
import com.cyanbirds.yljy.net.request.FollowListRequest;
import com.cyanbirds.yljy.net.request.GetAppointmentListRequest;
import com.cyanbirds.yljy.net.request.GetCityInfoRequest;
import com.cyanbirds.yljy.net.request.GetLoveFormeListRequest;
import com.cyanbirds.yljy.net.request.GetOSSTokenRequest;
import com.cyanbirds.yljy.net.request.GiftsListRequest;
import com.cyanbirds.yljy.net.request.UploadCityInfoRequest;
import com.cyanbirds.yljy.service.MyIntentService;
import com.cyanbirds.yljy.service.MyPushService;
import com.cyanbirds.yljy.ui.widget.CustomViewPager;
import com.cyanbirds.yljy.utils.DateUtil;
import com.cyanbirds.yljy.utils.DensityUtil;
import com.cyanbirds.yljy.utils.MsgUtil;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.cyanbirds.yljy.utils.PushMsgUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.igexin.sdk.PushManager;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yuntongxun.ecsdk.ECInitParams;

import java.util.Calendar;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.ACCEPT;
import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.DECLINE;
import static com.cyanbirds.yljy.entity.AppointmentModel.AppointStatus.MY_WAIT_CALL_BACK;
import static com.cyanbirds.yljy.utils.DateUtil.TIMESTAMP_PATTERN;

public class MainActivity extends BaseActivity implements MessageUnReadListener.OnMessageUnReadListener, AMapLocationListener {

	private CustomViewPager viewPager;
	private BottomNavigationView bottomNavigationView;
	private ClientConfiguration mOSSConf;

	private static final int REQUEST_PERMISSION = 0;
	private final int REQUEST_LOCATION_PERMISSION = 1000;
	private final int REQUEST_PERMISSION_SETTING = 10001;

	private AMapLocationClientOption mLocationOption;
	private AMapLocationClient mlocationClient;
	private boolean isSecondAccess = false;
	private boolean isSecondRead = false;

	private String curLat;
	private String curLon;

	/**
	 * oss鉴权获取失败重试次数
	 */
	public int mOSSTokenRetryCount = 0;

	private Badge mBadgeView;
	private QBadgeView mQBadgeView;

	private static Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		new GetCityInfoTask().request();
		setupViews();
		setupEvent();
		initOSS();
		if (AppManager.getClientUser().is_vip) {
			SDKCoreHelper.init(CSApplication.getInstance(), ECInitParams.LoginMode.FORCE_LOGIN);
		}
		updateConversationUnRead();

		AppManager.getExecutorService().execute(new Runnable() {
			@Override
			public void run() {
				initLocationClient();

				if (AppManager.getClientUser().isShowVip) {
					/**
					 * 注册小米推送
					 */
					MiPushClient.registerPush(MainActivity.this, AppConstants.MI_PUSH_APP_ID, AppConstants.MI_PUSH_APP_KEY);

					//个推
					initGeTuiPush();

					XGPushManager.registerPush(getApplicationContext());

					loadData();

					initFareGetTime();
				}
			}
		});

		if (AppManager.getClientUser().isShowVip) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new GetLoveFormeListTask().request(1, 1);
				}
			}, 9000 * 10);

			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new MyGiftListTask().request(1, 1);
				}
			}, 5000 * 10);

			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new FollowListTask().request("followFormeList", 1, 1);
				}
			}, 1500 * 10);
		} else {
			SDKCoreHelper.init(CSApplication.getInstance(), ECInitParams.LoginMode.FORCE_LOGIN);
		}
		if (AppManager.getClientUser().versionCode <= AppManager.getVersionCode() &&
				AppManager.getClientUser().isShowVip && AppManager.getClientUser().isShowAppointment) {
			//我约的
			new GetIAppointmentListTask().request(1, 1, AppManager.getClientUser().userId, 0);
			//约我的
			new GetAppointmeListTask().request(1, 1, AppManager.getClientUser().userId, 1);
		}
		registerWeiXin();
	}

	private void registerWeiXin() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		AppManager.setIWX_PAY_API(WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_PAY_ID, true));
		AppManager.getIWX_PAY_API().registerApp(AppConstants.WEIXIN_PAY_ID);
	}

	/**
	 * 初始化当月是否可以领取话费
	 */
	private void initFareGetTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		String lastDay = DateUtil.formatDateByFormat(calendar.getTime(), TIMESTAMP_PATTERN);
		try {
			if (System.currentTimeMillis() > Long.parseLong(lastDay)) {
				PreferencesUtils.setIsCanGetFare(this, true);
			}
		} catch (Exception e) {

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

	/**
	 * 点击通知栏的消息，将消息入库
	 */
	private void loadData() {
		String msg = getIntent().getStringExtra(ValueKey.DATA);
		if (!TextUtils.isEmpty(msg)) {
			PushMsgUtil.getInstance().handlePushMsg(false, msg);
			NotificationManager.getInstance().cancelNotification();
			AppManager.isMsgClick = true;
		}
	}

	/**
	 * 初始化oss
	 */
	private void initOSS() {
		mOSSConf = new ClientConfiguration();
		mOSSConf.setConnectionTimeout(30 * 1000); // 连接超时，默认15秒
		mOSSConf.setSocketTimeout(30 * 1000); // socket超时，默认15秒
		mOSSConf.setMaxConcurrentRequest(50); // 最大并发请求书，默认5个
		mOSSConf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
		OSSLog.enableLog();

		final Handler handler = new Handler();
		// 每30分钟请求一次鉴权
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				new GetFederationTokenTask().request();
				handler.postDelayed(this, 60 * 30 * 1000);
			}
		};

		handler.postDelayed(runnable, 0);
	}

	class GetFederationTokenTask extends GetOSSTokenRequest {

		@Override
		public void onPostExecute(FederationToken result) {
			try {
				if (result != null) {
					AppManager.setFederationToken(result);
					OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(result.accessKeyId, result.accessKeySecret, result.securityToken);
					OSS oss = new OSSClient(getApplicationContext(), result.endpoint, credentialProvider, mOSSConf);
					AppManager.setOSS(oss);
					mOSSTokenRetryCount = 0;
				} else {
					if (mOSSTokenRetryCount < 5) {
						new GetFederationTokenTask().request();
						mOSSTokenRetryCount++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onErrorExecute(String error) {
			if (mOSSTokenRetryCount < 5) {
				new GetFederationTokenTask().request();
				mOSSTokenRetryCount++;
			}
		}
	}

	/**
	 * 个推注册
	 */
	private void initGeTuiPush() {
		// SDK初始化，第三方程序启动时，都要进行SDK初始化工作
		PushManager.getInstance().initialize(this.getApplicationContext(), MyPushService.class);
		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), MyIntentService.class);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// 必须要调用这句(信鸽推送)
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if (aMapLocation != null && !TextUtils.isEmpty(aMapLocation.getCity())) {
			PreferencesUtils.setCurrentCity(this, aMapLocation.getCity());
			ClientUser clientUser = AppManager.getClientUser();
			clientUser.latitude = String.valueOf(aMapLocation.getLatitude());
			clientUser.longitude = String.valueOf(aMapLocation.getLongitude());
			AppManager.setClientUser(clientUser);
			curLat = clientUser.latitude;
			curLon = clientUser.longitude;
			if (TextUtils.isEmpty(PreferencesUtils.getCurrentProvince(this))) {
				PreferencesUtils.setCurrentProvince(this, aMapLocation.getProvince());
			}
			new UploadCityInfoRequest().request(aMapLocation.getCity(), String.valueOf(aMapLocation.getLatitude()),
					String.valueOf(aMapLocation.getLongitude()));
		}
		PreferencesUtils.setLatitude(this, curLat);
		PreferencesUtils.setLongitude(this, curLon);
	}

	/**
	 * 获取用户所在城市
	 */
	class GetCityInfoTask extends GetCityInfoRequest {

		@Override
		public void onPostExecute(CityInfo cityInfo) {
			if (cityInfo != null) {
				try {
					String[] rectangle = cityInfo.rectangle.split(";");
					String[] leftBottom = rectangle[0].split(",");
					String[] rightTop = rectangle[1].split(",");

					double lat = Double.parseDouble(leftBottom[1]) + (Double.parseDouble(rightTop[1]) - Double.parseDouble(leftBottom[1])) / 5;
					curLat = String.valueOf(lat);

					double lon = Double.parseDouble(leftBottom[0]) + (Double.parseDouble(rightTop[0]) - Double.parseDouble(leftBottom[0])) / 5;
					curLon = String.valueOf(lon);

					AppManager.getClientUser().latitude = curLat;
					AppManager.getClientUser().longitude = curLon;
				} catch (Exception e) {

				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	/**
	 * 获取最近喜欢我的那个人
	 */
	class GetLoveFormeListTask extends GetLoveFormeListRequest {
		@Override
		public void onPostExecute(List<LoveModel> loveModels) {
			if(loveModels != null && loveModels.size() > 0) {
				String lastUserId = PreferencesUtils.getLoveMeUserId(MainActivity.this);
				if (!lastUserId.equals(String.valueOf(loveModels.get(0).userId))) {

					PreferencesUtils.setLoveMeUserId(
							MainActivity.this, String.valueOf(loveModels.get(0).userId));
					Intent intent = new Intent(MainActivity.this, PopupLoveActivity.class);
					intent.putExtra(ValueKey.DATA, loveModels.get(0));
					startActivity(intent);
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	class MyGiftListTask extends GiftsListRequest {
		@Override
		public void onPostExecute(List<ReceiveGiftModel> receiveGiftModels) {
			if(null != receiveGiftModels && receiveGiftModels.size() > 0){
				ReceiveGiftModel model = receiveGiftModels.get(0);
				String lastUserId = PreferencesUtils.getGiftMeUserId(MainActivity.this);
				if (!lastUserId.equals(String.valueOf(model.userId))) {
					PreferencesUtils.setGiftMeUserId(
							MainActivity.this, String.valueOf(model.userId));
					MsgUtil.sendAttentionOrGiftMsg(String.valueOf(model.userId), model.nickname, model.faceUrl,
							model.nickname + "给您送了一件礼物");
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	class FollowListTask extends FollowListRequest {
		@Override
		public void onPostExecute(List<FollowModel> followModels) {
			if(followModels != null && followModels.size() > 0){
				FollowModel followModel = followModels.get(0);
				String lastUserId = PreferencesUtils.getAttentionMeUserId(MainActivity.this);
				if (!lastUserId.equals(String.valueOf(followModel.userId))) {
					PreferencesUtils.setAttentionMeUserId(
							MainActivity.this, String.valueOf(followModel.userId));
					MsgUtil.sendAttentionOrGiftMsg(String.valueOf(followModel.userId),
							followModel.nickname, followModel.faceUrl,
							followModel.nickname + "关注了您");
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	/**
	 * 我约的
	 */
	class GetIAppointmentListTask extends GetAppointmentListRequest {

		@Override
		public void onPostExecute(List<AppointmentModel> appointmentModels) {
			if(appointmentModels != null && appointmentModels.size() > 0){
				final AppointmentModel model = appointmentModels.get(0);
				if(model.status == ACCEPT || model.status == DECLINE) {
					String lastUserId = PreferencesUtils.getIAppointUserId(MainActivity.this);
					if (!lastUserId.equals(String.valueOf(model.userById))) {
						PreferencesUtils.setIAppointUserId(
								MainActivity.this, String.valueOf(model.userById));
						String status = "";
						if (model.status == ACCEPT) {
							status = model.userByName + "同意了你的约会请求";
						} else {
							status = model.userByName + "拒绝了你的约会请求";
						}
						MsgUtil.sendAttentionOrGiftMsg(String.valueOf(model.userById),
							model.userName, model.faceUrl, status);
					}
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	/**
	 * 约我的
	 */
	class GetAppointmeListTask extends GetAppointmentListRequest {

		@Override
		public void onPostExecute(List<AppointmentModel> appointmentModels) {
			if(appointmentModels != null && appointmentModels.size() > 0){
				AppointmentModel model = appointmentModels.get(0);
				if(model.status == MY_WAIT_CALL_BACK) {
					String lastUserId = PreferencesUtils.getAppointMeUserId(MainActivity.this);
					if (!lastUserId.equals(String.valueOf(model.userById))) {
						PreferencesUtils.setAppointMeUserId(
								MainActivity.this, String.valueOf(model.userById));
						//向你发起了约会申请
						showAppointmentInfoDialog(model);
					}
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	private void showAppointmentInfoDialog(final AppointmentModel model) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.appointment_invite);
		builder.setView(initAppointmentUserInfoView(model));
		builder.setPositiveButton(R.string.check_appointment_invite_info, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(MainActivity.this, AppointmentInfoActivity.class);
				intent.putExtra(ValueKey.DATA, model);
				intent.putExtra(ValueKey.FROM_ACTIVITY, MainActivity.this.getClass().getSimpleName());
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		builder.show();
	}

	private View initAppointmentUserInfoView(final AppointmentModel model) {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_appointment, null);
		SimpleDraweeView portrait = (SimpleDraweeView) view.findViewById(R.id.portrait);
		TextView inviteInfo = (TextView) view.findViewById(R.id.appointment_info);
		if (!TextUtils.isEmpty(model.faceUrl)) {
			portrait.setImageURI(Uri.parse(model.faceUrl));
		}
		portrait.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
				intent.putExtra(ValueKey.USER_ID, model.userId);
				startActivity(intent);
			}
		});
		inviteInfo.setText(Html.fromHtml(String.format(
				getResources().getString(R.string.appointment_invite_info), model.userName)));
		return view;
	}

	/**
	 * 设置视图
	 */
	private void setupViews() {
		viewPager = findViewById(R.id.viewpager);
		viewPager.setNoScroll(true);
		bottomNavigationView = findViewById(R.id.bottom_navigation);
		//默认 >3 的选中效果会影响ViewPager的滑动切换时的效果，故利用反射去掉
		BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
		bottomNavigationView.setOnNavigationItemSelectedListener(
				new BottomNavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(@NonNull MenuItem item) {
						switch (item.getItemId()) {
							case R.id.item_news:
								viewPager.setCurrentItem(0);
								break;
							case R.id.item_lib:
								viewPager.setCurrentItem(1);
								break;
							case R.id.item_find:
								viewPager.setCurrentItem(2);
								break;
							case R.id.item_more:
								viewPager.setCurrentItem(3);
								break;
						}
						return false;
					}
				});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(bottomNavigationView.getMenu().getItem(position).isChecked()){
					bottomNavigationView.getMenu().getItem(position).setChecked(false);
				}
			}

			@Override
			public void onPageSelected(int position) {
				bottomNavigationView.getMenu().getItem(position).setChecked(true);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		setupViewPager(viewPager);

		BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
		if (menuView != null) {
			mQBadgeView = new QBadgeView(this);
			mBadgeView = mQBadgeView.setGravityOffset((float) (DensityUtil.getWidthInPx(this) / 3.2), 2, false)
					.bindTarget(menuView);
		}
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

		adapter.addFragment(new HomeLoveFragment());
		adapter.addFragment(new FoundFragment());
		adapter.addFragment(new MessageFragment());
		adapter.addFragment(new PersonalFragment());
		viewPager.setAdapter(adapter);
	}


	private void setupEvent(){
		MessageUnReadListener.getInstance().setMessageUnReadListener(this);
	}


	@Override
	public void notifyUnReadChanged(int type) {
		updateConversationUnRead();
	}

	/**
	 * 更新会话未读消息总数
	 */
	private void updateConversationUnRead() {
		if (mBadgeView != null) {
			int total = ConversationSqlManager.getInstance(this)
					.getAnalyticsUnReadConversation();
			if (total > 0) {
				mQBadgeView.setVisibility(View.VISIBLE);
				if (total >= 100) {
					mBadgeView.setBadgeText("99+");
				} else {
					mBadgeView.setBadgeText(String.valueOf(total));
				}
			} else {
				mQBadgeView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION) {
			// 拒绝授权
			if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				// 勾选了不再提示
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
//					showOpenLocationDialog();
				} else {
					if (!isSecondRead) {
						showReadPhoneStateDialog();
					}
				}
			}
		} else if (requestCode == REQUEST_LOCATION_PERMISSION) {
			// 拒绝授权
			if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
				// 勾选了不再提示
				if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
						!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
					showOpenLocationDialog();
				} else {
					if (!isSecondAccess) {
						showAccessLocationDialog();
					}
				}
			} else {
				initLocationClient();
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void showOpenLocationDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.open_location);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

			}
		});
		builder.show();
	}


	private void showAccessLocationDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.access_location);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				isSecondAccess = true;
				if (Build.VERSION.SDK_INT >= 23) {
					ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
							REQUEST_LOCATION_PERMISSION);
				}

			}
		});
		builder.show();
	}

	private void showReadPhoneStateDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.get_read_phone_state);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				isSecondRead = true;
				if (Build.VERSION.SDK_INT >= 23) {
					ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
							REQUEST_LOCATION_PERMISSION);
				}

			}
		});
		builder.show();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			moveTaskToBack(false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PERMISSION_SETTING) {
			initLocationClient();
		}
	}

}
