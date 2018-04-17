package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.TabVideoAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.UserVideoPhotoModel;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:32 GMT+8
 * @email: 395044952@qq.com
 * @description: 个人视频
 */
public class TabVideoFragment extends Fragment {
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerView;
	@BindView(R.id.tv_tab_content)
	TextView mTvTabContent;
	private View rootView;

	private UserVideoPhotoModel mModel;

	private TabVideoAdapter mAdapter;
	private StaggeredGridLayoutManager layoutManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.tab_item_video, null);
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
		return rootView;
	}

	private void setupViews() {
		layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setHasFixedSize(false);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setNestedScrollingEnabled(false);
	}

	private void setupEvent() {
	}

	private void setupData() {
		if (getArguments() != null) {
			mModel = (UserVideoPhotoModel) getArguments().getSerializable(ValueKey.VIDEO_LIST);
			if (null != mModel) {
				mAdapter = new TabVideoAdapter(getActivity(), mModel);
				mRecyclerView.setAdapter(mAdapter);
			}
		}
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
