package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.TabFragmentAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.eventtype.MakeMoneyEvent;
import com.cyanbirds.yljy.eventtype.SnackBarEvent;
import com.cyanbirds.yljy.fragment.ClickBusinessFragment;
import com.cyanbirds.yljy.fragment.DownloadAppFragment;
import com.cyanbirds.yljy.fragment.DownloadBanlanceFragment;
import com.cyanbirds.yljy.fragment.DownloadPayFragment;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 */
public class MakeMoneyActivity extends BaseActivity {

	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.viewpager)
	ViewPager mViewpager;

	private List<String> tabList;
	private List<Fragment> fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_money);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
		setupEvent();
		setupData();
	}

	private void setupView() {
		tabList = new ArrayList<>(3);
		fragmentList = new ArrayList<>(3);
		tabList.add("免费赚钱");
		tabList.add("赚更多钱");
		tabList.add("余额提现");
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(2)));

		ClickBusinessFragment downloadAppFragment = new ClickBusinessFragment();
		DownloadPayFragment downloadPayFragment = new DownloadPayFragment();
		DownloadBanlanceFragment downloadBanlanceFragment = new DownloadBanlanceFragment();
		fragmentList.add(downloadAppFragment);
		fragmentList.add(downloadPayFragment);
		fragmentList.add(downloadBanlanceFragment);

		TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(
				getSupportFragmentManager(), fragmentList, tabList);
		mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
		mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
	}

	private void setupEvent() {
		EventBus.getDefault().register(this);
	}

	private void setupData() {
		String from = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);
		if (!TextUtils.isEmpty(from)) {
			mViewpager.setCurrentItem(1, true);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void showMoneyDialog(MakeMoneyEvent event) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getResources().getString(R.string.make_money_tips));
		builder.setMessage(this.getResources().getString(R.string.make_money_tips_content));
		builder.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mViewpager.setCurrentItem(1, true);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void showSnackBar(SnackBarEvent event) {
		Snackbar.make(findViewById(R.id.download_co_layout), "账户余额增加2元", Snackbar.LENGTH_SHORT)
				.setActionTextColor(Color.RED)
				.setAction("点击查看", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mViewpager.setCurrentItem(2, true);
					}
				}).show();
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
