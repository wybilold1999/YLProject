package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.SuccessCaseAdapter;
import com.cyanbirds.yljy.entity.SuccessCase;
import com.cyanbirds.yljy.net.request.GetSuccessCaseListRequest;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者：wangyb
 * 时间：2016/11/2 15:56
 * 描述：成功案例
 */
public class SuccessCaseActivity extends BaseActivity {

	@BindView(R.id.recyclerview)
	RecyclerView mRecyclerView;

	private SuccessCaseAdapter mAdapter;
	private LinearLayoutManager layoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_success_case);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
		setupData();
	}

	private void setupView() {
		layoutManager = new WrapperLinearLayoutManager(
				this, LinearLayoutManager.VERTICAL, false);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setNestedScrollingEnabled(false);
	}

	private void setupData() {
		new GetSuccessCaseListTask().request();
	}

	class GetSuccessCaseListTask extends GetSuccessCaseListRequest {
		@Override
		public void onPostExecute(List<SuccessCase> successCases) {
			mAdapter = new SuccessCaseAdapter(successCases, SuccessCaseActivity.this);
			mRecyclerView.setAdapter(mAdapter);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
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
