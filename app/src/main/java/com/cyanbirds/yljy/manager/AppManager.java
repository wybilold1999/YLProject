package com.cyanbirds.yljy.manager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.OSS;
import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.FederationToken;
import com.cyanbirds.yljy.entity.IMessage;
import com.cyanbirds.yljy.net.DynamicService;
import com.cyanbirds.yljy.net.FollowService;
import com.cyanbirds.yljy.net.LoveService;
import com.cyanbirds.yljy.net.PictureService;
import com.cyanbirds.yljy.net.UserService;
import com.cyanbirds.yljy.net.VideoService;
import com.cyanbirds.yljy.utils.PreferencesUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @ClassName:AppManager
 * @Description:APP管理类
 * @Author:wangyb
 * @Date:2015年5月4日下午5:30:07
 *
 */
public class AppManager {

	/** Android 应用上下文 */
	private static Context mContext = null;
	/** 包名 */
	public static String pkgName = "com.cyanbirds.yljy";
	/** 用户登录信息 */
	private static ClientUser mClientUser;
	/**
	 * 点击通知栏消息，进入Main之后，需要所有的通知栏消息，
	 * 有些推送进入main之后，来的比较晚，用这个变量判断
	 * 是否通过通知栏进入main，是的话，其他后来的消息就
	 * 取消通知栏
	 */
	public static boolean isMsgClick = false;
	/** Activity管理对象 **/
	private static ActivityManager mActivityManager = null;
	/** 运行的任务集体 **/
	private static List<RunningTaskInfo> tasksInfo = null;
	/**
	 * 进入聊天界面当前聊天联系人id
	 */
	public static String currentChatTalker = null;
	private static final int REQUEST_LOCATION_PERMISSION = 1000;

	private static UserService mUserService;
	private static PictureService mPictureService;
	private static FollowService mFollowService;
	private static LoveService mLoveService;
	private static VideoService mVideoService;
	private static DynamicService mDynamicService;

	private static IWXAPI sIWX_PAY_API;
	private static IWXAPI sIWXAPI;

	private static ExecutorService mExecutorService;

	private static OSS mOSS;

	private static FederationToken mOSSFederationToken;

	public static FederationToken getFederationToken() {
		if(mOSSFederationToken==null){
			mOSSFederationToken=new FederationToken();
		}
		return mOSSFederationToken;
	}

	public static FederationToken setFederationToken(FederationToken token) {
		return mOSSFederationToken = token;
	}

	public static OSS getOSS() {
		return mOSS;
	}

	public static void setOSS(OSS oss) {
		mOSS = oss;
	}

	public static ExecutorService getExecutorService() {
		if (mExecutorService == null) {
			mExecutorService = Executors.newFixedThreadPool(3);
		}
		return mExecutorService;
	}

