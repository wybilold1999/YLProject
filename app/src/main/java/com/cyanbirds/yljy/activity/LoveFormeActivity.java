package com.cyanbirds.yljy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.LoveFormeAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.LoveModel;
import com.cyanbirds.yljy.net.request.GetLoveFormeListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.ui.widget.DividerItemDecoration;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.DensityUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 * 谁赞了我
 */
public class LoveFormeActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgress mCircularProgress;
    @BindView(R.id.info)
    TextView mNoUserinfo;
    private LoveFormeAdapter mAdapter;

    private List<LoveModel> mLoveModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_me);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView() {
        mCircularProgress = (CircularProgress) findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mNoUserinfo = (TextView) findViewById(R.id.info);
        LinearLayoutManager manager = new WrapperLinearLayoutManager(this);
        manager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                this, LinearLayoutManager.VERTICAL, DensityUtil
                .dip2px(this, 12), DensityUtil.dip2px(
                this, 12)));
    }

    private void setupEvent() {

    }

    private void setupData() {
        mAdapter = new LoveFormeAdapter(LoveFormeActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        new GetLoveFormeListTask().request(1, 100);
    }

    private LoveFormeAdapter.OnItemClickListener mOnItemClickListener = new LoveFormeAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            LoveModel loveModel = mLoveModels.get(position);
            Intent intent = new Intent(LoveFormeActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(loveModel.userId));
            startActivity(intent);
        }
    };

    class GetLoveFormeListTask extends GetLoveFormeListRequest {
        @Override
        public void onPostExecute(List<LoveModel> loveModels) {
            mCircularProgress.setVisibility(View.GONE);
            if(loveModels != null && loveModels.size() > 0){
                mNoUserinfo.setVisibility(View.GONE);
                mLoveModels = loveModels;
                mAdapter.setLoveModels(loveModels);
            } else {
                mNoUserinfo.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onErrorExecute(String error) {
            mCircularProgress.setVisibility(View.GONE);
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
