package com.vikka.smartalarm;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
  static volatile boolean foreground = false;

  @Override
  public void onCreate(Bundle b) {
    registerPlugin(AlarmPlugin.class);
    super.onCreate(b);
    if (Build.VERSION.SDK_INT >= 27) { setShowWhenLocked(true); setTurnScreenOn(true); }
    else getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
  }
  @Override public void onResume() { super.onResume(); foreground = true; }
  @Override public void onPause() { foreground = false; super.onPause(); }
}
