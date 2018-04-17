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
import com.cyanbirds.yljy.adapter.VideoDetailAdapter;
import com.cyanbirds.yljy.entity.VideoModel;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetTypeVideoListRequest;
import com.cyanbirds.yljy.ui.widget.CircularProgress;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * @author Cloudsoar(wangyb)
 * @datetime 2016-06-22 17:15 GMT+8
 * @description
 */
public class VideoDetailFragment extends Fragment {
    private RecyclerView mRecyclerview;
    private CircularProgress mProgressBar;

    private View rootView;
    private VideoDetailAdapter mAdapter;
    private int pageIndex = 1;
    private int pageNo = 100;
    private int videoType = 0;
    public static final int NEW = 0;//最新
    public static final int HOT = 1;//最热
    public static final int DANCING = 2;//热舞
    public static final int CLOTH = 3;//制服
    public static final int SEXY = 4;//性感
    public static final int PURE = 5;//清纯

    private List<VideoModel> mVideoModels;

    private StaggeredGridLayoutManager layoutManager;

    private static final String FRAGMENT_INDEX = "fragment_index";
    private int mCurIndex = -1;
    public static VideoDetailFragment newInstance(int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_INDEX, index);
        VideoDetailFragment fragment = new VideoDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_video_detail, null);
            setupView();
            setupData();
            setHasOptionsMenu(true);
            ButterKnife.bind(this, rootView);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    private void setupView(){
        mRecyclerview = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mProgressBar = (CircularProgress) rootView.findViewById(R.id.progress_bar);
    }

    private void setupData(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurIndex = bundle.getInt(FRAGMENT_INDEX);
        }
        switch (mCurIndex) {
            case 0 :
                videoType = NEW;
                break;
            case 1 :
                videoType = HOT;
                break;
            case 2 :
                videoType = DANCING;
                break;
            case 3 :
                videoType = CLOTH;
                break;
            case 4 :
                videoType = SEXY;
                break;
            case 5 :
                videoType = PURE;
                break;
        }
        mVideoModels = new ArrayList<>();
        mAdapter = new VideoDetailAdapter(getActivity(), null);
        layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setHasFixedSize(false);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.addOnScrollListener(mOnScrollListener);
        mProgressBar.setVisibility(View.VISIBLE);
        new GetVideoListTask().request(pageIndex, pageNo, videoType);
    }

    class GetVideoListTask extends GetTypeVideoListRequest {
        @Override
        public void onPostExecute(List<VideoModel> videoModels) {
            mProgressBar.setVisibility(View.GONE);
            if(videoModels != null && videoModels.size() > 0){
                if("0".equals(videoModels.get(0).isTurnOnVideo)) {
                    List<VideoModel> models = new ArrayList<>(3);
                    if (Integer.parseInt(videoModels.get(0).type) == NEW) {
                        if ("男".equals(AppManager.getClientUser().sex)) {
                            for(VideoModel model : videoModels) {
                                if (model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_4c65f4d0-6b24-4b2c-acb0-c340ab8ebdb6.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_16331136-8807-4756-8129-eaa189698bb2.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_7016ed18-e096-4321-966f-0c3c9ea41c27.jpg")) {
                                    models.add(model);
                                    if (models.size() == 3) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            for(VideoModel model : videoModels) {
                                if (model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_6ad4f316-543d-4293-9b6c-fd38d4b606e5.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_5ae96a20-4157-4d93-b4fd-37ef993c8361.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_3f7a8aef-5718-422a-9666-882cd791b3c1.jpg")) {
                                    models.add(model);
                                    if (models.size() == 3) {
                                        break;
                                    }
                                }
                            }
                        }
                    } else if (Integer.parseInt(videoModels.get(0).type) == HOT) {
                        if ("男".equals(AppManager.getClientUser().sex)) {
                            for(VideoModel model : videoModels) {
                                if (model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_e3f373ad-6a00-46cb-b4c4-defc99bec279.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_c1e96127-b98b-42c3-91b0-d3e453c0086e.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_3cfc2163-cebf-454d-898b-43370854fa01.jpg")) {
                                    models.add(model);
                                    if (models.size() == 3) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            for(VideoModel model : videoModels) {
                                if (model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_34cbe5c0-1597-4b3f-b79a-7a1c823561ba.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_5fdd3e3c-e9a5-4fcb-aff0-e58fc9450c41.jpg") ||
                                        model.curImgPath.equals("http://real-love-server.img-cn-shenzhen.aliyuncs.com/tan_love/img/tl_08011121-7b1d-4f76-9c3b-04b6244a7621.jpg")) {
                                    models.add(model);
                                    if (models.size() == 3) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    for(VideoModel model : models) {
                        videoModels.remove(model);
                        videoModels.add(0, model);
                    }

                    int position = mAdapter.getItemCount();
                    mVideoModels.addAll(videoModels);
                    mAdapter.setVideoModels(mVideoModels);
                    mAdapter.notifyItemInserted(position);
                }
            }
        }

        @Override
        public void onErrorExecute(String error) {
            mProgressBar.setVisibility(View.GONE);
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
                    new GetVideoListTask().request(++pageIndex, pageNo, videoType);
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
