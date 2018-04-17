package com.cyanbirds.yljy.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.adapter.VideoTabFragmentAdapter;
import com.cyanbirds.yljy.manager.AppManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangyb on 2018/1/19.
 */

public class VideoListActivity extends BaseActivity {

    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    private View rootView;

    private List<String> tabList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_show_list);
        ButterKnife.bind(this);
        setupView();
    }

    private void setupView() {
        tabList = new ArrayList<>();
        tabList.add("最新");
        tabList.add("最热");
        if ("男".equals(AppManager.getClientUser().sex)) {
            tabList.add("热舞");
            tabList.add("制服");
            tabList.add("性感");
            tabList.add("清纯");
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(1)));
        if ("男".equals(AppManager.getClientUser().sex)) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(3)));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(4)));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabList.get(5)));
        }

        VideoTabFragmentAdapter fragmentAdapter = new VideoTabFragmentAdapter(
                getSupportFragmentManager(), tabList);
        mViewpager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewpager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
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
