package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.eventtype.SnackBarEvent;
import com.cyanbirds.yljy.utils.PreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * 作者：wangyb
 * 时间：2017/9/9 11:23
 * 描述：钱包
 */
public class MoneyPacketActivity extends BaseActivity {
	@BindView(R.id.my_money)
	TextView mMyMoney;
	@BindView(R.id.btn_output)
	FancyButton mBtnOutput;

	private float mMoney;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_money_pakcet);
		ButterKnife.bind(this);
		EventBus.getDefault().register(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupData();
	}

	private void setupData() {
		mMoney = PreferencesUtils.getMyMoney(this);
		mMyMoney.setText("￥" + mMoney);
	}

	@OnClick(R.id.btn_output)
	public void onViewClicked() {
		Intent intent = new Intent(this, MoneyOutputActivity.class);
		intent.putExtra(ValueKey.DATA, mMoney);
		startActivity(intent);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void showSnackBar(SnackBarEvent event) {
		mMoney = PreferencesUtils.getMyMoney(this);
		mMyMoney.setText("￥" + mMoney);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
