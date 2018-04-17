package com.cyanbirds.yljy.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.BetweenLoversAdapter;
import com.cyanbirds.yljy.adapter.LoverMemberBuyAdapter;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.db.MyGoldDaoManager;
import com.cyanbirds.yljy.entity.BetweenLovers;
import com.cyanbirds.yljy.entity.Gold;
import com.cyanbirds.yljy.entity.MemberBuy;
import com.cyanbirds.yljy.entity.PayResult;
import com.cyanbirds.yljy.entity.UserVipModel;
import com.cyanbirds.yljy.entity.WeChatPay;
import com.cyanbirds.yljy.eventtype.PayEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.CreateOrderRequest;
import com.cyanbirds.yljy.net.request.GetAliPayOrderInfoRequest;
import com.cyanbirds.yljy.net.request.GetBetweenLoversInfoRequest;
import com.cyanbirds.yljy.net.request.GetMemberBuyListRequest;
import com.cyanbirds.yljy.net.request.GetPayResultRequest;
import com.cyanbirds.yljy.ui.widget.DividerItemDecoration;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.DensityUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：wangyb
 * 时间：2016/11/2 15:56
 * 描述：红娘尊贵服务
 */
public class BetweenLoversActivity extends BaseActivity {

	@BindView(R.id.vip_recyclerview)
	RecyclerView mVipRecyclerview;
	@BindView(R.id.love_recyclerview)
	RecyclerView mLoveRecyclerview;
	
	private LoverMemberBuyAdapter mVipAdapter;
	private LinearLayoutManager layoutManager;

	private BetweenLoversAdapter mLoversAdapter;
	private GridLayoutManager mLayoutManager;

	private MemberBuy memberBuy;//当前选中的商品

	private final int LOVERS = 3;
	private static final int SDK_PAY_FLAG = 1;

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
						new GetPayResultTask().request();
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_between_lovers);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
		setupData();
	}

	private void setupView() {
		layoutManager = new WrapperLinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mVipRecyclerview.setLayoutManager(layoutManager);
		mVipRecyclerview.setItemAnimator(new DefaultItemAnimator());
		mVipRecyclerview.setNestedScrollingEnabled(false);
		mVipRecyclerview.addItemDecoration(new DividerItemDecoration(
				this, LinearLayoutManager.VERTICAL, DensityUtil
				.dip2px(this, 12), DensityUtil.dip2px(
				this, 12)));

		mLayoutManager = new GridLayoutManager(this, 2);
		mLoveRecyclerview.setLayoutManager(mLayoutManager);
		mLoveRecyclerview.setNestedScrollingEnabled(false);
	}

	private void setupData() {
		EventBus.getDefault().register(this);
		new GetMemberBuyListTask().request(LOVERS);
		new GetBetweenLoversInfoTask().request();
	}

	class GetMemberBuyListTask extends GetMemberBuyListRequest {
		@Override
		public void onPostExecute(List<MemberBuy> memberBuys) {
			mVipAdapter = new LoverMemberBuyAdapter(memberBuys);
			mVipAdapter.setOnItemClickListener(mOnItemClickListener);
			mVipRecyclerview.setAdapter(mVipAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	class GetBetweenLoversInfoTask extends GetBetweenLoversInfoRequest {
		@Override
		public void onPostExecute(List<BetweenLovers> betweenLoverses) {
			mLoversAdapter = new BetweenLoversAdapter(betweenLoverses);
			mLoveRecyclerview.setAdapter(mLoversAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	private LoverMemberBuyAdapter.OnItemClickListener mOnItemClickListener = new LoverMemberBuyAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			memberBuy = mVipAdapter.getItem(position);
			showPayDialog(memberBuy);
		}
	};

	private void showPayDialog(final MemberBuy memberBuy) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.pay_type));
		builder.setNegativeButton(getResources().getString(R.string.cancel),
				null);
		builder.setItems(
				new String[]{getResources().getString(R.string.ali_pay),
						getResources().getString(R.string.weixin_pay)},
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								new GetAliPayOrderInfoTask().request(memberBuy.id, AppConstants.ALI_PAY_PLATFORM);
								break;
							case 1:
								new CreateOrderTask().request(memberBuy.id, AppConstants.WX_PAY_PLATFORM);
								break;
						}
						dialog.dismiss();
					}
				});
		builder.show();
	}

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
				PayTask alipay = new PayTask(BetweenLoversActivity.this);
				Map<String, String> result = alipay.payV2(orderInfo, true);

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
		new GetPayResultTask().request();
	}

	class GetPayResultTask extends GetPayResultRequest {
		int clickCount = 0;

		@Override
		public void onPostExecute(UserVipModel userVipModel) {
			AppManager.getClientUser().is_vip = userVipModel.isVip;
			AppManager.getClientUser().is_download_vip = userVipModel.isDownloadVip;
			AppManager.getClientUser().gold_num = userVipModel.goldNum;
			/**
			 * 开通赚钱会员之后，更新本地拥有的金币数量和每天可以点击的最大次数
			 */
			if (memberBuy.price == 1000) {
				clickCount = 25;
			} else {
				clickCount = 15;
			}
			Gold gold = MyGoldDaoManager.getInstance(BetweenLoversActivity.this).getMyGold();
			if (gold == null) {
				gold = new Gold();
				gold.clickCount = clickCount;
				if (clickCount == 25) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(BetweenLoversActivity.this).insertGold(gold);
			} else {
				gold.clickCount = clickCount;
				if (clickCount == 25) {
					gold.vipFlag = 2;
				} else {
					gold.vipFlag = 1;
				}
				MyGoldDaoManager.getInstance(BetweenLoversActivity.this).updateGold(gold);
			}
			Snackbar.make(findViewById(R.id.between_layout),
					"您已经是红娘尊贵会员了，即将摆脱单身", Snackbar.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
