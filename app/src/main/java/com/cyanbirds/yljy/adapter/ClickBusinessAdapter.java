package com.cyanbirds.yljy.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.db.MyGoldDaoManager;
import com.cyanbirds.yljy.entity.ApkInfo;
import com.cyanbirds.yljy.entity.Gold;
import com.cyanbirds.yljy.eventtype.MakeMoneyEvent;
import com.cyanbirds.yljy.eventtype.SnackBarEvent;
import com.cyanbirds.yljy.manager.AppManager;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * @author Cloudsoar(wangyb)
 * @datetime 2015-12-26 18:34 GMT+8
 * @email 395044952@qq.com
 */
public class ClickBusinessAdapter extends
        RecyclerView.Adapter<ClickBusinessAdapter.ViewHolder> {

    private List<ApkInfo> mApkInfos;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private final long daySpan = 24 * 60 * 60 * 1000;
    private ColorMatrixColorFilter filter;//灰色

    private OnItemClickListener mOnItemClickListener;

    public ClickBusinessAdapter(List<ApkInfo> apkInfos, Context mContext, RecyclerView recyclerView) {
        this.mApkInfos = apkInfos;
        this.mContext = mContext;
        mRecyclerView = recyclerView;
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);//饱和度 0灰色 100过度彩色，50正常
        filter = new ColorMatrixColorFilter(matrix);
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_click_business, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApkInfo apkInfo = mApkInfos.get(position);
        if(apkInfo == null){
            return;
        }
        holder.portrait.setImageURI(Uri.parse(apkInfo.apkImgUrl));
    }

    @Override
    public int getItemCount() {
        return mApkInfos == null ? 0 : mApkInfos.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        SimpleDraweeView portrait;
        public ViewHolder(View itemView) {
            super(itemView);
            portrait = (SimpleDraweeView) itemView.findViewById(R.id.portrait);
            portrait.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Gold gold = MyGoldDaoManager.getInstance(mContext).getMyGold();
            if (gold == null) {
                gold = new Gold();
                gold.clickCount = 15;
                gold.vipFlag = 1;
                gold.banlance += 2;
                gold.downloadTime = System.currentTimeMillis();
                MyGoldDaoManager.getInstance(mContext).insertGold(gold);
                EventBus.getDefault().post(new SnackBarEvent());
                portrait.setColorFilter(filter);
                portrait.setEnabled(false);
                portrait.setClickable(false);
                return;
            }
			/**
			 * 是赚钱会员，可以持续点击，但是每次只能点15或者25次；否则每天只允许点一次
             */
            if (AppManager.getClientUser().is_download_vip) {
                if (System.currentTimeMillis() > gold.downloadTime + daySpan) {
                    gold.clickCount = gold.vipFlag == 1 ? 15 : 25;
                }
                if (gold.clickCount == 0) {
                    String msgTips = String.format(mContext.getResources().getString(
                            R.string.click_count_has_used), gold.vipFlag == 1 ? 30 : 50);
                    Snackbar.make(mRecyclerView, msgTips, Snackbar.LENGTH_SHORT).show();
                } else {
                    gold.clickCount -= 1;
                    gold.banlance += 2;
                    gold.downloadTime = System.currentTimeMillis();
                    MyGoldDaoManager.getInstance(mContext).updateGold(gold);
                    EventBus.getDefault().post(new SnackBarEvent());
                    portrait.setColorFilter(filter);
                    portrait.setEnabled(false);
                    portrait.setClickable(false);

                }
            } else {//不是赚钱会员，每天只允许点一次；点第二次的时候提示不是赚钱会员
                if (System.currentTimeMillis() > gold.downloadTime + daySpan) {
                    gold.banlance += 2;
                    gold.downloadTime = System.currentTimeMillis();
                    MyGoldDaoManager.getInstance(mContext).updateGold(gold);
                    EventBus.getDefault().post(new SnackBarEvent());
                } else {
                    EventBus.getDefault().post(new MakeMoneyEvent());
                }
            }

            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public ApkInfo getItem(int position){
        return mApkInfos == null ? null : mApkInfos.get(position);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