	/**
	 * 设置用户信息
	 * 
	 * @param user
	 */
	public static void setClientUser(ClientUser user) {
		mClientUser = user;
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public static ClientUser getClientUser() {
		return mClientUser;
	}

	/**
	 * 获取包名
	 * 
	 * @return
	 */
	public static String getPackageName() {
		return pkgName;
	}

	/**
	 * 返回上下文对象
	 * 
	 * @return
	 */
	public static Context getContext() {
		return mContext;
	}

	/**
	 * 设置上下文对象
	 * 
	 * @param context
	 */
	public static void setContext(Context context) {
		mContext = context;
		pkgName = context.getPackageName();
	}

	/**
	 * 获取应用程序版本名称
	 * 
	 * @return 版本名称
	 */
	public static String getVersion() {
		String version = "0.0.0";
		if (mContext == null) {
			return version;
		}
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return 版本号
	 */
	public static int getVersionCode() {
		int code = 1;
		if (mContext == null) {
			return code;
		}
		try {
			PackageInfo packageInfo = mContext.getPackageManager()
					.getPackageInfo(getPackageName(), 0);
			code = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * 获取设备ID
	 * 
	 * @return
	 */
	public static String getDeviceId() {
		String deviceId = "";
		try {
			// 获取ID
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String id = tm.getDeviceId();
			// 获取mac地址
			String macSerial = null;
			String str = "";
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();
					break;
				}
			}
			deviceId = "Android_" + id + "_" + macSerial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceId;
	}

	/**
	 * 获取设备名称
	 * @return
     */
	public static String getDeviceName(){
		return new Build().MANUFACTURER;
	}

	/**
	 * 获取系统版本
	 * @return
     */
	public static String getDeviceSystemVersion(){
		return Build.VERSION.RELEASE;
	}

	/**
	 * 判断手机是否处于锁屏状态
	 * 
	 * @param context
	 * @return true 表示处于锁屏状态，
	 */
	public static boolean isScreenLocked(Context context) {
		boolean flag = false;
		KeyguardManager mKeyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 判断程序是否在前台运行
	 * @param context
	 * @return
	 */
	public static boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				//前台程序
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}

		return isInBackground;
	}

	/**
	 * 判断3C是否在栈顶
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static boolean MyActivityIsTop(Context context) {
		boolean isTop = false;
		if (null == mActivityManager) {
			mActivityManager = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE));
		}
		if (null != mActivityManager) {
			tasksInfo = mActivityManager.getRunningTasks(1);
			if (null != tasksInfo && tasksInfo.size() > 0) {
				if (pkgName.equals(tasksInfo.get(0).topActivity
						.getPackageName())) {// 3c应用在栈顶
					isTop = true;
				}
			}
		}
		return isTop;
	}

	/**
	 * 判断应用是否已经启动
	 *
	 * @param context     一个context
	 * @param packageName 要判断应用的包名
	 * @return boolean
	 */
	public static boolean isAppAlive(Context context, String packageName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager
				.getRunningAppProcesses();
		for (int i = 0; i < processInfos.size(); i++) {
			if (processInfos.get(i).processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前栈顶的activity的名称
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getTopActivity(Context context) {
		if (null == mActivityManager) {
			mActivityManager = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE));
		}
		if (null != mActivityManager) {
			tasksInfo = mActivityManager.getRunningTasks(1);
			if (null != tasksInfo && tasksInfo.size() > 0) {
				// return
				// (tasksInfo.get(0).topActivity.getShortClassName()).toString();
				return tasksInfo.get(0).topActivity.getClassName();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 现在消息通知
	 *
	 * @param message
	 */
	public static void showNotification(IMessage message) {
		if (checkNeedMsgNotify(message)) {
			NotificationManager.getInstance().showMessageNotification(
					message);
		}
	}

	private static boolean checkNeedMsgNotify(IMessage message) {
		if (TextUtils.isEmpty(AppManager.currentChatTalker) ||
				!message.talker.equals(String.valueOf(AppManager.currentChatTalker))) {
			return true;
		}
		return false;
	}

	/**
	 * 获取手机IMEI
	 * 
	 * @return
	 */
	public static String getImei() {
		String imei = ((TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		return imei;
	}

	public static void installApk(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	/**
	 * 获取UUID作为群聊唯一标识
	 * 
	 * @return
	 */
	public static String getUUID() {
		String strUUID = java.util.UUID.randomUUID().toString();
		return strUUID;
	}

	/**
	 * 是否登录判读
	 * 
	 * @return
	 */
	public static boolean isLogin() {
		if (getClientUser() == null || TextUtils.isEmpty(getClientUser().userId) // &&Integer.parseInt(getClientUser().userId) <= 0)
				|| !PreferencesUtils.getIsLogin(mContext)) {
			return false;
		}

		return true;
	}

	public static void goToMarket(Context context, String channel) {
		/**
		 * 根据渠道跳转到不同的应用市场更新APP
		 */
		if ("sanxing".equals(channel)) {
			Uri uri = Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=" + pkgName);
			Intent goToMarket = new Intent();
			goToMarket.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
			goToMarket.setData(uri);
			try {
				context.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} else if ("leshi".equals(channel)) {
			Intent intent = new Intent();
			intent.setClassName("com.letv.app.appstore", "com.letv.app.appstore.appmodule.details.DetailsActivity");
			intent.setAction("com.letv.app.appstore.appdetailactivity");
			intent.putExtra("packageName", pkgName);
			context.startActivity(intent);
		} else {
			Uri uri = Uri.parse("market://details?id=" + pkgName);
			Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			try {
				context.startActivity(goToMarket);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public static void requestLocationPermission(Activity activity) {
		PackageManager pkgManager = CSApplication.getInstance().getPackageManager();
		boolean ACCESS_COARSE_LOCATION =
				pkgManager.checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
		boolean ACCESS_FINE_LOCATION =
				pkgManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()) == PackageManager.PERMISSION_GRANTED;
		if (Build.VERSION.SDK_INT >= 23 && !ACCESS_COARSE_LOCATION || !ACCESS_FINE_LOCATION) {
			//请求权限
			ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION_PERMISSION);
		}
	}

	public static boolean checkPermission(Activity activity, String permission, int flag) {
		boolean isHasPermission = false;
		if (Build.VERSION.SDK_INT >= 23) {
			PackageManager pkgManager = CSApplication.getInstance().getPackageManager();
			isHasPermission = pkgManager.checkPermission(permission, getPackageName()) == PackageManager.PERMISSION_GRANTED;
			if (!isHasPermission) {
				//请求权限
				ActivityCompat.requestPermissions(activity, new String[] {permission}, flag);
			}
		} else {
			isHasPermission = true;
		}
		return isHasPermission;
	}

	/**
	 * 获取需要上传到oss路径
	 *
	 * @param
	 * @return
	 */
	public static String getOSSFacePath() {
		String path = "tan_love/img/tl_" + getUUID() + ".jpg";
		return path;
	}

	public static IWXAPI getIWXAPI() {
		return sIWXAPI;
	}

	public static void setIWXAPI(IWXAPI IWXAPI) {
		sIWXAPI = IWXAPI;
	}

	public static IWXAPI getIWX_PAY_API() {
		return sIWX_PAY_API;
	}

	public static void setIWX_PAY_API(IWXAPI IWX_PAY_API) {
		sIWX_PAY_API = IWX_PAY_API;
	}

	/**
	 * 设置用户信息
	 */
	public static void setUserInfo() {
		try {
			String userId = PreferencesUtils.getAccount(mContext);
			String mobile = PreferencesUtils.getUserMobile(mContext);
			String pwd = PreferencesUtils.getPassword(mContext);
			String sex = PreferencesUtils.getUserSex(mContext);
			String userName = PreferencesUtils.getUserName(mContext);
			String face_url = PreferencesUtils.getFaceUrl(mContext);
			String face_local = PreferencesUtils.getFaceLocal(mContext);
			String signature = PreferencesUtils.getSignature(mContext);
			String qq_no = PreferencesUtils.getQq(mContext);
			String weixin_no = PreferencesUtils.getWeiXin(mContext);
			String weight = PreferencesUtils.getWeight(mContext);
			String tall = PreferencesUtils.getTall(mContext);
			String distance = PreferencesUtils.getDistance(mContext);
			String constellation = PreferencesUtils.getConstellation(mContext);
			String state_marry = PreferencesUtils.getEmotionStatus(mContext);
			String sessionId = PreferencesUtils.getSessionid(mContext);
			boolean isCheckPhone = PreferencesUtils.getIsCheckPhone(mContext);
			boolean publicSocialNumber = PreferencesUtils.getPublicSocialnumber(mContext);
			boolean is_vip = PreferencesUtils.getIsVip(mContext);
			ClientUser clientUser = new ClientUser();
			clientUser.userId = userId;
			clientUser.mobile = mobile;
			clientUser.userPwd = pwd;
			clientUser.sex = sex;
			clientUser.user_name = userName;
			clientUser.face_url = face_url;
			clientUser.face_local = face_local;
			clientUser.signature = signature;
			clientUser.qq_no = qq_no;
			clientUser.weixin_no = weixin_no;
			clientUser.weight = weight;
			clientUser.tall = tall;
			clientUser.distance = distance;
			clientUser.constellation = constellation;
			clientUser.isCheckPhone = isCheckPhone;
			clientUser.state_marry = state_marry;
			clientUser.publicSocialNumber = publicSocialNumber;
			clientUser.is_vip = is_vip;
			clientUser.sessionId = sessionId;
			clientUser.isShowVip = PreferencesUtils.getIsShow(mContext);
			clientUser.isShowDownloadVip = clientUser.isShowVip;
			clientUser.isShowGold = clientUser.isShowVip;
			clientUser.isShowLovers = clientUser.isShowVip;
			clientUser.isShowVideo  = clientUser.isShowVip;
			clientUser.currentCity = PreferencesUtils.getCurrentCity(mContext);
			setClientUser(clientUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存用户信息
	 */
	public static void saveUserInfo() {
		try {
			PreferencesUtils.setAccount(mContext, getClientUser().userId);
			PreferencesUtils.setPassword(mContext, getClientUser().userPwd);
			PreferencesUtils.setFaceLocal(mContext, getClientUser().face_local);
			PreferencesUtils.setFaceUrl(mContext, getClientUser().face_url);
			PreferencesUtils.setUserMobile(mContext, getClientUser().mobile);
			PreferencesUtils.setUserName(mContext, getClientUser().user_name);
			PreferencesUtils.setOccupation(mContext, getClientUser().occupation);
			PreferencesUtils.setEducation(mContext, getClientUser().education);
			PreferencesUtils.setUserSex(mContext, getClientUser().sex);
			PreferencesUtils.setAge(mContext, getClientUser().age);
			PreferencesUtils.setSignature(mContext, getClientUser().signature);
			PreferencesUtils.setCity(mContext, getClientUser().city);
			PreferencesUtils.setPurpose(mContext, getClientUser().purpose);
			PreferencesUtils.setDoWhatFirst(mContext, getClientUser().do_what_first);
			PreferencesUtils.setLoveWhere(mContext, getClientUser().love_where);
			PreferencesUtils.setConception(mContext, getClientUser().conception);
			PreferencesUtils.setQq(mContext, getClientUser().qq_no);
			PreferencesUtils.setWeiXin(mContext, getClientUser().weixin_no);
			PreferencesUtils.setWeight(mContext, getClientUser().weight);
			PreferencesUtils.setTall(mContext, getClientUser().tall);
			PreferencesUtils.setDistance(mContext, getClientUser().distance);
			PreferencesUtils.setConstellation(mContext, getClientUser().constellation);
			PreferencesUtils.setIsCheckPhone(mContext, getClientUser().isCheckPhone);
			PreferencesUtils.setEmotionStatus(mContext, getClientUser().state_marry);
			PreferencesUtils.setPublicSocialnumber(mContext, getClientUser().publicSocialNumber);
			PreferencesUtils.setIsVip(mContext, getClientUser().is_vip);
			PreferencesUtils.setSessionId(mContext, getClientUser().sessionId);
			PreferencesUtils.setIsShow(mContext, getClientUser().isShowVip);
			PreferencesUtils.setIsLogin(mContext, true);
			PreferencesUtils.setCurrentCity(mContext, getClientUser().currentCity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 释放数据库
	 */
	public static void release() {
	}

	/************设置和获取网络用户接口**********************/
	public static void setUserService(UserService userService){
		mUserService = userService;
	}

	public static UserService getUserService(){
		return mUserService;
	}

	/************设置和获取网络图片接口**********************/
	public static void setPictureService(PictureService pictureService){
		mPictureService = pictureService;
	}

	public static PictureService getPictureService(){
		return mPictureService;
	}

	/************设置和获取网络关注操作接口**********************/
	public static void setFollowService(FollowService followService){
		mFollowService = followService;
	}

	public static FollowService getFollowService(){
		return mFollowService;
	}

	/************设置和获取网络喜欢操作接口**********************/
	public static void setLoveService(LoveService loveService){
		mLoveService = loveService;
	}

	public static LoveService getLoveService(){
		return mLoveService;
	}

	/************设置和获取网络视频操作接口**********************/
	public static void setVideoService(VideoService videoService){
		mVideoService = videoService;
	}

	public static VideoService getVideoService(){
		return mVideoService;
	}

	/************设置和获取动态操作接口**********************/
	public static DynamicService getDynamicService() {
		return mDynamicService;
	}

	public static void setDynamicService(DynamicService mDynamicService) {
		AppManager.mDynamicService = mDynamicService;
	}
}
