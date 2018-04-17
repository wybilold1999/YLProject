package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.ClickBusinessAdapter;
import com.cyanbirds.yljy.entity.ApkInfo;
import com.cyanbirds.yljy.net.request.GetDownloadAppListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class ClickBusinessFragment extends Fragment{
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerView;
	@BindView(R.id.progress_bar)
	CircularProgress mProgressBar;

	private View rootView;

	private ClickBusinessAdapter mAdapter;

	private int pageNo = 1;
	private int pageSize = 20;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_click_business, null);
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
		GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setNestedScrollingEnabled(false);
	}

	private void setupEvent() {
	}

	private void setupData() {
		mProgressBar.setVisibility(View.VISIBLE);
		new GetAppListTask().request(pageNo, pageSize);
	}

	class GetAppListTask extends GetDownloadAppListRequest {
		@Override
		public void onPostExecute(List<ApkInfo> apkInfos) {
			mProgressBar.setVisibility(View.GONE);
			mAdapter = new ClickBusinessAdapter(apkInfos , getActivity(), mRecyclerView);
			mAdapter.setOnItemClickListener(mOnItemClickListener);
			mRecyclerView.setAdapter(mAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	private ClickBusinessAdapter.OnItemClickListener mOnItemClickListener = new ClickBusinessAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
		}
	};

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
