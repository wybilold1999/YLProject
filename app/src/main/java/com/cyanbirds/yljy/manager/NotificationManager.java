package com.cyanbirds.yljy.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.cyanbirds.yljy.R;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.db.ConversationSqlManager;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.IMessage;
import com.cyanbirds.yljy.receiver.NotificationReceiver;
import com.cyanbirds.yljy.utils.PreferencesUtils;

/**
 * 通知管理
 * Created by Administrator on 2016/3/14.
 */
public class NotificationManager {

    public static NotificationManager mInstance;
    public static Context mContext;

    public static NotificationManager getInstance() {
        if (mInstance == null) {
            mInstance = new NotificationManager(AppManager.getContext());
        }

        return mInstance;
    }

    private NotificationManager(Context context) {
        mContext = context;
    }

    /**
     * 显示消息通知
     *
     * @param message
     */
    public void showMessageNotification(IMessage message) {
        try {
            Notification notification = getNotification(message);
            if (notification != null) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat
                        .from(mContext);
                notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
                notificationManager.notify(0, notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置消息通知数据
     *
     * @param message
     * @return
     */
    public Notification getNotification(IMessage message) {
        if (message == null)
            return null;
        cancelNotification();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                mContext);
        String title;
        String content;
        int unReadNum = ConversationSqlManager.getInstance(mContext).getAnalyticsUnReadConversation();
        // 显示消息详情
        /*if (PreferencesUtils.getShowMessageInfo(mContext)) {
            title = mContext.getResources().getString(R.string.app_name_short);
            content = getTickerText(message);
        } else {
            int unContactNum = ConversationSqlManager
                    .getInstance(mContext).getConversationUnReadNum();
            title = mContext.getResources().getString(R.string.app_name_short);
            content = String.format(
                    mContext.getResources().getString(R.string.notification_tips), unContactNum, unReadNum);
        }*/
        if (!TextUtils.isEmpty(message.face_url)) {
            title = mContext.getResources().getString(R.string.app_name_short);
        } else {
            title = message.sender_name;
        }
        content = getTickerText(message);
        builder.setTicker(title + "\n" + content);
        // 设置通知内容的标题
        builder.setContentTitle(title);
        builder.setContentText(content);
        // 设置打开通知，该通知自动消失
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // 是否震动
        if (PreferencesUtils.getNoticeShock(mContext)) {
            builder.setVibrate(new long[]{300, 100, 300, 100});
        }
        // 是否显示消息声音
        if (PreferencesUtils.getNoticeVoice(mContext)) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }
        builder.setNumber(unReadNum);
        // 通知时间
        builder.setWhen(System.currentTimeMillis());

        ClientUser clientUser = new ClientUser();
        clientUser.user_name = message.sender_name;
        clientUser.userId = message.sender;
        if (!TextUtils.isEmpty(message.face_url)) {
            clientUser.face_url = message.face_url;
            clientUser.isLocalMsg = true;
        }

        Intent intent = new Intent(mContext, NotificationReceiver.class);
        intent.putExtra(ValueKey.USER, clientUser);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 设置通知将要启动程序的intent
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        return notification;
    }

    /**
     * 获取显示的内容
     *
     * @param message
     * @return
     */
    public final String getTickerText(IMessage message) {
        if (message.msgType == IMessage.MessageType.TEXT) {
            return message.content;
        } else if (message.msgType == IMessage.MessageType.VOICE) {
            return mContext.getResources().getString(R.string.voice_symbol);
        } else if (message.msgType == IMessage.MessageType.IMG) {
            return mContext.getResources().getString(R.string.image_symbol);
        } else if (message.msgType == IMessage.MessageType.LOCATION) {
            return mContext.getResources().getString(R.string.location_symbol);
        } else if (message.msgType == IMessage.MessageType.RED_PKT) {
            return mContext.getResources().getString(R.string.rpt_symbol);
        }
        return "";
    }

    /**
     * 关闭通知
     */
    public void cancelNotification() {
        NotificationManagerCompat.from(mContext).cancelAll();
    }

}
