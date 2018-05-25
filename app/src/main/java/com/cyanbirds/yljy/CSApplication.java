package com.cyanbirds.yljy;

import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;
import android.util.SparseIntArray;

import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.helper.AppActivityLifecycleCallbacks;
import com.cyanbirds.yljy.helper.CrashHandler;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.DynamicService;
import com.cyanbirds.yljy.net.FollowService;
import com.cyanbirds.yljy.net.LoveService;
import com.cyanbirds.yljy.net.PictureService;
import com.cyanbirds.yljy.net.UserService;
import com.cyanbirds.yljy.net.VideoService;
import com.cyanbirds.yljy.net.base.RetrofitManager;
import com.cyanbirds.yljy.utils.FileAccessorUtils;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.memory.PoolParams;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

import static com.cyanbirds.yljy.config.AppConstants.BUGLY_ID;

/**
 * 
 * @ClassName:CSApplication
 * @Description:全局Application
 * @author wangyb
 * @Date:2015年5月3日上午10:39:37
 *
 */
public class CSApplication extends MultiDexApplication {

	private static CSApplication sApplication;

	// IWXAPI 是第三方app和微信通信的openapi接口
	public static IWXAPI api;

	public static synchronized CSApplication getInstance() {
		return sApplication;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		sApplication = this;
		initNetInterface();
		AppManager.setContext(sApplication);
		AppManager.setUserInfo();
		//初始化短信sdk
		MobSDK.init(this);
		initFresco();

		FileDownloader.init(sApplication, new FileDownloadHelper.OkHttpClientCustomMaker() {
			@Override
			public OkHttpClient customMake() {
				return RetrofitManager.getInstance().getOkHttpClient();
			}
		});

		CrashHandler.getInstance().init(this);

		registerActivityLifecycleCallbacks(AppActivityLifecycleCallbacks.getInstance());

		registerWeiXin();

		initBugly();

	}

	private void initBugly() {
		// 获取当前进程名
		String processName = AppManager.getProcessName(android.os.Process.myPid());
		// 设置是否为上报进程
		CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
		strategy.setUploadProcess(processName == null || processName.equals(AppManager.pkgName));
		// 初始化Bugly
		CrashReport.initCrashReport(this, BUGLY_ID, false, strategy);
	}

	private void registerWeiXin() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, AppConstants.WEIXIN_ID, true);
		api.registerApp(AppConstants.WEIXIN_ID);
	}

	private void initFresco() {
		DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
				.setBaseDirectoryPath(FileAccessorUtils.getCachePathName())
				.setBaseDirectoryName("tan_love")
				.setMaxCacheSize(500*1024*1024)//500MB
				.setMaxCacheSizeOnLowDiskSpace(10 * 1024 * 1024)
				.setMaxCacheSizeOnVeryLowDiskSpace(5 * 1024 * 1024)
				.build();

		Set<RequestListener> listeners = new HashSet<>();
		listeners.add(new RequestLoggingListener());

		int MaxRequestPerTime = 64;
		SparseIntArray defaultBuckets = new SparseIntArray();
		defaultBuckets.put(16 * ByteConstants.KB, MaxRequestPerTime);
		PoolParams smallByteArrayPoolParams = new PoolParams(
				16 * ByteConstants.KB * MaxRequestPerTime,
				2 * ByteConstants.MB,
				defaultBuckets);
		PoolFactory factory = new PoolFactory(
				PoolConfig.newBuilder()
						. setSmallByteArrayPoolParams(smallByteArrayPoolParams)
						.build());

		ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
				.newBuilder(this, RetrofitManager.getInstance().getOkHttpClient())
				.setBitmapsConfig(Bitmap.Config.RGB_565)
				.setDownsampleEnabled(true)
				.setPoolFactory(factory)
				.setMainDiskCacheConfig(diskCacheConfig)
				.setRequestListeners(listeners).build();
		Fresco.initialize(this, config);
	}

	/**
	 * 初始化网络接口
	 */
	private void initNetInterface(){
		/**
		 * 用户
		 */
		UserService userService = RetrofitManager.getInstance().getRetrofitInstance().create(UserService.class);
		AppManager.setUserService(userService);
		/**
		 * 图片
		 */
		PictureService pictureService = RetrofitManager.getInstance().getRetrofitInstance().create(PictureService.class);
		AppManager.setPictureService(pictureService);
		/**
		 * 关注
		 */
		FollowService followService = RetrofitManager.getInstance().getRetrofitInstance().create(FollowService.class);
		AppManager.setFollowService(followService);
		/**
		 * 喜欢
		 */
		LoveService loveService = RetrofitManager.getInstance().getRetrofitInstance().create(LoveService.class);
		AppManager.setLoveService(loveService);
		/**
		 * 视频
		 */
		VideoService videoService = RetrofitManager.getInstance().getRetrofitInstance().create(VideoService.class);
		AppManager.setVideoService(videoService);
		/**
		 * 动态
		 */
		DynamicService dynamicService = RetrofitManager.getInstance().getRetrofitInstance().create(DynamicService.class);
		AppManager.setDynamicService(dynamicService);
	}
}
