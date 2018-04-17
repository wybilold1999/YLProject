package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.activity.SingleVideoActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.entity.UserVideoPhotoModel;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * @author: wangyb
 * @datetime: 2016-01-02 16:51 GMT+8
 * @email: 395044952@qq.com
 * @description:
 */
public class TabVideoAdapter extends
        RecyclerView.Adapter<TabVideoAdapter.ViewHolder>{
    private Context mContext;
    private UserVideoPhotoModel mModel;

    public TabVideoAdapter(Context context, UserVideoPhotoModel model) {
        mContext = context;
        this.mModel = model;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri uri = Uri.parse(mModel.videos.get(position).curImgPath);
        holder.imageView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mModel.videos == null ? 0 : mModel.videos.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_tab_video, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        SimpleDraweeView imageView;
        ImageView mIvVideo;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView)itemView.findViewById(R.id.img_queue);
            mIvVideo = (ImageView) itemView.findViewById(R.id.iv_video);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(mContext, SingleVideoActivity.class);
            intent.putExtra(ValueKey.USER, mModel);
            intent.putExtra(ValueKey.FROM_ACTIVITY, "PersonalVideoActivity");
            intent.putExtra(ValueKey.POSITION, position);
            if (null != mModel.videos.get(position)) {
                intent.putExtra(ValueKey.VIDEO_TYPE, mModel.videos.get(position).type);
            }
            mContext.startActivity(intent);
        }
    }
}
