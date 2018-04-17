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
import com.cyanbirds.yljy.adapter.MyGiftsAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.ReceiveGiftModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GiftsListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.ui.widget.DividerItemDecoration;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.DensityUtil;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-01-13 22:17 GMT+8
 * @email 395044952@qq.com
 */
public class MyGiftsActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private CircularProgress mCircularProgress;
    private TextView mNoUserInfo;
    private MyGiftsAdapter mAdapter;

    private int pageNo = 1;
    private int pageSize = 13;
    private List<ReceiveGiftModel> mReceiveGiftModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_gifts);
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
        if (AppManager.getClientUser().isShowVip) {
            if (AppManager.getClientUser().is_vip) {
                pageSize = 200;
            }
        } else {
            pageSize = 200;
        }
        mReceiveGiftModels = new ArrayList<>();
        mAdapter = new MyGiftsAdapter(MyGiftsActivity.this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mCircularProgress.setVisibility(View.VISIBLE);
        new MyGiftListTask().request(pageNo, pageSize);
    }

    private MyGiftsAdapter.OnItemClickListener mOnItemClickListener = new MyGiftsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ReceiveGiftModel receiveGiftModel = mAdapter.getItem(position);
            Intent intent = new Intent(MyGiftsActivity.this, PersonalInfoActivity.class);
            intent.putExtra(ValueKey.USER_ID, String.valueOf(receiveGiftModel.userId));
            startActivity(intent);
        }
    };

    class MyGiftListTask extends GiftsListRequest {
        @Override
        public void onPostExecute(List<ReceiveGiftModel> receiveGiftModels) {
            mCircularProgress.setVisibility(View.GONE);
            if(null != receiveGiftModels && receiveGiftModels.size() > 0){
                if (AppManager.getClientUser().isShowVip &&
                        !AppManager.getClientUser().is_vip &&
                        receiveGiftModels.size() > 10) {//如果不是vip，移除前面3个
                    mAdapter.setIsShowFooter(true);
                    List<String> urls = new ArrayList<>(3);
                    urls.add(receiveGiftModels.get(0).faceUrl);
                    urls.add(receiveGiftModels.get(1).faceUrl);
                    urls.add(receiveGiftModels.get(2).faceUrl);
                    mAdapter.setFooterFaceUrls(urls);
                    receiveGiftModels.remove(0);
                    receiveGiftModels.remove(1);
                }
                mCircularProgress.setVisibility(View.GONE);
                mReceiveGiftModels.addAll(receiveGiftModels);
                mAdapter.setReceiveGiftModel(mReceiveGiftModels);
            } else {
                if (receiveGiftModels != null) {
                    mReceiveGiftModels.addAll(receiveGiftModels);
                }
                mAdapter.setIsShowFooter(false);
                mAdapter.setReceiveGiftModel(mReceiveGiftModels);
            }
            if (mReceiveGiftModels != null && mReceiveGiftModels.size() > 0) {
                mNoUserInfo.setVisibility(View.GONE);
            } else {
                mNoUserInfo.setText("您还没有收到礼物哦");
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
