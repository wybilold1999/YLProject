package com.cyanbirds.yljy.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cyanbirds.yljy.manager.AppManager;


/**
 * 
 * @ClassName:PrefUtils.java
 * @Description:
 * @author wangyb
 * @Date:2015年7月6日下午2:12:22
 */
public class PreferencesUtils {

	/** 保存登陆账号 */
	public static final String SETTINGS_RL_ACCOUNT = "com.cyanbird.tanlove_account";
	/** 保存登陆密码 */
	public static final String SETTINGS_RL_PASSWORD = "com.cyanbird.tanlove_p";
	/** 保存是否第一次启动 */
	public static final String SETTINGS_RL_FIRST_LAUNCHER = "com.cyanbird.tanlove_first_launcher";
	/** 用户年龄 */
	public static final String SETTINGS_RL_USER_AGE = "com.cyanbird.tanlove_age";
	/** 头像url */
	public static final String SETTINGS_RL_USER_FACE_URL = "com.cyanbird.tanlove_face_url";
	/** 手机号 */
	public static final String SETTINGS_RL_USER_MOBILE = "com.cyanbird.tanlove_mobile";
	/** 个性签名 */
	public static final String SETTINGS_RL_USER_SIGNATURE = "com.cyanbird.tanlove_signature";
	/** 保存用户性别 */
	public static final String SETTINGS_RL_USER_SEX = "com.cyanbird.tanlove_sex";
	/** 用户名 */
	public static final String SETTINGS_RL_USER_USER_NAME = "com.cyanbird.tanlove_user_name";
	/** 某用户是否第一次登录 */
	public static final String SETTINGS_RL_FIRST_LOGIN = "com.cyanbird.tanlove_first_login";
	/** 是否登录 */
	public static final String SETTINGS_RL_IS_LOGIN = "com.cyanbird.tanlove_is_login";
	/** 本地头像地址 */
	public static final String SETTINGS_RL_FACE_LOCAL = "com.cyanbird.tanlove_face_local";
	/**sessionId */
	public static final String SETTINGS_RL_SESSIONID = "com.cyanbird.tanlove_sessionid";
	/**qq号 */
	public static final String SETTINGS_RL_QQ = "com.cyanbird.tanlove_qq";
	/**微信号 */
	public static final String SETTINGS_RL_WEIXIN = "com.cyanbird.tanlove_weixin";
	/**身高 */
	public static final String SETTINGS_RL_TALL = "com.cyanbird.tanlove_tall";
	/**体重 */
	public static final String SETTINGS_RL_WEIGHT = "com.cyanbird.tanlove_weight";
	/**距离 */
	public static final String SETTINGS_RL_DISTANCE = "com.cyanbird.tanlove_distance";
	/**情感状态 */
	public static final String SETTINGS_RL_EMOTION_STATUS = "com.cyanbird.tanlove_emotion_status";
	/**星座 */
	public static final String SETTINGS_RL_CONSTELLATION = "com.cyanbird.tanlove_constellation";
	/**城市 */
	public static final String SETTINGS_RL_CITY = "com.cyanbird.tanlove_city";
	/**职业 */
	public static final String SETTINGS_RL_OCCUPATION = "com.cyanbird.tanlove_occupation";
	/**教育 */
	public static final String SETTINGS_RL_EDUCATION = "com.cyanbird.tanlove_education";
	/**交友目的 */
	public static final String SETTINGS_RL_PURPOSE = "com.cyanbird.tanlove_purpose";
	/**爱爱地点 */
	public static final String SETTINGS_RL_LOVE_WHERE = "com.cyanbird.tanlove_love_where";
	/**首次见面希望 */
	public static final String SETTINGS_RL_DO_WHAT_FIRST = "com.cyanbird.tanlove_do_what_first";
	/**恋爱观念 */
	public static final String SETTINGS_RL_CONCEPTION = "com.cyanbird.tanlove_conception";
	/**是否显示 */
	public static final String SETTINGS_RL_IS_SHOW = "com.cyanbird.tanlove_is_show";
	/**是否vip */
	public static final String SETTINGS_RL_IS_VIP = "com.cyanbird.tanlove_is_vip";
	/**isCheckPhone */
	public static final String SETTINGS_RL_IS_CHECK_PHONE = "com.cyanbird.tanlove_is_check_phone";
	/**publicSocialNumber */
	public static final String SETTINGS_RL_PUBLIC_SOCIALNUMBER = "com.cyanbird.tanlove_public_socialnumber";
	/** 新消息提醒 */
	public static final String SETTINGS_RL_NEW_MESSAGE_NOTICE = "com.cyanbird.tanlove_new_message_notice";
	/** 通知显示消息详情 */
	public static final String SETTINGS_RL_NOTICE_MESSAGE_INFO = "com.cyanbird.tanlove_notice_message_info";
	/** 通知声音*/
	public static final String SETTINGS_RL_NOTICE_VOICE = "com.cyanbird.tanlove_notice_voice";
	/** 通知震动*/
	public static final String SETTINGS_RL_NOTICE_SHOCK = "com.cyanbird.tanlove_notice_shock";
	/** 听筒播放*/
	public static final String SETTINGS_RL_EARPIECE_PLAY_VOICE = "com.cyanbird.tanlove_earpiece_play_voice";
	/** 极光推送设置别名是否成功*/
	public static final String SETTINGS_JPUSH_SET_ALIAS = "com.cyanbird.tanlove_jpush_set_alias";
	/** 抽奖次数*/
	public static final String SETTINGS_REWARD_COUNT = "com.cyanbird.tanlove_reward_count";
	/** 定位到的城市*/
	public static final String SETTINGS_CURRENT_CITY = "com.cyanbird.tanlove_current_city";
	/** 最近喜欢我的userid*/
	public static final String SETTINGS_LOVE_ME_USER_ID = "com.cyanbird.tanlove_love_me_user_id";
	/** 最近关注我的userid*/
	public static final String SETTINGS_ATTENTION_ME_USER_ID = "com.cyanbird.tanlove_attention_me_user_id";
	/** 最近送我礼物的userid*/
	public static final String SETTINGS_GIFT_ME_USER_ID = "com.cyanbird.tanlove_gift_me_user_id";
	/** (我约会的)改变约会状态的userid*/
	public static final String SETTINGS_I_APPOINT_USER_ID = "com.cyanbird.tanlove_i_appoint_user_id";
	/** (约我的)改变约会状态的userid*/
	public static final String SETTINGS_APPOINT_ME_USER_ID = "com.cyanbird.tanlove_appoint_me_user_id";
	/** 登录时间*/
	public static final String SETTINGS_LOGIN_TIME = "com.cyanbird.tanlove_login_time";
	/** 支付宝支付次数*/
	public static final String SETTINGS_ALI_PAY_COUNT = "com.cyanbird.tanlove_ali_pay_count";
	/** 我的零钱*/
	public static final String SETTINGS_MY_MONEY = "com.cyanbird.tanlove_my_money";
	/** 聊天次数限制*/
	public static final String SETTINGS_CHAT_LIMIT = "com.cyanbird.tanlove_chat_limit";
	/** 省份*/
	public static final String SETTINGS_CURRENT_PROVINCE = "com.cyanbird.tanlove_current_province";
	/** 经度*/
	public static final String SETTINGS_LATITUDE = "com.cyanbird.tanlove_latitude";
	/** 纬度*/
	public static final String SETTINGS_LONGITUDE = "com.cyanbird.tanlove_longitude";
	/** 是否有领取话费的活动*/
	public static final String SETTINGS_IS_HAS_GET_FARE_ACTIVITY = "com.cyanbirds.tanlove_is_has_get_fare_activity";
	/** 购买的是多少钱的vip*/
	public static final String SETTINGS_WHICH_VIP = "com.cyanbirds.tanlove_which_vip";
	/** 登录的次数*/
	public static final String SETTINGS_LOGIN_COUNT = "com.cyanbirds.tanlove_login_count";
	/** 该月是否可以领取话费*/
	public static final String SETTINGS_IS_CAN_GET_FARE = "com.cyanbirds.tanlove_is_can_get_fare";
	/** 个推token*/
	public static final String SETTINGS_GT_TOKEN = "com.cyanbirds.tanlove_gt_token";
	/** 信鸽token*/
	public static final String SETTINGS_XG_TOKEN = "com.cyanbirds.tanlove_xg_token";

