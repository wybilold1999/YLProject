package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.MyAttentionAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.FollowModel;
import com.cyanbirds.yljy.net.request.FollowListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.ui.widget.DividerItemDecoration;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.DensityUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 */
public class MyAttentionActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CircularProgress mCircularProgress;
    private TextView mNoUserInfo;
    private MyAttentionAdapter mAdapter;

    private int pageNo = 1;
    private int pageSize = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_attention);
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView(){
        mCircularProgress = (CircularProgress) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mNoUserInfo = (TextView) findViewById(R.id.info);
        LinearLayoutManager manager = new WrapperLinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL, DensityUtil
                .dip2px(this, 12), DensityUtil.dip2px(
                this, 12)));
    }

    private void setupEvent(){

    }

    private void setupData(){
        mAdapter = new MyAttentionAdapter(MyAttentionActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        new FollowListTask().request("followList", pageNo, pageSize);
    }

    private MyAttentionAdapter.OnItemClickListener mOnItemClickListener = new MyAttentionAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            FollowModel followModel = mAdapter.getItem(position);
            Intent intent = new Intent(MyAttentionActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(followModel.userId));
            startActivity(intent);
        }
    };

    class FollowListTask extends FollowListRequest {
        @Override
        public void onPostExecute(List<FollowModel> followModels) {
            mCircularProgress.setVisibility(View.GONE);
            if(null != followModels && followModels.size() > 0){
                mNoUserInfo.setVisibility(View.GONE);
                mAdapter.setFollowModels(followModels);
            } else {
                mNoUserInfo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onErrorExecute(String error) {
            ToastUtil.showMessage(error);
            mCircularProgress.setVisibility(View.GONE);
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
