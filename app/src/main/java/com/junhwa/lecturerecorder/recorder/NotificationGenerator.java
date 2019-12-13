package com.junhwa.lecturerecorder.recorder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.junhwa.lecturerecorder.R;
import com.junhwa.lecturerecorder.ui.MainActivity;

public class NotificationGenerator {
    public static final String NOTIFY_STOP = "con.junhwa.lecturerecorder.stop";
    public static final String NOTIFY_PAUSE = "con.junhwa.lecturerecorder.pause";

    private static final int NOTIFICATION_ID_OPNE_ACTIVITY = 9;

    public static void customBigNotification(Context context) {
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.notification_recording);

        NotificationCompat.Builder nc = new NotificationCompat.Builder(context);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notifyIntent = new Intent(context, MainActivity.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nc.setContentIntent(pendingIntent);
        nc.setSmallIcon(R.drawable.ic_menu);
        nc.setCustomBigContentView(expandedView);
        nc.setContentTitle(context.getString(R.string.app_name));
        nc.setContentText("this is test");
        nc.getBigContentView().setTextViewText(R.id.textView5, "Test");

        setListeners(expandedView, context);
    }

    private static void setListeners(RemoteViews view, Context context) {
        Intent stop = new Intent(NOTIFY_STOP);
        Intent pause = new Intent(NOTIFY_PAUSE);

        PendingIntent pStop = PendingIntent.getBroadcast(context, 0, stop, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.button, pStop);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.button, pPause);
    }
}
