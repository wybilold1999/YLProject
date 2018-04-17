package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.lib.widget.verticalmarqueetextview.VerticalMarqueeTextView;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.ui.widget.LuckyPanView;
import com.cyanbirds.yljy.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作者：wangyb
 * 时间：2017/3/25 19:35
 * 描述：抽奖
 */
public class RewardActivity extends BaseActivity implements View.OnClickListener{
	private LuckyPanView mLuckyPanView;
	private ImageView mStartBtn;
	private Toolbar mToolbar;
	private VerticalMarqueeTextView mMarqueeTextView;
	private TextView mRewardInfo;
	private ArrayList<String> mData;
	private List<String> mBuyList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reward);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		if (mToolbar != null) {
			setSupportActionBar(mToolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(R.string.reward);
		}

		mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
		mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
		mMarqueeTextView = (VerticalMarqueeTextView) findViewById(R.id.vertical_text);
		mRewardInfo = (TextView) findViewById(R.id.reward_info);
		mStartBtn.setOnClickListener(this);

		mData = getIntent().getStringArrayListExtra(ValueKey.DATA);
		String rewardInfo = getIntent().getStringExtra(ValueKey.USER);
		if (!TextUtils.isEmpty(rewardInfo)) {
			mRewardInfo.setText(rewardInfo);
		}

		if (mData != null && mData.size() > 0) {
			Collections.reverse(mData);

			mBuyList = new ArrayList<>(5);
			mBuyList.add("小米手机5");
			mBuyList.add("IPAD");
			mBuyList.add("腾讯视频会员");
			mBuyList.add("国内100M流量");
			mBuyList.add("100元话费");
			StringBuilder builder = new StringBuilder();
			for (String name : mData) {
				builder.append(name + " 获得了" + mBuyList.get((int) (Math.random() * mBuyList.size())) + "\n");
			}
			mMarqueeTextView.setText(builder.toString());
		}
	}

	@Override
	public void onClick(View v) {
		int count = PreferencesUtils.getRewardCount(this);
		if (count > 0) {
			count--;
			if (!mLuckyPanView.isStart()) {
				mStartBtn.setImageResource(R.mipmap.stop);
				mLuckyPanView.luckyStart(6);
			} else {
				if (!mLuckyPanView.isShouldEnd()) {
					mStartBtn.setImageResource(R.mipmap.start);
					mLuckyPanView.luckyEnd();
					PreferencesUtils.setRewardCount(this, count);
				}
			}
		} else {
			if (AppManager.getClientUser().is_vip) {
				showTurnOnVipDialog(R.string.reward_count_over);
			} else {
				showTurnOnVipDialog(R.string.no_reward_count);
			}
		}
	}

	private void showTurnOnVipDialog(int id){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(id);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
