package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.SuccessCase;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class SuccessCaseAdapter extends
		RecyclerView.Adapter<SuccessCaseAdapter.ViewHolder> {

	private List<SuccessCase> mSuccessCaseModels;
	private Context mContext;

	public SuccessCaseAdapter(List<SuccessCase> successCaseModels, Context mContext) {
		this.mSuccessCaseModels = successCaseModels;
		this.mContext = mContext;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_success_case, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		SuccessCase successCaseModel = mSuccessCaseModels.get(position);
		if (successCaseModel == null) {
			return;
		}
		holder.mTitle.setText(successCaseModel.title);
		holder.mTime.setText(successCaseModel.time);
		holder.mCoupleName.setText(successCaseModel.coupleName);
		holder.mImage.setImageURI(Uri.parse(successCaseModel.imgUrl));
		holder.mContent.setText(successCaseModel.desciption);
	}

	@Override
	public int getItemCount() {
		return mSuccessCaseModels == null ? 0 : mSuccessCaseModels.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder {

		TextView mTitle;
		TextView mTime;
		TextView mCoupleName;
		SimpleDraweeView mImage;
		TextView mContent;

		public ViewHolder(View itemView) {
			super(itemView);
			mTitle = (TextView) itemView.findViewById(R.id.title);
			mTime = (TextView) itemView.findViewById(R.id.time);
			mCoupleName = (TextView) itemView.findViewById(R.id.couple_name);
			mImage = (SimpleDraweeView) itemView.findViewById(R.id.image);
			mContent = (TextView) itemView.findViewById(R.id.content);
		}
	}

}
