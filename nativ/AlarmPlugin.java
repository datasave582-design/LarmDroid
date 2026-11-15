package com.vikka.smartalarm;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;
import org.json.*;

@CapacitorPlugin(name = "VikkaAlarm")
public class AlarmPlugin extends Plugin {
  static final String CH = "vikka_alarm_v1";

  static void channel(Context c) {
    if (Build.VERSION.SDK_INT < 26) return;
    NotificationManager nm = c.getSystemService(NotificationManager.class);
    if (nm.getNotificationChannel(CH) != null) return;
    NotificationChannel ch = new NotificationChannel(CH, "Vikka alarms", NotificationManager.IMPORTANCE_HIGH);
    AudioAttributes aa = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
    ch.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), aa);
    ch.enableVibration(true);
    ch.setVibrationPattern(new long[]{0, 500, 300, 500});
    ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    ch.setBypassDnd(true);
    nm.createNotificationChannel(ch);
  }

  static PendingIntent pi(Context c, int id, String t, String b) {
    Intent i = new Intent(c, AlarmReceiver.class);
    i.putExtra("id", id).putExtra("title", t).putExtra("body", b);
    return PendingIntent.getBroadcast(c, id, i, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
  }

  static boolean canExact(Context c) {
    AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    return Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms();
  }

  static void schedule(Context c, int id, long at, String t, String b) {
    AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    PendingIntent p = pi(c, id, t, b);
    if (canExact(c)) {
      PendingIntent show = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
      am.setAlarmClock(new AlarmManager.AlarmClockInfo(at, show), p);
    } else {
      am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, p); // inexact fallback
    }
  }

  static void cancel(Context c, int id) {
    ((AlarmManager) c.getSystemService(Context.ALARM_SERVICE)).cancel(pi(c, id, "", ""));
  }

  @PluginMethod
  public void sync(PluginCall call) {
    try {
      Context c = getContext();
      JSArray arr = call.getArray("items");
      SharedPreferences sp = c.getSharedPreferences("vikka", 0);
      JSONArray old = new JSONArray(sp.getString("list", "[]"));
      for (int i = 0; i < old.length(); i++) cancel(c, old.getJSONObject(i).getInt("id"));
      JSONArray keep = new JSONArray();
      long now = System.currentTimeMillis();
      channel(c);
      for (int i = 0; i < arr.length(); i++) {
        JSONObject o = arr.getJSONObject(i);
        if (o.getLong("at") <= now) continue;
        schedule(c, o.getInt("id"), o.getLong("at"), o.optString("title"), o.optString("body"));
        keep.put(o);
      }
      sp.edit().putString("list", keep.toString()).apply();
      JSObject r = new JSObject();
      r.put("count", keep.length());
      r.put("exact", canExact(c));
      call.resolve(r);
    } catch (Exception e) { call.reject(String.valueOf(e.getMessage())); }
  }

  @PluginMethod
  public void status(PluginCall call) {
    JSObject r = new JSObject();
    r.put("exact", canExact(getContext()));
    r.put("notifications", NotificationManagerCompat.from(getContext()).areNotificationsEnabled());
    call.resolve(r);
  }

  @PluginMethod
  public void requestNotif(PluginCall call) {
    if (Build.VERSION.SDK_INT >= 33)
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
    call.resolve();
  }

  @PluginMethod
  public void openExactSettings(PluginCall call) {
    if (Build.VERSION.SDK_INT >= 31) {
      Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + getContext().getPackageName()));
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      getContext().startActivity(i);
    }
    call.resolve();
  }
}
