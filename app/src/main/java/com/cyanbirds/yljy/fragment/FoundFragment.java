package com.cyanbirds.yljy.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.adapter.FoundAdapter;
import com.cyanbirds.yljy.entity.PictureModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetDiscoverInfoRequest;
import com.cyanbirds.yljy.net.request.GetRealUsersDiscoverInfoRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2015-12-20 11:33 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class FoundFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private CircularProgress mProgressBar;
    private View rootView;
    private FoundAdapter mAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private List<PictureModel> mPictureModels;
    private int pageIndex = 1;
    private int pageSize = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_found, null);
            setupViews();
            setupEvent();
            setupData();
            setHasOptionsMenu(true);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(
                R.string.tab_found);
        return rootView;
    }

    private void setupViews() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mProgressBar = (CircularProgress) rootView.findViewById(R.id.progress_bar);
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void setupEvent(){
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    private void setupData() {
        mPictureModels = new ArrayList<>();
        mAdapter = new FoundAdapter(null, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mProgressBar.setVisibility(View.VISIBLE);
        if("-1".equals(AppManager.getClientUser().userId) ||
                "-2".equals(AppManager.getClientUser().userId) ||
                "-3".equals(AppManager.getClientUser().userId)){
            new GetRealUsersDiscoverInfoTask().request(pageIndex,pageSize);
        } else {
            new GetDiscoverInfoTask().request(pageIndex,pageSize);
        }
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        private int lastVisibleItem;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //获取最后一个完全显示的ItemPosition
                int[] lastVisiblePositions = manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
                int lastVisiblePos = getMaxElem(lastVisiblePositions);
                int totalItemCount = manager.getItemCount();
                if (lastVisiblePos == (totalItemCount -1)) {
                    //加载更多
                    if("-1".equals(AppManager.getClientUser().userId)){
                        new GetRealUsersDiscoverInfoTask().request(++pageIndex,pageSize);
                    } else {
                        new GetDiscoverInfoTask().request(++pageIndex,pageSize);
                    }
                }
            }
        }
    };

    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i]>maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }

    /**
     * 获取发现信息
     */
    class GetDiscoverInfoTask extends GetDiscoverInfoRequest {
        @Override
        public void onPostExecute(List<PictureModel> pictureModels) {
            mProgressBar.setVisibility(View.GONE);
            if(pictureModels == null || pictureModels.size() == 0){
                mAdapter.setIsShowFooter(false);
                mAdapter.notifyDataSetChanged();
                ToastUtil.showMessage(R.string.no_more_data);
            } else {
                mPictureModels.addAll(pictureModels);
                mAdapter.setIsShowFooter(true);
                mAdapter.setPictureModels(mPictureModels);
            }
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    /**
     * 获取真实用户的图片
     */
    class GetRealUsersDiscoverInfoTask extends GetRealUsersDiscoverInfoRequest {
        @Override
        public void onPostExecute(List<PictureModel> pictureModels) {
            mProgressBar.setVisibility(View.GONE);
            if(pictureModels == null || pictureModels.size() == 0){
                mAdapter.setIsShowFooter(false);
                mAdapter.notifyDataSetChanged();
                ToastUtil.showMessage(R.string.no_more_data);
            } else {
                mPictureModels.addAll(pictureModels);
                mAdapter.setIsShowFooter(true);
                mAdapter.setPictureModels(mPictureModels);
            }
        }

        @Override
        public void onErrorExecute(String error) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
