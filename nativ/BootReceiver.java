package com.vikka.smartalarm;

import android.content.*;
import org.json.*;

public class BootReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context c, Intent in) {
    try {
      JSONArray a = new JSONArray(c.getSharedPreferences("vikka", 0).getString("list", "[]"));
      long now = System.currentTimeMillis();
      AlarmPlugin.channel(c);
      for (int i = 0; i < a.length(); i++) {
        JSONObject o = a.getJSONObject(i);
        if (o.getLong("at") > now) AlarmPlugin.schedule(c, o.getInt("id"), o.getLong("at"), o.optString("title"), o.optString("body"));
      }
    } catch (Exception ignored) {}
  }
}
