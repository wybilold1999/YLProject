package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.TabPhotosAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:32 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class TabVideoPhotosFragment extends Fragment {
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;
	@BindView(R.id.tv_tab_content)
	TextView mTvTabContent;
	private View rootView;

	private List<String> mData;

	private TabPhotosAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.tab_item_photos, null);
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
		mRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
	}

	private void setupEvent() {
	}

	private void setupData() {
		mData = getArguments().getStringArrayList(ValueKey.IMAGE_URL);
		mAdapter = new TabPhotosAdapter(getActivity(), mData);
		mRecyclerview.setAdapter(mAdapter);
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
