package com.cyanbirds.yljy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.AppointmentInfoActivity;
import com.cyanbirds.yljy.adapter.AppointmentAdapter;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.AppointmentModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetAppointmentListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.ui.widget.WrapperLinearLayoutManager;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description: 我约的
 */
public class MyAppointFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CircularProgress mProgress;
    private TextView mNoResult;
    private View rootView;

    private AppointmentAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<AppointmentModel> mAppointmentModels;
    private int pageIndex = 1;
    private int pageSize = 150;
    private static final int flag = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_appoint, null);
            setupViews();
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
        mProgress = (CircularProgress) rootView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mNoResult = (TextView) rootView.findViewById(R.id.no_result);

        layoutManager = new WrapperLinearLayoutManager(
                getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void setupData() {
        mProgress.setVisibility(View.VISIBLE);
        mAppointmentModels = new ArrayList<>();
        mAdapter = new AppointmentAdapter("", mAppointmentModels, getActivity());
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        new GetAppointmentListTask().request(pageIndex, pageSize, AppManager.getClientUser().userId, flag);
    }

    class GetAppointmentListTask extends GetAppointmentListRequest {

        @Override
        public void onPostExecute(List<AppointmentModel> appointmentModels) {
            mProgress.setVisibility(View.GONE);
            if(appointmentModels != null && appointmentModels.size() > 0){
                mNoResult.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAppointmentModels.addAll(appointmentModels);
                mAdapter.setAppointmentModels(mAppointmentModels);
            } else {
                mNoResult.setVisibility(View.VISIBLE);
                mNoResult.setText(R.string.no_my_appointment_list);
                mRecyclerView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onErrorExecute(String error) {
            ToastUtil.showMessage(error);
            mProgress.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mNoResult.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
    }

    private AppointmentAdapter.OnItemClickListener mOnItemClickListener = new AppointmentAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            AppointmentModel model = mAdapter.getItem(position);
            if (model != null) {
                Intent intent = new Intent(getActivity(), AppointmentInfoActivity.class);
                intent.putExtra(ValueKey.DATA, model);
                startActivity(intent);
            }
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
