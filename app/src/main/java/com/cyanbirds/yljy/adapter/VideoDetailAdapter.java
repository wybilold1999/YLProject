package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.SingleVideoActivity;
import com.cyanbirds.yljy.activity.VideoPlayActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.VideoModel;
import com.cyanbirds.yljy.fragment.VideoDetailFragment;
import com.cyanbirds.yljy.manager.AppManager;
import com.cyanbirds.yljy.net.request.UpdateGoldRequest;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * 
 * @Description:视频
 * @author wangyb
 * @Date:2015年7月26日上午11:53:17
 */
public class VideoDetailAdapter extends
		RecyclerView.Adapter<VideoDetailAdapter.ViewHolder> {

	private List<VideoModel> mVideoModels; //视频列表
	private Context mContext;

	public VideoDetailAdapter(Context context, List<VideoModel> beans) {
		mContext = context;
		mVideoModels = beans;
	}

	@Override
	public int getItemCount() {
		return mVideoModels == null ? 0 : mVideoModels.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		VideoModel model = mVideoModels.get(position);
		if(model != null){
			holder.img_queue.setImageURI(Uri.parse(model.curImgPath));
			holder.mVideoInfo.setText(model.description);
			holder.mViewCount.setText(model.view + "次播放");
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_video_detail, parent, false));
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {

		SimpleDraweeView img_queue;
		TextView mVideoInfo;
		TextView mViewCount;

		public ViewHolder(View itemView) {
			super(itemView);
			img_queue = (SimpleDraweeView) itemView.findViewById(R.id.img_queue);
			mVideoInfo = (TextView) itemView.findViewById(R.id.video_info);
			mViewCount = (TextView) itemView.findViewById(R.id.view_count);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			VideoModel model = mVideoModels.get(position);
			Intent intent = new Intent();
			if (position == 0 || position == 1 || position == 2) {
				if (model.type.equals(String.valueOf(VideoDetailFragment.CLOTH)) ||
						model.type.equals(String.valueOf(VideoDetailFragment.DANCING)) ||
						model.type.equals(String.valueOf(VideoDetailFragment.PURE)) ||
						model.type.equals(String.valueOf(VideoDetailFragment.SEXY))) {

					intent.setClass(v.getContext(), SingleVideoActivity.class);
					intent.putExtra(ValueKey.VIDEO, model);

				} else {
					intent.setClass(v.getContext(), VideoPlayActivity.class);
					intent.putExtra(ValueKey.VIDEO, model.curVideoPath);
					/**
					 * 更新视频播放次数
					 */
					new UpdateGoldRequest().request(AppManager.getClientUser().gold_num, model.curVideoPath);
				}
			} else {
				intent.setClass(v.getContext(), SingleVideoActivity.class);
				intent.putExtra(ValueKey.VIDEO, model);
			}
			v.getContext().startActivity(intent);
		}
	}

	public void setVideoModels(List<VideoModel> videoModels){
		this.mVideoModels = videoModels;
	}
}
