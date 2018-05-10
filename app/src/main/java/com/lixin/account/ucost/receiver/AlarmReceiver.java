package com.lixin.account.ucost.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lixin.account.ucost.R;
import com.lixin.account.ucost.activity.RecordActivity;
import com.lixin.account.ucost.utils.Constant;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constant.ACTION_ALARM.equals(intent.getAction())) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent playIntent = new Intent(context, RecordActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(context.getString(R.string.app_name)).setContentText(context.getString(R.string.notice)).setTicker(context.getString(R.string.notice)).
                    setSmallIcon(R.mipmap.ic_launcher).setDefaults(Notification.DEFAULT_ALL).
                    setContentIntent(pendingIntent).setAutoCancel(true);
            manager.notify(1, builder.build());
        }
    }
}