	/**
	 * 获取RL账号
	 *
	 * @return
	 */
	public static String getAccount(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_ACCOUNT, "");
	}

	/**
	 * 保存RL账号
	 *
	 * @param context
	 * @param account
	 */
	public static void setAccount(final Context context, final String account) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_ACCOUNT, account).commit();
	}

	/**
	 * 获取保存的密码
	 *
	 * @param context
	 * @return
	 */
	public static String getPassword(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_PASSWORD, "");
	}

	/**
	 * 保存密码
	 *
	 * @param context
	 */
	public static void setPassword(final Context context, final String password) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_PASSWORD, password).commit();
	}

	/**
	 * 获取是否第一次启动
	 *
	 * @param context
	 * @return
	 */
	public static boolean getIsFirstLauncher(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_FIRST_LAUNCHER, true);
	}

	/**
	 * 保存第一次启动
	 *
	 * @param context
	 */
	public static void setIsFirstLauncher(final Context context,
										  final Boolean firstLauncher) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_FIRST_LAUNCHER, firstLauncher)
				.commit();
	}


	/**
	 * 获取用户头像
	 *
	 * @param context
	 * @return
	 */
	public static String getFaceUrl(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_FACE_URL, "");
	}

	/**
	 * 保存用户头像
	 *
	 * @param context
	 */
	public static void setFaceUrl(final Context context, final String faceUrl) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_FACE_URL, faceUrl).commit();
	}

	/**
	 * 获取用户手机号码
	 *
	 * @param context
	 * @return
	 */
	public static String getUserMobile(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_MOBILE, "");
	}

	/**
	 * 保存用户手机号码
	 *
	 * @param context
	 */
	public static void setUserMobile(final Context context, final String faceUrl) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_MOBILE, faceUrl).commit();
	}

	/**
	 * 获取用户签名
	 *
	 * @param context
	 * @return
	 */
	public static String getSignature(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_SIGNATURE, "");
	}

	/**
	 * 保存用户签名
	 *
	 * @param context
	 */
	public static void setSignature(final Context context,
									final String signature) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_SIGNATURE, signature).commit();
	}

	/**
	 * 获取用户性别
	 *
	 * @param context
	 * @return
	 */
	public static String getUserSex(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_SEX, "");
	}

	/**
	 * 保存用户性别
	 *
	 * @param context
	 */
	public static void setUserSex(final Context context, final String sex) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_SEX, sex).commit();
	}

	/**
	 * 获取用户名称
	 *
	 * @param context
	 * @return
	 */
	public static String getUserName(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_USER_USER_NAME, "");
	}

	/**
	 * 保存用户名称
	 *
	 * @param context
	 */
	public static void setUserName(final Context context, final String username) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_USER_USER_NAME, username).commit();
	}

	/**
	 * 获取用户是否第一次登录
	 *
	 * @param context
	 * @return
	 */
	public static boolean getFirstLogin(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(
				SETTINGS_RL_FIRST_LOGIN + "_"
						+ AppManager.getClientUser().userId, true);
	}

	/**
	 * 保存用户是否第一次登录
	 *
	 * @param context
	 */
	public static void setFirstLogin(final Context context,
									 final Boolean firstLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit()
				.putBoolean(
						SETTINGS_RL_FIRST_LOGIN + "_"
								+ AppManager.getClientUser().userId, firstLogin)
				.commit();
	}

	/**
	 * 获取用户是否登录
	 *
	 * @param context
	 * @return
	 */
	public static boolean getIsLogin(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_IS_LOGIN, false);
	}

	/**
	 * 保存用户是否登录
	 *
	 * @param context
	 */
	public static void setIsLogin(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_IS_LOGIN, isLogin).commit();
	}

	/**
	 * 获取本地头像地址
	 *
	 * @param context
	 * @return
	 */
	public static String getFaceLocal(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_FACE_LOCAL, "");
	}

	/**
	 * 保存本地头像地址
	 *
	 * @param context
	 * @return
	 */
	public static void setFaceLocal(final Context context, final String face_local) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_FACE_LOCAL, face_local).commit();
	}

	/**
	 *
	 * 获取sessionId
	 * @param context
	 * @return
	 */
	public static String getSessionid(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_SESSIONID, "");
	}

	/**
	 * 保存sessionId
	 *
	 * @param context
	 */
	public static void setSessionId(final Context context,
							 final String sessionId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_SESSIONID, sessionId).commit();
	}

	/**
	 *
	 * 获取QQ
	 * @param context
	 * @return
	 */
	public static String getQq(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_QQ, "");
	}

	/**
	 * 保存QQ
	 *
	 * @param context
	 */
	public static void setQq(final Context context,
									final String qq) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_QQ, qq).commit();
	}

	/**
	 *
	 * 获取微信
	 * @param context
	 * @return
	 */
	public static String getWeiXin(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_WEIXIN, "");
	}

	/**
	 * 保存微信
	 *
	 * @param context
	 */
	public static void setWeiXin(final Context context,
									final String weixin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_WEIXIN, weixin).commit();
	}

	/**
	 *
	 * 获取体重
	 * @param context
	 * @return
	 */
	public static String getTall(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_TALL, "");
	}

	/**
	 * 保存身高
	 *
	 * @param context
	 */
	public static void setTall(final Context context,
									final String tall) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_TALL, tall).commit();
	}

	/**
	 *
	 * 获取体重
	 * @param context
	 * @return
	 */
	public static String getWeight(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_WEIGHT, "");
	}

	/**
	 * 保存体重
	 *
	 * @param context
	 */
	public static void setWeight(final Context context,
									final String weight) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_WEIGHT, weight).commit();
	}

	/**
	 *
	 * 获取距离
	 * @param context
	 * @return
	 */
	public static String getDistance(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_DISTANCE, "");
	}

	/**
	 * 保存距离
	 *
	 * @param context
	 */
	public static void setDistance(final Context context,
									final String distance) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_DISTANCE, distance).commit();
	}

	/**
	 *
	 * 获取情感状态
	 * @param context
	 * @return
	 */
	public static String getEmotionStatus(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_EMOTION_STATUS, "");
	}

	/**
	 * 保存情感状态
	 *
	 * @param context
	 */
	public static void setEmotionStatus(final Context context,
									final String emotionStatus) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_EMOTION_STATUS, emotionStatus).commit();
	}

	/**
	 *
	 * 获取是否vip
	 * @param context
	 * @return
	 */
	public static boolean getIsVip(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_IS_VIP, false);
	}

	/**
	 * 保存是否vip
	 *
	 * @param context
	 */
	public static void setIsVip(final Context context,
									final boolean is_vip) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_IS_VIP, is_vip).commit();
	}

	/**
	 *
	 * 获取是否显示
	 * @param context
	 * @return
	 */
	public static boolean getIsShow(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_IS_SHOW, false);
	}

	/**
	 * 保存是否显示
	 *
	 * @param context
	 */
	public static void setIsShow(final Context context,
								final boolean is_show) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_IS_SHOW, is_show).commit();
	}

	/**
	 *
	 * 获取星座
	 * @param context
	 * @return
	 */
	public static String getConstellation(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_CONSTELLATION, "");
	}

	/**
	 * 保存星座
	 *
	 * @param context
	 */
	public static void setConstellation(final Context context,
										final String constellation) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_CONSTELLATION, constellation).commit();
	}

	/**
	 *
	 * 获取职业
	 * @param context
	 * @return
	 */
	public static String getOccupation(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_OCCUPATION, "");
	}

	/**
	 * 保存职业
	 *
	 * @param context
	 */
	public static void setOccupation(final Context context,
										final String occupation) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_OCCUPATION, occupation).commit();
	}


	/**
	 *
	 * 获取学历
	 * @param context
	 * @return
	 */
	public static String getEducation(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_EDUCATION, "");
	}

	/**
	 * 保存学历
	 *
	 * @param context
	 */
	public static void setEducation(final Context context,
									final String education) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_EDUCATION, education).commit();
	}

	/**
	 *
	 * 获取年龄
	 * @param context
	 * @return
	 */
	public static int getAge(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_RL_USER_AGE, 23);
	}

	/**
	 * 保存年龄
	 *
	 * @param context
	 */
	public static void setAge(final Context context,
									final int age) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_RL_USER_AGE, age).commit();
	}

	/**
	 *
	 * 获取交友目的
	 * @param context
	 * @return
	 */
	public static String getPurpose(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_PURPOSE, "长期X伴侣");
	}

	/**
	 * 保存交友目的
	 *
	 * @param context
	 */
	public static void setPurpose(final Context context,
							  final String purpose) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_PURPOSE, purpose).commit();
	}

	/**
	 *
	 * 获取爱爱地点
	 * @param context
	 * @return
	 */
	public static String getLoveWhere(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_LOVE_WHERE, "车上");
	}

	/**
	 * 保存爱爱地点
	 *
	 * @param context
	 */
	public static void setLoveWhere(final Context context,
									final String loveWhere) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_LOVE_WHERE, loveWhere).commit();
	}

	/**
	 *
	 * 获取首次见面
	 * @param context
	 * @return
	 */
	public static String getDoWhatFirst(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_DO_WHAT_FIRST, "如果喜欢，可以开房爱爱");
	}

	/**
	 * 保存首次见面
	 * @param context
	 */
	public static void setDoWhatFirst(final Context context,
									final String doWhat) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_DO_WHAT_FIRST, doWhat).commit();
	}

	/**
	 *
	 * 获取恋爱观念
	 * @param context
	 * @return
	 */
	public static String getConception(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_CONCEPTION, "大胆开放");
	}

	/**
	 * 保存恋爱观念
	 * @param context
	 */
	public static void setConception(final Context context,
									  final String conception) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_CONCEPTION, conception).commit();
	}

	/**
	 *
	 * 获取城市
	 * @param context
	 * @return
	 */
	public static String getCity(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_RL_CITY, "待定");
	}

	/**
	 * 保存城市
	 * @param context
	 */
	public static void setCity(final Context context,
									 final String conception) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_RL_CITY, conception).commit();
	}


	/**
	 *
	 * 获取是否验证手机
	 * @param context
	 * @return
	 */
	public static boolean getIsCheckPhone(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_IS_CHECK_PHONE, false);
	}

	/**
	 * 保存是否验证手机
	 *
	 * @param context
	 */
	public static void setIsCheckPhone(final Context context,
										final boolean is_check_phone) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_IS_CHECK_PHONE, is_check_phone).commit();
	}

	/**
	 *
	 * 获取是否公开社交号
	 * @param context
	 * @return
	 */
	public static boolean getPublicSocialnumber(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_PUBLIC_SOCIALNUMBER, false);
	}

	/**
	 * 保存是否公开社交号
	 *
	 * @param context
	 */
	public static void setPublicSocialnumber(final Context context,
											 final boolean public_socialnumber) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_PUBLIC_SOCIALNUMBER, public_socialnumber).commit();
	}

	/**
	 *
	 * 获取新消息是否通知
	 * @param context
	 * @return
	 */
	public static boolean getNewMessageNotice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NEW_MESSAGE_NOTICE, true);
	}

	/**
	 * 保存新消息是否通知
	 *
	 * @param context
	 */
	public static void setNewMessageNotice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NEW_MESSAGE_NOTICE, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否显示详情
	 * @param context
	 * @return
	 */
	public static boolean getShowMessageInfo(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_MESSAGE_INFO, true);
	}

	/**
	 * 保存消息通知是否显示详情
	 *
	 * @param context
	 */
	public static void setShowMessageInfo(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_MESSAGE_INFO, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否有声音
	 * @param context
	 * @return
	 */
	public static boolean getNoticeVoice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_VOICE, true);
	}

	/**
	 * 保存消息通知是否有声音
	 *
	 * @param context
	 */
	public static void setNoticeVoice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_VOICE, isLogin).commit();
	}

	/**
	 *
	 * 获取消息通知是否震动
	 * @param context
	 * @return
	 */
	public static boolean getNoticeShock(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_NOTICE_SHOCK, true);
	}

	/**
	 * 保存消息通知是否震动
	 *
	 * @param context
	 */
	public static void setNoticeShock(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_NOTICE_SHOCK, isLogin).commit();
	}

	/**
	 *
	 * 获取是否听筒播放语音
	 * @param context
	 * @return
	 */
	public static boolean getEarpiecePlayVoice(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_RL_EARPIECE_PLAY_VOICE, false);
	}

	/**
	 * 保存是否听筒播放语音
	 *
	 * @param context
	 */
	public static void setEarpiecePlayVoice(final Context context, final Boolean isLogin) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_RL_EARPIECE_PLAY_VOICE, isLogin).commit();
	}

	/**
	 *
	 * 获取极光推送设置别名是否成功
	 * @param context
	 * @return
	 */
	public static boolean getJpushSetAliasState(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_JPUSH_SET_ALIAS, false);
	}

	/**
	 * 保存极光推送设置别名状态
	 * @param context
	 */
	public static void setJpushSetAliasState(final Context context, final Boolean isSuccess) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_JPUSH_SET_ALIAS, isSuccess).commit();
	}

	/**
	 *
	 * 抽奖次数
	 * @param context
	 * @return
	 */
	public static int getRewardCount(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_REWARD_COUNT, 0);
	}

	/**
	 * 保存抽奖次数
	 * @param context
	 */
	public static void setRewardCount(final Context context, final int count) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_REWARD_COUNT, count).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getCurrentCity(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_CURRENT_CITY, "");
	}

	/**
	 * @param context
	 */
	public static void setCurrentCity(final Context context, final String city) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_CURRENT_CITY, city).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getLoveMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LOVE_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setLoveMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LOVE_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getAttentionMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_ATTENTION_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setAttentionMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_ATTENTION_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getGiftMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_GIFT_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setIAppointUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_I_APPOINT_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getIAppointUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_I_APPOINT_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setGiftMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_GIFT_ME_USER_ID, userId).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static long getLoginTime(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getLong(SETTINGS_LOGIN_TIME, -1);
	}

	/**
	 * @param context
	 */
	public static void setLoginTime(final Context context, final long loginTime) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putLong(SETTINGS_LOGIN_TIME, loginTime).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getAliPayCount(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_ALI_PAY_COUNT, 0);
	}

	/**
	 * @param context
	 */
	public static void setAliPayCount(final Context context, final int count) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_ALI_PAY_COUNT, count).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static float getMyMoney(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getFloat(SETTINGS_MY_MONEY, 0);
	}

	/**
	 * @param context
	 */
	public static void setMyMoney(final Context context, final float count) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putFloat(SETTINGS_MY_MONEY, count).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static int getChatLimit(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_CHAT_LIMIT, 0);
	}

	/**
	 * @param context
	 */
	public static void setChatLimit(final Context context, final int count) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_CHAT_LIMIT, count).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getCurrentProvince(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_CURRENT_PROVINCE, "");
	}

	/**
	 * @param context
	 */
	public static void setCurrentProvince(final Context context, final String province) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_CURRENT_PROVINCE, province).commit();
	}

	/**
	 *
	 * @param context
	 * @return
	 */
	public static String getAppointMeUserId(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_APPOINT_ME_USER_ID, "");
	}

	/**
	 * @param context
	 */
	public static void setAppointMeUserId(final Context context, final String userId) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_APPOINT_ME_USER_ID, userId).commit();
	}

	public static void setLatitude(final Context context, final String lat) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LATITUDE, lat).commit();
	}

	public static String getLatitude(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LATITUDE, "");
	}

	public static void setLongitude(final Context context, final String longitude) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_LONGITUDE, longitude).commit();
	}

	public static String getLongitude(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_LONGITUDE, "");
	}

	public static boolean getIsHasGetFareActivity(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_IS_HAS_GET_FARE_ACTIVITY, false);
	}

	public static void setIsHasGetFareActivity(final Context context,
											   final Boolean isHasActivity) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_IS_HAS_GET_FARE_ACTIVITY, isHasActivity)
				.commit();
	}

	/**
	 * 0:返话费的vip
	 * 1:不能返话费的vip
	 * @param context
	 * @return
	 */
	public static int getWhichVip(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_WHICH_VIP, 1);
	}

	public static void setWhichVip(final Context context,
								   final int whichVip) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_WHICH_VIP, whichVip).commit();
	}

	public static int getLoginCount(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getInt(SETTINGS_LOGIN_COUNT, 0);
	}

	public static void setLoginCount(final Context context,
									 final int loginCount) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putInt(SETTINGS_LOGIN_COUNT, loginCount).commit();
	}

	/**
	 * 当月是否可领取话费
	 * @param context
	 * @return
	 */
	public static boolean getIsCanGetFare(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean(SETTINGS_IS_CAN_GET_FARE, true);
	}

	public static void setIsCanGetFare(final Context context,
									   final Boolean isCanGetFare) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(SETTINGS_IS_CAN_GET_FARE, isCanGetFare)
				.commit();
	}

	public static void setSettingsGtToken(final Context context, final String gtToken) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_GT_TOKEN, gtToken).commit();
	}

	public static String getSettingsGtToken(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_GT_TOKEN, "");
	}

	public static void setSettingsXgToken(final Context context, final String xgToken) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		sp.edit().putString(SETTINGS_XG_TOKEN, xgToken).commit();
	}

	public static String getSettingsXgToken(final Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getString(SETTINGS_XG_TOKEN, "");
	}

}
