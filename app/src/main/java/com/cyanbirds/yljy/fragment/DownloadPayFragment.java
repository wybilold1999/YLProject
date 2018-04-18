package com.cyanbirds.yljy.fragment;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.DownloadPayAdapter;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.db.MyGoldDaoManager;
import com.cyanbirds.yljy.entity.Gold;
import com.cyanbirds.yljy.entity.MemberBuy;
import com.cyanbirds.yljy.entity.PayResult;
import com.cyanbirds.yljy.entity.UserVipModel;
import com.cyanbirds.yljy.entity.WeChatPay;
import com.cyanbirds.yljy.eventtype.PayEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.CreateOrderRequest;
import com.cyanbirds.yljy.net.request.GetAliPayOrderInfoRequest;
import com.cyanbirds.yljy.net.request.GetMemberBuyListRequest;
import com.cyanbirds.yljy.net.request.GetPayResultRequest;
import com.cyanbirds.yljy.net.request.GetUserNameRequest;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sunfusheng.marqueeview.MarqueeView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class DownloadPayFragment extends Fragment{

	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;
	@BindView(R.id.user_name)
	TextView mUserName;
	@BindView(R.id.user_info)
	TextView mInfo;
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerView;
	@BindView(R.id.vip_info_first)
	TextView mVipInfoFirst;
	@BindView(R.id.vip_info_sec)
	TextView mVipInfoSec;
	@BindView(R.id.marqueeView)
	MarqueeView mMarqueeView;
	@BindView(R.id.btn_pay)
	FancyButton mBtnPay;
	@BindView(R.id.select_alipay)
	CheckBox mSelectAlipay;
	@BindView(R.id.alipay_lay)
	RelativeLayout mAlipayLay;
	@BindView(R.id.select_wechatpay)
	CheckBox mSelectWechatpay;
	@BindView(R.id.wechat_lay)
	RelativeLayout mWechatLay;
	@BindView(R.id.pay_lay)
	LinearLayout mPayLay;
	@BindView(R.id.alipay_lay_info)
	TextView mAliPayInfo;

	private View rootView;

	private DownloadPayAdapter mAdapter;
	private MemberBuy memberBuy;//当前选中的商品
	private String mPayType;//支付方式

	private static final int SDK_PAY_FLAG = 1;
	private final int DOWNLOAD_VIP = 1;//服务器取下载赚钱的商品

	private List<String> nameList;

	/**
	 * 下载赚钱会员
	 */
	private final int TYPE = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SDK_PAY_FLAG: {
					@SuppressWarnings("unchecked")
					PayResult payResult = new PayResult((Map<String, String>) msg.obj);
					/**
					 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
					 */
					String resultInfo = payResult.getResult();// 同步返回需要验证的信息
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为9000则代表支付成功
					if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
						ToastUtil.showMessage(R.string.pay_success);
						if (memberBuy != null) {
							new GetPayResultTask().request();
						}
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						ToastUtil.showMessage(R.string.pay_ali_failure);
					}
					break;
				}
				default:
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_pay, null);
			ButterKnife.bind(this, rootView);
			setupViews();
			setupEvent();
			setupData();
			setHasOptionsMenu(true);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	private void setupViews() {
		LinearLayoutManager manager = new WrapperLinearLayoutManager(getActivity());
		manager.setOrientation(LinearLayout.VERTICAL);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		if (AppManager.getClientUser().isShowLovers) {
			mPayLay.setVisibility(View.VISIBLE);
		} else {
			mPayLay.setVisibility(View.GONE);
		}
	}

	private void setupEvent() {
		EventBus.getDefault().register(this);
	}

	private void setupData() {
		mUserName.setText(AppManager.getClientUser().user_name);
		if (AppManager.getClientUser().is_download_vip) {
			mInfo.setText("您已经是赚钱会员了哦!");
		} else {
			mInfo.setText("您还不是赚钱会员哦，赶快开通会员去赚钱吧!");
		}
		String imgPath = "";
		if (!TextUtils.isEmpty(AppManager.getClientUser().face_local) &&
				new File(AppManager.getClientUser().face_local).exists()) {
			imgPath = "file://" + AppManager.getClientUser().face_local;
		} else {
			imgPath = AppManager.getClientUser().face_url;
		}
		mPortrait.setImageURI(Uri.parse(imgPath));
		//获取商品列表
		new GetMemberBuyListTask().request(DOWNLOAD_VIP);
		//获取用户名称
		new GetUserNameTask().request(1, 100);
		/**
		 * 默认支付宝支付
		 */
		mPayType = AppConstants.ALI_PAY_PLATFORM;
		mSelectAlipay.setChecked(true);
		mSelectWechatpay.setChecked(false);
	}

	@OnClick({R.id.btn_pay, R.id.select_alipay, R.id.alipay_lay, R.id.select_wechatpay, R.id.wechat_lay})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.select_alipay:
				mPayType = AppConstants.ALI_PAY_PLATFORM;
				mSelectAlipay.setChecked(true);
				mSelectWechatpay.setChecked(false);
				break;
			case R.id.alipay_lay:
				mPayType = AppConstants.ALI_PAY_PLATFORM;
				mSelectAlipay.setChecked(true);
				mSelectWechatpay.setChecked(false);
				break;
			case R.id.select_wechatpay:
				mPayType = AppConstants.WX_PAY_PLATFORM;
				mSelectAlipay.setChecked(false);
				mSelectWechatpay.setChecked(true);
				break;
			case R.id.wechat_lay:
				mPayType = AppConstants.WX_PAY_PLATFORM;
				mSelectAlipay.setChecked(false);
				mSelectWechatpay.setChecked(true);
				break;
			case R.id.btn_pay:
				if (null != memberBuy) {
					if (mPayType.equals(AppConstants.ALI_PAY_PLATFORM)) {
						new GetAliPayOrderInfoTask().request(memberBuy.id, AppConstants.ALI_PAY_PLATFORM);
					} else {
						new CreateOrderTask().request(memberBuy.id, AppConstants.WX_PAY_PLATFORM);
					}
				}
				break;
		}
	}


	/**
	 * 获取赚钱会员商品
	 */
	class GetMemberBuyListTask extends GetMemberBuyListRequest {
		@Override
		public void onPostExecute(List<MemberBuy> memberBuys) {
			if (memberBuys != null && !memberBuys.isEmpty()) {
				memberBuys.get(0).isSelected = true;
				memberBuy = memberBuys.get(0);
				mAdapter = new DownloadPayAdapter(memberBuys, getActivity());
				mAdapter.setOnItemClickListener(mOnItemClickListener);
				mRecyclerView.setAdapter(mAdapter);
				mVipInfoFirst.setText(memberBuys.get(1).preferential);
				mVipInfoSec.setText(memberBuys.get(0).preferential);

				if (memberBuys.get(0).isShowAli) {
					double price = memberBuy.price - memberBuy.aliPrice;
					mAliPayInfo.setText(String.format(
							getResources().getString(R.string.pay_info), String.valueOf(price)));
					mAliPayInfo.setVisibility(View.VISIBLE);
				} else {
					mAliPayInfo.setVisibility(View.GONE);
				}
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	/**
	 * 获取用户名
	 */
	class GetUserNameTask extends GetUserNameRequest {
		@Override
		public void onPostExecute(List<String> strings) {
			if (strings != null && strings.size() > 0) {
				List<String> turnOnNameList = new ArrayList<>();
				for (String name : strings) {
					turnOnNameList.add(name + " 开通了赚钱会员，赶快去和TA聊天吧！");
				}
				mMarqueeView.startWithList(turnOnNameList);
			} else {
				setUserName();
			}
		}

		@Override
		public void onErrorExecute(String error) {
			setUserName();
		}
	}

	private void setUserName() {
		nameList = new ArrayList<>();
		int[] array = new int[]{60, 150};
		nameList.add("旧情 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("秋叶 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("敏 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("粉红琵琶 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("馨忆 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("琳琳 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("山楂 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("云云 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("雪花 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("糖宝宝 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("小洁 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("等你 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("王丫丫 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("潮汐 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("昔日 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("丹樱。。。 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("欢欢 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("葛葛 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("小淘气 开通了赚钱会员，赶快去和TA聊天吧！");
		nameList.add("尹子莲 开通了赚钱会员，赶快去和TA聊天吧！");
		if (null != mMarqueeView) {
			mMarqueeView.startWithList(nameList);
		}
	}

	private DownloadPayAdapter.OnItemClickListener mOnItemClickListener = new DownloadPayAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			memberBuy = mAdapter.getItem(position);
			if (memberBuy.isShowAli) {
				double price = memberBuy.price - memberBuy.aliPrice;
				mAliPayInfo.setText(String.format(
						getResources().getString(R.string.pay_info), String.valueOf(price)));
				mAliPayInfo.setVisibility(View.VISIBLE);
			} else {
				mAliPayInfo.setVisibility(View.GONE);
			}
		}
	};


	class CreateOrderTask extends CreateOrderRequest {
		@Override
		public void onPostExecute(WeChatPay weChatPay) {
			PayReq payReq = new PayReq();
			payReq.appId = AppConstants.WEIXIN_PAY_ID;
			payReq.partnerId = weChatPay.mch_id;
			payReq.prepayId = weChatPay.prepay_id;
			payReq.packageValue = "Sign=WXPay";
			payReq.nonceStr = weChatPay.nonce_str;
			payReq.timeStamp = weChatPay.timeStamp;
			payReq.sign = weChatPay.appSign;
			AppManager.getIWX_PAY_API().sendReq(payReq);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	/**
	 * 调用支付宝支付
	 */
	class GetAliPayOrderInfoTask extends GetAliPayOrderInfoRequest {
		@Override
		public void onPostExecute(String s) {
			payV2(s);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}
	/**
	 * 支付宝支付业务
	 *
	 * @param orderInfo
	 */
	public void payV2(final String orderInfo) {
		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 * orderInfo的获取必须来自服务端；
		 */
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask alipay = new PayTask(getActivity());
				Map<String, String> result = alipay.payV2(orderInfo, true);
				Log.d("test", result.toString());

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void paySuccess(PayEvent event) {
		if (memberBuy != null) {
			new GetPayResultTask().request();
		}
	}

	class GetPayResultTask extends GetPayResultRequest {
		int clickCount = 0;
		public GetPayResultTask() {
		}

		@Override
		public void onPostExecute(UserVipModel userVipModel) {
			AppManager.getClientUser().is_vip = userVipModel.isVip;
			AppManager.getClientUser().is_download_vip = userVipModel.isDownloadVip;
			AppManager.getClientUser().gold_num = userVipModel.goldNum;
			/**
			 * 开通赚钱会员之后，更新本地拥有的金币数量和每天可以点击的最大次数
			 */
			if (memberBuy.price == 500) {
				clickCount = 25;
			} else {
				clickCount = 15;
			}
			Gold gold = MyGoldDaoManager.getInstance(getActivity()).getMyGold();
			if (gold == null) {
				gold = new Gold();
				gold.clickCount = clickCount;
				if (clickCount == 25) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(getActivity()).insertGold(gold);
			} else {
				gold.clickCount = clickCount;
				if (clickCount == 25) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(getActivity()).updateGold(gold);
			}
			Snackbar.make(rootView.findViewById(R.id.download_pay_layout),
					"您已经是赚钱会员了，赶快去下载赚钱吧", Snackbar.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	/*class UpdateVipTask extends UpdateVipRequest {
		int clickCount;

		public UpdateVipTask(int clickCount) {
			this.clickCount = clickCount;
		}

		@Override
		public void onPostExecute(String s) {
			AppManager.getClientUser().is_download_vip = true;
			AppManager.getClientUser().gold_num = 2500;

			*//**
			 * 开通赚钱会员之后，更新本地拥有的金币数量和每天可以点击的最大次数
			 *//*
			Gold gold = MyGoldDaoManager.getInstance(getActivity()).getMyGold();
			if (gold == null) {
				gold = new Gold();
				gold.clickCount = clickCount;
				if (clickCount == 50) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(getActivity()).insertGold(gold);
			} else {
				gold.clickCount = clickCount;
				if (clickCount == 50) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(getActivity()).updateGold(gold);
			}
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}*/

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(this.getClass().getName());
	}

}
