package com.cyanbirds.yljy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.cyanbirds.yljy.fragment.FindLoveFragment;
import com.cyanbirds.yljy.fragment.VideoDetailFragment;

import java.util.List;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:31 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class VideoTabFragmentAdapter extends FragmentPagerAdapter {

    private List<String> mTitles;

    public VideoTabFragmentAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return VideoDetailFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try{
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException){
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }
}
