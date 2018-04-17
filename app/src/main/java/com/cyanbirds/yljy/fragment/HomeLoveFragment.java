package com.cyanbirds.yljy.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.HomeTabFragmentAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.service.DownloadUpdateService;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：wangyb
 * 时间：2016/9/26 14:30
 * 描述：
 */
public class HomeLoveFragment extends Fragment {

	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.viewpager)
	ViewPager mViewpager;
	@BindView(R.id.fab)
	FloatingActionButton mFab;

	private View rootView;

	private List<String> tabList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_homelove, null);
			ButterKnife.bind(this, rootView);
			setupView();
			setupData();
			setHasOptionsMenu(true);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().show();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(
				R.string.tab_find_love);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	private void setupView() {
		tabList = new ArrayList<>();
		tabList.add("颜值");
		tabList.add("同城");
		tabList.add("全国");
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(2)));

		HomeTabFragmentAdapter fragmentAdapter = new HomeTabFragmentAdapter(
				getChildFragmentManager(), tabList);
		mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
		mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
		mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
	}

	private void setupData() {
		if (AppManager.getClientUser().versionCode > AppManager.getVersionCode()) {
			showVersionInfo();
		}
	}

	private void showVersionInfo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.new_version);
		builder.setMessage(AppManager.getClientUser().versionUpdateInfo);
		builder.setPositiveButton(getResources().getString(R.string.update),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						/**
						 * 开始下载apk文件
						 */
						Intent intent = new Intent(getActivity(), DownloadUpdateService.class);
						intent.putExtra(ValueKey.APK_URL, AppManager.getClientUser().apkUrl);
						getActivity().startService(intent);
						ToastUtil.showMessage(R.string.bg_downloading);
//						AppManager.goToMarket(getActivity(), channel);
					}
				});
		if (!AppManager.getClientUser().isForceUpdate) {
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		}
		builder.setCancelable(false);
		builder.show();
	}

	@OnClick(R.id.fab)
	public void onClick() {
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
