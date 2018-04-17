package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.MemberBuy;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class MyGoldAdapter extends
        RecyclerView.Adapter<MyGoldAdapter.ViewHolder> {

    private List<MemberBuy> mMemberBuys;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MyGoldAdapter(List<MemberBuy> memberBuys, Context mContext) {
        this.mMemberBuys = memberBuys;
        this.mContext = mContext;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_gold, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MemberBuy memberBuy = mMemberBuys.get(position);
        if (memberBuy == null) {
            return;
        }
        holder.mGoldNum.setText(String.format(mContext.getResources().getString(R.string.gold_num),
                Integer.parseInt(memberBuy.months)));
        holder.mPrice.setText("￥" + memberBuy.price);
        if (!TextUtils.isEmpty(memberBuy.preferential)) {
            holder.mPreferential.setText(String.format(mContext.getResources().getString(R.string.send_gold_num),
                    Integer.parseInt(memberBuy.preferential)));
        }
        holder.mSelect.setChecked(memberBuy.isSelected);
        if (position == 2) {
            holder.mHot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mMemberBuys == null ? 0 : mMemberBuys.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mGoldNum;
        TextView mPreferential;
        TextView mPrice;
        CheckBox mSelect;
        ImageView mHot;
        public ViewHolder(View itemView) {
            super(itemView);
            mGoldNum = (TextView) itemView.findViewById(R.id.gold_num);
            mPreferential = (TextView) itemView.findViewById(R.id.preferential);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mSelect = (CheckBox) itemView.findViewById(R.id.select);
            mHot = (ImageView) itemView.findViewById(R.id.iv_hot);
            mSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position);
            }
            for (MemberBuy memberBuy : mMemberBuys) {
                memberBuy.isSelected = false;
            }
            mMemberBuys.get(position).isSelected = true;
            notifyDataSetChanged();
        }
    }

    public MemberBuy getItem(int position){
        return mMemberBuys == null ? null : mMemberBuys.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


}
