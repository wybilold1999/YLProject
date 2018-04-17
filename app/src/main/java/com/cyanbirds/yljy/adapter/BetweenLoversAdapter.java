package com.cyanbirds.yljy.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.BetweenLovers;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 * 红娘信息
 */
public class BetweenLoversAdapter extends
		RecyclerView.Adapter<BetweenLoversAdapter.ViewHolder> {


	private List<BetweenLovers> mBetweenLoverses;

	public BetweenLoversAdapter(List<BetweenLovers> betweenLoverses) {
		this.mBetweenLoverses = betweenLoverses;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_between_lovers, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		BetweenLovers betweenLovers =  mBetweenLoverses.get(position);
		holder.mName.setText(betweenLovers.name);
		holder.mPortrait.setImageURI(Uri.parse(betweenLovers.faceUrl));
		holder.mSuccessCase.setText(betweenLovers.sucessCount);
		holder.mWorkYear.setText(betweenLovers.workYear);
		holder.mDesciption.setText(betweenLovers.desciption);
	}

	@Override
	public int getItemCount() {
		return mBetweenLoverses == null ? 0 : mBetweenLoverses.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder {

		TextView mName;
		SimpleDraweeView mPortrait;
		TextView mSuccessCase;
		TextView mWorkYear;
		TextView mDesciption;
		public ViewHolder(View itemView) {
			super(itemView);
			mName = (TextView) itemView.findViewById(R.id.name);
			mPortrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
			mSuccessCase = (TextView) itemView.findViewById(R.id.success_case);
			mWorkYear = (TextView) itemView.findViewById(R.id.workYear);
			mDesciption = (TextView) itemView.findViewById(R.id.desciption);
		}
	}
}
