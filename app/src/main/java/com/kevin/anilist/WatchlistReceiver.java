package com.kevin.anilist;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class WatchlistReceiver extends BroadcastReceiver {
    public static final String ACTION_WATCHLIST_ADDED = "com.kevin.anilist.ACTION_WATCHLIST_ADDED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_WATCHLIST_ADDED.equals(intent.getAction())) {
            String animeTitle = intent.getStringExtra("anime_title");
            showNotification(context, animeTitle);
        }
    }

    private void showNotification(Context context, String animeTitle) {
        String channelId = "watchlist_notifications";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Watchlist Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Watchlist Updated!")
                .setContentText(animeTitle + " has been added to your list.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
