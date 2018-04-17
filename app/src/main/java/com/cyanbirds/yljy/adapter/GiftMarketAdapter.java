package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.BetweenLovers;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.Gift;
import com.cyanbirds.yljy.manager.AppManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 * 礼物适配器
 */
public class GiftMarketAdapter extends
		RecyclerView.Adapter<GiftMarketAdapter.ViewHolder> {

	private Context mContext;
	private List<Gift> mGifts;
	private OnItemClickListener mOnItemClickListener;

	public GiftMarketAdapter(Context context, List<Gift> gifts) {
		this.mGifts = gifts;
		mContext = context;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_gift_market, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Gift gift =  mGifts.get(position);
		holder.mGiftName.setText(gift.name);
		holder.mImgUrl.setImageURI(Uri.parse(gift.dynamic_image_url));
		if (AppManager.getClientUser().isShowGold) {
			holder.mPriceLay.setVisibility(View.VISIBLE);
			holder.mLine.setVisibility(View.VISIBLE);
			holder.mVipAmount.setVisibility(View.VISIBLE);
			holder.mVip.setVisibility(View.VISIBLE);
			if (gift.vip_amount == 0) {
				holder.mVipAmount.setText("免费");
			} else {
				holder.mVipAmount.setText(gift.vip_amount + "金币");
			}
		} else {
			holder.mPriceLay.setVisibility(View.GONE);
			holder.mVipAmount.setVisibility(View.GONE);
			holder.mLine.setVisibility(View.GONE);
			holder.mVip.setVisibility(View.GONE);
		}
		holder.mAmount.setText(String.format(mContext.getResources().getString(R.string.org_price), gift.amount));
	}

	@Override
	public int getItemCount() {
		return mGifts == null ? 0 : mGifts.size();
	}

	public Gift getItem(int position){
		return mGifts == null ? null : mGifts.get(position);
	}


	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

		TextView mGiftName;
		SimpleDraweeView mImgUrl;
		TextView mVipAmount;
		TextView mAmount;
		TextView mLine;
		ImageView mVip;
		LinearLayout mPriceLay;
		public ViewHolder(View itemView) {
			super(itemView);
			mGiftName = (TextView) itemView.findViewById(R.id.gift_name);
			mImgUrl = (SimpleDraweeView) itemView.findViewById(R.id.img_url);
			mVipAmount = (TextView) itemView.findViewById(R.id.vip_amount);
			mAmount = (TextView) itemView.findViewById(R.id.amount);
			mLine = (TextView) itemView.findViewById(R.id.line);
			mVip = (ImageView) itemView.findViewById(R.id.iv_vip);
			mPriceLay = (LinearLayout) itemView.findViewById(R.id.price_lay);
			mImgUrl.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(v, getAdapterPosition());
			}
		}
	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}
}
