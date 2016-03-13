/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sample.nohttp.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.sample.nohttp.Application;

/**
 * Created by YOLANDA on 2016/3/12.
 */
public class ProgressNotify {

    /**
     * Android本地通知管理器
     */
    private NotificationManager mNotifyManager;

    /**
     * 创建进度通知
     *
     * @param id              通知ID
     * @param activityIntent  Activity动作
     * @param broadcastIntent Broadcast动作
     * @param stateBar        通知栏标题
     * @param title           通知标题
     * @param content         通知内容
     * @param iconResId       通知图标
     * @param progress        初始进度
     * @return {@link NotificationCompat.Builder}
     */
    public NotificationCompat.Builder createNotification(int id, Intent activityIntent, Intent broadcastIntent, CharSequence stateBar, CharSequence title, CharSequence content, int iconResId,
                                                         int progress) {
        Context context = Application.getInstance();
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(iconResId);
        builder.setTicker(stateBar);// 状态栏提示文字
        builder.setContentTitle(title);// 通知标题
        builder.setContentText(content);// 通知内容
        builder.setOngoing(false);
        builder.setAutoCancel(false);
        builder.setProgress(100, progress, false);
        // 动作
        PendingIntent pendIntent;
        if (activityIntent != null) {
            pendIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        } else {
            pendIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, 0);
        }
        builder.setContentIntent(pendIntent); // 关联PendingIntent
        mNotifyManager.notify(id, builder.build());
        return builder;
    }

    /**
     * 更新通知
     *
     * @param id
     * @param builder
     * @param content
     * @param progress
     */
    public void update(int id, NotificationCompat.Builder builder, CharSequence content, int progress) {
        builder.setContentText(content).setProgress(100, progress, false);
        builder.setAutoCancel(false).setOngoing(true);
        mNotifyManager.notify(id, builder.build());
    }

    /**
     * 进度通知完成
     *
     * @param id
     * @param builder
     * @param title
     * @param content
     */
    public NotificationCompat.Builder finish(int id, NotificationCompat.Builder builder, CharSequence title, CharSequence content, Intent intent, boolean isLong) {
        mNotifyManager.cancel(id);
        builder.setContentText(content).setProgress(100, 100, false);
        builder.setAutoCancel(false).setOngoing(isLong);
        PendingIntent pendIntent = PendingIntent.getActivity(Application.getInstance(), 0, intent, 0);
        builder.setContentIntent(pendIntent);
        mNotifyManager.notify(id, builder.build());
        return builder;
    }

    /**
     * 取消通知
     *
     * @author YOLANDA
     */
    public void cancel(int id) {
        mNotifyManager.cancel(id);
    }

}
