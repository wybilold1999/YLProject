package com.cyanbirds.yljy.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.base.BaseActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.UserVideoPhotoModel;
import com.cyanbirds.yljy.entity.VideoModel;
import com.cyanbirds.yljy.fragment.VideoDetailFragment;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.GetUserVideoPhotoListRequest;
import com.cyanbirds.yljy.net.request.UpdateGoldRequest;
import com.cyanbirds.yljy.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/6/19.
 */
public class SingleVideoActivity extends BaseActivity {
	@BindView(R.id.img_queue)
	SimpleDraweeView imgQueue;
	@BindView(R.id.rose_num)
	TextView roseNum;
	@BindView(R.id.video_info)
	TextView mVideoInfo;
	@BindView(R.id.tv_video_show)
	TextView mTvVideoShow;
	@BindView(R.id.portrait)
	SimpleDraweeView mPortrait;
	@BindView(R.id.user_name)
	TextView mUserName;
	@BindView(R.id.age)
	TextView mAge;
	@BindView(R.id.occupation)
	TextView mOccupation;
	@BindView(R.id.personal_lay)
	RelativeLayout personal_lay;

	private String videoType;
	private VideoModel mVideoModel;
	private UserVideoPhotoModel mUserVideoPhotoModel;
	private String fromActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_video);
		ButterKnife.bind(this);
		Toolbar toolbar = getActionBarToolbar();
		if (toolbar != null) {
			toolbar.setNavigationIcon(R.mipmap.ic_up);
		}
		setupView();
		setupData();
	}

	private void setupView() {
		videoType = getIntent().getStringExtra(ValueKey.VIDEO_TYPE);
		mVideoModel = (VideoModel) getIntent().getSerializableExtra(ValueKey.VIDEO);
		if (String.valueOf(VideoDetailFragment.NEW).equals(videoType) ||
				String.valueOf(VideoDetailFragment.HOT).equals(videoType)) {
			personal_lay.setVisibility(View.GONE);
		} else {
			personal_lay.setVisibility(View.VISIBLE);
		}
	}

	private void setupData() {
		if (mVideoModel != null) {
			imgQueue.setImageURI(Uri.parse(mVideoModel.curImgPath));
			roseNum.setText(mVideoModel.gold + "枚金币");
			mVideoInfo.setText(mVideoModel.description);
			if (!"0".equals(mVideoModel.type) && !"".equals(mVideoModel.type)) {
				mTvVideoShow.setVisibility(View.GONE);
			} else {
				mTvVideoShow.setVisibility(View.VISIBLE);
			}
		}

		/**
		 * 视频不是最新、最热的时候才执行以下code
		 */
		if (!String.valueOf(VideoDetailFragment.NEW).equals(videoType) &&
				!String.valueOf(VideoDetailFragment.HOT).equals(videoType)) {
			fromActivity = getIntent().getStringExtra(ValueKey.FROM_ACTIVITY);//不是从个人信息界面的视频列表跳转过来的，就请求个人信息
			if (fromActivity == null) {
				new GetUserVideoPhotoListTask().request(mVideoModel.usersByUserId);
			} else {
				UserVideoPhotoModel model = (UserVideoPhotoModel) getIntent().getSerializableExtra(ValueKey.USER);
				int position = getIntent().getIntExtra(ValueKey.POSITION, 0);
				if (null != model) {
					mUserVideoPhotoModel = model;

					mPortrait.setImageURI(Uri.parse(model.faceUrl));
					mUserName.setText(model.nickName);
					mAge.setText(model.age);
					mOccupation.setText(model.occupation);

					if (null != model.videos && model.videos.size() > 0 && null != model.videos.get(position)) {
						mVideoModel = model.videos.get(position);
						imgQueue.setImageURI(Uri.parse(mVideoModel.curImgPath));
						roseNum.setText(mVideoModel.gold + "枚金币");
						mVideoInfo.setText(mVideoModel.description);
						if (!"0".equals(mVideoModel.type) && !"".equals(mVideoModel.type)) {
							mTvVideoShow.setVisibility(View.GONE);
						} else {
							mTvVideoShow.setVisibility(View.VISIBLE);
						}
					}
				}
			}
		}

	}

	class GetUserVideoPhotoListTask extends GetUserVideoPhotoListRequest {
		@Override
		public void onPostExecute(UserVideoPhotoModel userVideoPhotoModel) {
			if (userVideoPhotoModel != null && !TextUtils.isEmpty(userVideoPhotoModel.userId)) {
				personal_lay.setVisibility(View.VISIBLE);
				mUserVideoPhotoModel = userVideoPhotoModel;
				mPortrait.setImageURI(Uri.parse(userVideoPhotoModel.faceUrl));
				mUserName.setText(userVideoPhotoModel.nickName);
				mAge.setText(userVideoPhotoModel.age);
				mOccupation.setText(userVideoPhotoModel.occupation);
			} else {
				personal_lay.setVisibility(View.GONE);
			}
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	@OnClick({R.id.img_queue, R.id.tv_video_show, R.id.portrait})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.img_queue:
				if (AppManager.getClientUser().is_vip && AppManager.getClientUser().gold_num == 0) {
					showBuyRoseDialog();
				} else if (!AppManager.getClientUser().is_vip) {
					showTurnOnVipDialog();
				} else if (AppManager.getClientUser().gold_num < Integer.parseInt(mVideoModel.gold)) {
					showBuyRoseDialog();
				} else {
					/**
					 * 更新服务器上的金币数量
					 */
					AppManager.getClientUser().gold_num -= Integer.parseInt(mVideoModel.gold);
					new UpdateGoldTask().request(AppManager.getClientUser().gold_num, mVideoModel.curVideoPath);
				}
				break;
			case R.id.tv_video_show:
				Intent intent = new Intent();
				intent.setClass(this, VideoShowActivity.class);
				intent.putExtra(ValueKey.USER_ID, mVideoModel.usersByUserId);
				startActivity(intent);
				break;
			case R.id.portrait :
				if (null == fromActivity) {
					Intent intent1 = new Intent(this, PersonalVideoActivity.class);
					intent1.putExtra(ValueKey.USER, mUserVideoPhotoModel);
					startActivity(intent1);
				}
				break;
		}
	}

	class UpdateGoldTask extends UpdateGoldRequest {
		@Override
		public void onPostExecute(Integer integer) {
			Intent intent = new Intent();
			intent.setClass(SingleVideoActivity.this, VideoPlayActivity.class);
			intent.putExtra(ValueKey.VIDEO, mVideoModel.curVideoPath);
			startActivity(intent);
		}

		@Override
		public void onErrorExecute(String error) {
			ToastUtil.showMessage(error);
		}
	}

	private void showTurnOnVipDialog() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.un_see_video);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(SingleVideoActivity.this, VipCenterActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void showBuyRoseDialog() {
		Builder builder = new Builder(this);
		builder.setMessage(String.format(getResources().getString(R.string.buy_gold), Integer.parseInt(mVideoModel.gold)));
		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(SingleVideoActivity.this, MyGoldActivity.class);
				startActivity(intent);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
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
