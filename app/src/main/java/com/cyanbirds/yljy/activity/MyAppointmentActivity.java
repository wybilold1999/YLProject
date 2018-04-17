package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.TabFragmentAdapter;
import com.cyanbirds.yljy.fragment.AppointMeFragment;
import com.cyanbirds.yljy.fragment.MyAppointFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 */
public class MyAppointmentActivity extends BaseActivity {

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
	}

	private void setupView() {
		tabList = new ArrayList<>(2);
		fragmentList = new ArrayList<>(2);
		tabList.add("我约的");
		tabList.add("约我的");
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));

		AppointMeFragment appointMeFragment = new AppointMeFragment();
		MyAppointFragment myAppointFragment = new MyAppointFragment();
		fragmentList.add(myAppointFragment);
		fragmentList.add(appointMeFragment);

		TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(
				getSupportFragmentManager(), fragmentList, tabList);
		mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
		mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
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
