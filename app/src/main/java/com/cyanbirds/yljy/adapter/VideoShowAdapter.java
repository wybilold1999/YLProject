package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.SingleVideoActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.VideoModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * 
 * @Description:视频集
 * @author wangyb
 * @Date:2015年7月26日上午11:53:17
 */
public class VideoShowAdapter extends
		RecyclerView.Adapter<VideoShowAdapter.ViewHolder> {

	private List<VideoModel> mBeans;
	private Context mContext;

	public VideoShowAdapter(Context context, List<VideoModel> beans) {
		mContext = context;
		mBeans = beans;
	}

	@Override
	public int getItemCount() {
		return mBeans == null ? 0 : mBeans.size();
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		VideoModel model = mBeans.get(position);
		if (model == null) {
			return;
		}
		holder.img_queue.setImageURI(Uri.parse(model.curImgPath));
		holder.view_count.setText(model.view + "播放");
		holder.rose_num.setText(model.gold + "金币");
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(
				R.layout.item_video_show, parent, false));
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {

		SimpleDraweeView img_queue;
		TextView view_count;
		TextView rose_num;

		public ViewHolder(View itemView) {
			super(itemView);
			img_queue = (SimpleDraweeView) itemView.findViewById(R.id.img_queue);
			view_count = (TextView) itemView.findViewById(R.id.view_count);
			rose_num = (TextView) itemView.findViewById(R.id.rose_num);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			VideoModel model = mBeans.get(position);
			Intent intent = new Intent();
			intent.setClass(mContext, SingleVideoActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ValueKey.VIDEO, model);
			mContext.startActivity(intent);
		}
	}
}
