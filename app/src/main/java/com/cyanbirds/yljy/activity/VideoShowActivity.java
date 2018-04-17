package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.VideoShowAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.VideoModel;
import com.cyanbirds.yljy.net.request.GetVideoListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/6/19.
 */
public class VideoShowActivity extends BaseActivity {

	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerview;
	@BindView(R.id.progress_bar)
	CircularProgress mProgressBar;

	private List<VideoModel> mVideoModel;
	private VideoShowAdapter mAdapter;
	private GridLayoutManager mLayoutManager;

	private int pageNo = 0;
	private int pageSize = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_show);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
		setupData();
	}

	private void setupView() {
		mLayoutManager = new GridLayoutManager(this, 3);
		mRecyclerview.setLayoutManager(mLayoutManager);
	}

	private void setupData() {
		String userId = getIntent().getStringExtra(ValueKey.USER_ID);
		mProgressBar.setVisibility(View.VISIBLE);
		new GetVideoListTask().request(userId, pageNo, pageSize);
	}

	class GetVideoListTask extends GetVideoListRequest {
		@Override
		public void onPostExecute(List<VideoModel> videoModels) {
			mProgressBar.setVisibility(View.GONE);
			mAdapter = new VideoShowAdapter(VideoShowActivity.this, videoModels);
			mRecyclerview.setAdapter(mAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
			mProgressBar.setVisibility(View.GONE);
		}
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
