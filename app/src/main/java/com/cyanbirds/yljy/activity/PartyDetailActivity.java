package com.cyanbirds.yljy.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.AppConstants;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.LoveParty;
import com.cyanbirds.yljy.entity.PayResult;
import com.cyanbirds.yljy.entity.WeChatPay;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.CreateOrderRequest;
import com.cyanbirds.yljy.net.request.GetAliPayOrderInfoRequest;
import com.cyanbirds.yljy.utils.StringUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 作者：wangyb
 * 时间：2016/12/11 23:57
 * 描述：
 */
public class PartyDetailActivity extends BaseActivity {

	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;
	@BindView(R.id.title)
	TextView mTitle;
	@BindView(R.id.time)
	TextView mTime;
	@BindView(R.id.where)
	TextView mWhere;
	@BindView(R.id.limit_count)
	TextView mLimitCount;
	@BindView(R.id.tv_party_detail)
	TextView mTvPartyDetail;
	@BindView(R.id.iv_party_1)
	SimpleDraweeView mIvParty1;
	@BindView(R.id.iv_party_2)
	SimpleDraweeView mIvParty2;
	@BindView(R.id.iv_party_3)
	SimpleDraweeView mIvParty3;
	@BindView(R.id.iv_party_4)
	SimpleDraweeView mIvParty4;
	@BindView(R.id.iv_party_5)
	SimpleDraweeView mIvParty5;
	@BindView(R.id.iv_party_6)
	SimpleDraweeView mIvParty6;
	@BindView(R.id.price_info)
	TextView mPriceInfo;
	@BindView(R.id.btn_attention)
	FancyButton mBtnAttention;

	private LoveParty mLoveParty;

	private static final int SDK_PAY_FLAG = 1;
	private static final int member_buy_id = 12;//写死，固定值

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
						ToastUtil.showMessage("支付成功");
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						ToastUtil.showMessage("支付失败");
					}
					break;
				}
				default:
					break;
			}
		}

		;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_party_detail);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		ButterKnife.bind(this);
		initData();
	}

	private void initData() {
		mLoveParty = (LoveParty) getIntent().getSerializableExtra(ValueKey.DATA);
		if (mLoveParty != null) {
			mTitle.setText(mLoveParty.title);
			mTime.setText(mLoveParty.time);
			mWhere.setText(mLoveParty.partyWhere);
			mLimitCount.setText(mLoveParty.limitCount);
			mTvPartyDetail.setText(mLoveParty.partyDetail);
			mPriceInfo.setText(mLoveParty.priceInfo);
			mPortrait.setImageURI(Uri.parse(mLoveParty.banner));
			if (mLoveParty.status) {
				mBtnAttention.setEnabled(true);
				mBtnAttention.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
				mBtnAttention.setText("开始报名");
			} else {
				mBtnAttention.setEnabled(false);
				mBtnAttention.setBackgroundColor(getResources().getColor(R.color.gray_background));
				mBtnAttention.setText("报名截止");
			}
			List<String> urls = StringUtil.stringToIntList(mLoveParty.ImgUrl);
			mIvParty1.setImageURI(Uri.parse(urls.get(0)));
			mIvParty2.setImageURI(Uri.parse(urls.get(1)));
			mIvParty3.setImageURI(Uri.parse(urls.get(2)));
			mIvParty4.setImageURI(Uri.parse(urls.get(3)));
			mIvParty5.setImageURI(Uri.parse(urls.get(4)));
			mIvParty6.setImageURI(Uri.parse(urls.get(5)));
		}
	}

	@OnClick(R.id.btn_attention)
	public void onClick() {
		showPayDialog();
	}

	private void showPayDialog() {
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
								new GetAliPayOrderInfoTask().request(member_buy_id, AppConstants.ALI_PAY_PLATFORM);
								break;
							case 1:
								new CreateOrderTask().request(member_buy_id, AppConstants.WX_PAY_PLATFORM);
								break;
						}
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 调用微信支付
	 */
	class CreateOrderTask extends CreateOrderRequest {
		@Override
		public void onPostExecute(WeChatPay weChatPay) {
			PayReq payReq = new PayReq();
			payReq.appId = AppConstants.WEIXIN_ID;
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
				PayTask alipay = new PayTask(PartyDetailActivity.this);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
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
