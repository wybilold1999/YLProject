package com.cyanbirds.yljy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cyanbirds.yljy.CSApplication;
import com.cyanbirds.yljy.activity.ChatActivity;
import com.cyanbirds.yljy.activity.LauncherActivity;
import com.cyanbirds.yljy.activity.PersonalInfoActivity;
import com.cyanbirds.yljy.config.ValueKey;
import com.cyanbirds.yljy.db.ConversationSqlManager;
import com.cyanbirds.yljy.entity.ClientUser;
import com.cyanbirds.yljy.entity.Conversation;
import com.cyanbirds.yljy.listener.MessageChangedListener;
import com.cyanbirds.yljy.listener.MessageUnReadListener;
import com.cyanbirds.yljy.manager.AppManager;

/**
 * 通知广播
 * Created by Administrator on 2016/3/14.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppManager.isAppAlive(context, AppManager.getPackageName())
                && AppManager.getClientUser() != null/*
                && Integer.parseInt(AppManager.getClientUser().userId) > 0*/) {
            /*Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);*/

            ClientUser clientUser = (ClientUser) intent.getSerializableExtra(ValueKey.USER);
            if (clientUser != null) {
                Conversation conversation = ConversationSqlManager.getInstance(CSApplication.getInstance())
                        .queryConversationForByTalkerId(clientUser.userId);
                if (conversation != null) {
                    conversation.unreadCount = 0;
                    ConversationSqlManager.getInstance(context).updateConversation(conversation);
                    MessageUnReadListener.getInstance().notifyDataSetChanged(0);
                    MessageChangedListener.getInstance().notifyMessageChanged("");
                }
            }
            if (clientUser.isLocalMsg) {
                Intent chatIntent = new Intent(context, PersonalInfoActivity.class);
                chatIntent.putExtra(ValueKey.USER_ID, clientUser.userId);
                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chatIntent);
            } else {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra(ValueKey.USER, clientUser);
                chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chatIntent);
            }
        } else {
            Intent launcherIntent = new Intent(context, LauncherActivity.class);
            launcherIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launcherIntent);
        }
    }
}
