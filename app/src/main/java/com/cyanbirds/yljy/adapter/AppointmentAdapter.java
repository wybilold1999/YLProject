package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.entity.AppointmentModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class AppointmentAdapter extends
        RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<AppointmentModel> mAppointmentModels;
    private Context mContext;
    private String mFrom;

    private OnItemClickListener mOnItemClickListener;

    public AppointmentAdapter(String from, List<AppointmentModel> appointmentModels, Context mContext) {
        this.mAppointmentModels = appointmentModels;
        this.mContext = mContext;
        this.mFrom = from;
    }



    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AppointmentAdapter.ViewHolder holder, int position) {
        AppointmentModel model = mAppointmentModels.get(position);
        if(model == null){
            return;
        }
        holder.appointmentTime.setText(model.appointTime);
        if (!TextUtils.isEmpty(model.faceUrl)) {
            holder.portrait.setImageURI(Uri.parse(model.faceUrl));
        }
        if (!TextUtils.isEmpty(mFrom)) {
            if (!TextUtils.isEmpty(model.userName)) {
                holder.userName.setText(model.userName);
            }
        } else {
            if (!TextUtils.isEmpty(model.userByName)) {
                holder.userName.setText(model.userByName);
            }
        }
        holder.theme.setText("主题：" + model.theme);
        holder.status.setText(Html.fromHtml(AppointmentModel.getStatus(model.status)));
    }

    @Override
    public int getItemCount() {
        return mAppointmentModels == null ? 0 : mAppointmentModels.size();
    }

    public AppointmentModel getItem(int position){
        if (mAppointmentModels == null || mAppointmentModels.size() < 1) {
            return null;
        }
        return mAppointmentModels == null ? null : mAppointmentModels.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        SimpleDraweeView portrait;
        TextView userName;
        TextView theme;
        TextView appointmentTime;
        TextView status;
        public ViewHolder(View itemView) {
            super(itemView);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            theme = (TextView) itemView.findViewById(R.id.appointment_theme);
            appointmentTime = (TextView) itemView.findViewById(R.id.appointment_time);
            status = (TextView) itemView.findViewById(R.id.applay_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mOnItemClickListener != null) {
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

    public void setAppointmentModels(List<AppointmentModel> list) {
        mAppointmentModels = list;
        notifyDataSetChanged();
    }
}
