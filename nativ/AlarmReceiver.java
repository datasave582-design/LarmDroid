package com.vikka.smartalarm;

import android.app.*;
import android.content.*;
import android.media.RingtoneManager;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context c, Intent in) {
    AlarmPlugin.channel(c);
    int id = in.getIntExtra("id", 0);
    Intent open = new Intent(c, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent p = PendingIntent.getActivity(c, id, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(c, AlarmPlugin.CH) : new Notification.Builder(c);
    b.setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle(in.getStringExtra("title")).setContentText(in.getStringExtra("body"))
        .setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX)
        .setContentIntent(p).setFullScreenIntent(p, true).setAutoCancel(true)
        .setVisibility(Notification.VISIBILITY_PUBLIC);
    if (Build.VERSION.SDK_INT < 26) b.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).setVibrate(new long[]{0, 500, 300, 500});
    Notification n = b.build();
    n.flags |= Notification.FLAG_INSISTENT; // keep ringing until dismissed
    ((NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, n);
  }
}
