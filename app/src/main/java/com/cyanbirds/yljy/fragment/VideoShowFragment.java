package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.VideoTabFragmentAdapter;
import com.cyanbirds.yljy.manager.AppManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-06-22 17:15 GMT+8
 * @description
 */
public class VideoShowFragment extends Fragment {
	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.viewpager)
	ViewPager mViewpager;
	private View rootView;

	private List<String> tabList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_video_show, null);
			ButterKnife.bind(this, rootView);
			setupView();
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().hide();
		return rootView;
	}

	private void setupView() {
		tabList = new ArrayList<>();
		tabList.add("最新");
		tabList.add("最热");
		if ("男".equals(AppManager.getClientUser().sex)) {
			tabList.add("热舞");
			tabList.add("制服");
			tabList.add("性感");
			tabList.add("清纯");
		}
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));
		if ("男".equals(AppManager.getClientUser().sex)) {
			mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(3)));
			mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(4)));
			mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(5)));
		}

		VideoTabFragmentAdapter fragmentAdapter = new VideoTabFragmentAdapter(
				getChildFragmentManager(), tabList);
		mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				mViewpager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
