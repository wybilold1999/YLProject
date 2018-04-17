package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.MemberBuy;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class DownloadPayAdapter extends
        RecyclerView.Adapter<DownloadPayAdapter.ViewHolder> {

    private List<MemberBuy> mMemberBuys;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public DownloadPayAdapter(List<MemberBuy> memberBuys, Context mContext) {
        this.mMemberBuys = memberBuys;
        this.mContext = mContext;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_download_pay, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MemberBuy memberBuy = mMemberBuys.get(position);
        if (memberBuy == null) {
            return;
        }
        holder.mDataLimit.setText(memberBuy.months);
        holder.mPrice.setText(memberBuy.price + "元");
        holder.mInfo.setText(memberBuy.descreption);
        holder.mSelect.setChecked(memberBuy.isSelected);
    }

    @Override
    public int getItemCount() {
        return mMemberBuys == null ? 0 : mMemberBuys.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mDataLimit;
        TextView mPrice;
        TextView mInfo;
        CheckBox mSelect;
        public ViewHolder(View itemView) {
            super(itemView);
            mInfo = (TextView) itemView.findViewById(R.id.info);
            mPrice = (TextView) itemView.findViewById(R.id.price);
            mDataLimit = (TextView) itemView.findViewById(R.id.date_limit);
            mSelect = (CheckBox) itemView.findViewById(R.id.select);
            mSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
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
