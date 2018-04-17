package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.DownloadAppAdapter;
import com.cyanbirds.yljy.entity.ApkInfo;
import com.cyanbirds.yljy.net.request.GetDownloadAppListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
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
public class DownloadAppFragment extends Fragment implements OnRefreshListener {
	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerView;
	@BindView(R.id.swipe_refresh)
	SwipeRefreshLayout mSwipeRefresh;
	@BindView(R.id.progress_bar)
	CircularProgress mProgressBar;

	private View rootView;

	private DownloadAppAdapter mAdapter;

	private int pageNo = 1;
	private int pageSize = 30;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_download_app, null);
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
		LinearLayoutManager manager = new WrapperLinearLayoutManager(getActivity());
		manager.setOrientation(LinearLayout.VERTICAL);
		mRecyclerView.setLayoutManager(manager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	private void setupEvent() {
		mSwipeRefresh.setOnRefreshListener(this);
	}

	private void setupData() {
		new GetAppListTask().request(pageNo, pageSize);
	}

	class GetAppListTask extends GetDownloadAppListRequest {
		@Override
		public void onPostExecute(List<ApkInfo> apkInfos) {
			mSwipeRefresh.setRefreshing(false);
			mAdapter = new DownloadAppAdapter(apkInfos , getActivity(), mRecyclerView);
			mAdapter.setOnItemClickListener(mOnItemClickListener);
			mRecyclerView.setAdapter(mAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
		}
	}

	private DownloadAppAdapter.OnItemClickListener mOnItemClickListener = new DownloadAppAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
		}
	};

	@Override
	public void onRefresh() {
		new GetAppListTask().request(++pageNo, pageSize);
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
