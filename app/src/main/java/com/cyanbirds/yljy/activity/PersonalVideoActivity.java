package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.TabFragmentAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.UserVideoPhotoModel;
import com.cyanbirds.yljy.fragment.TabPhotosFragment;
import com.cyanbirds.yljy.fragment.TabVideoFragment;
import com.cyanbirds.yljy.fragment.TabVideoPhotosFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：wangyb
 * 时间：2016/12/5 17:50
 * 描述：用户视频和相册信息
 */
public class PersonalVideoActivity extends BaseActivity {
	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;
	@BindView(R.id.user_name)
	TextView mUserName;
	@BindView(R.id.age)
	TextView mAge;
	@BindView(R.id.occupation)
	TextView mOccupation;
	@BindView(R.id.tabs)
	TabLayout mTabLayout;
	@BindView(R.id.viewpager)
	ViewPager mViewpager;

	private List<String> tabList;
	private List<Fragment> fragmentList;
	private Fragment videoFragment;//视频tab
	private Fragment photoFragment; //相册tab

	private UserVideoPhotoModel mModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_video);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
	}

	private void setupView() {
		tabList = new ArrayList<>();
		tabList.add("热门视频");
		tabList.add("私房美照");
		mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
		mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));

		fragmentList = new ArrayList<>();
		videoFragment = new TabVideoFragment();
		photoFragment = new TabVideoPhotosFragment();
		fragmentList.add(videoFragment);
		fragmentList.add(photoFragment);

		mModel = (UserVideoPhotoModel) getIntent().getSerializableExtra(ValueKey.USER);
		if (mModel != null) {
			if (!TextUtils.isEmpty(mModel.faceUrl)) {
				mPortrait.setImageURI(Uri.parse(mModel.faceUrl));
			}
			mUserName.setText(mModel.nickName);
			mAge.setText(mModel.age);
			mOccupation.setText(mModel.occupation);

			Bundle videoBundle = new Bundle();
			videoBundle.putSerializable(ValueKey.VIDEO_LIST, mModel);
			videoFragment.setArguments(videoBundle);

			Bundle photoBundle = new Bundle();
			photoBundle.putStringArrayList(ValueKey.IMAGE_URL, (ArrayList<String>) mModel.photoUrl);
			photoFragment.setArguments(photoBundle);

			TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(
					getSupportFragmentManager(), fragmentList, tabList);
			mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
			mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
			mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
		}
	}

	@OnClick(R.id.portrait)
	public void onClick() {
		Intent intent = new Intent(this, PhotoViewActivity.class);
		intent.putExtra(ValueKey.IMAGE_URL, mModel.faceUrl);
		intent.putExtra(ValueKey.FROM_ACTIVITY, this.getClass().getSimpleName());
		startActivity(intent);
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
