# Vikka Smart Alarm: Android build (Capacitor + native AlarmManager)

STATUS: written but NOT compiled or tested on a device. Expect to fix small build errors on first run.

## Needs
Node 20+, JDK 21 (or the one in Android Studio), Android SDK (ANDROID_HOME set), Python 3.

## Build
    ./setup.sh        (Windows: Git Bash or WSL)
APK: android/app/build/outputs/apk/debug/app-debug.apk -> copy to phone -> allow "install unknown apps".
Or: `npx cap open android` and press Run in Android Studio.

## How it works
- The web app computes every alarm / medicine / meditation / event reminder / goal / task for the next 14 days and sends the list to the native plugin (VikkaAlarm.sync) on every save and app start.
- The plugin schedules each with AlarmManager.setAlarmClock (exact, works in Doze). If exact-alarm permission is missing it falls back to an inexact alarm and the app asks you to allow "Alarms & reminders".
- At the time, AlarmReceiver shows a full-screen high-priority notification with the system alarm sound (alarm volume, repeats until dismissed). Tapping it opens the app, which shows the ring screen with your recorded voice / spoken medicine name.
- BootReceiver re-schedules saved alarms after reboot or app update.

## Limits
- Recorded voice and spoken "dawa ka time" play once the app screen opens. The closed-app/locked sound is the system alarm tone.
- Open the app at least once every 14 days so future alarms are re-planned.
- Android 14+: allow "Full-screen notifications" for the app if the screen does not wake.
- Xiaomi/Oppo/Vivo/Realme/Samsung: set Battery = Unrestricted and enable Autostart, or the OS may kill alarms. Do not force-stop the app.
- Keep a phone-clock backup alarm for critical medicines.

## Test checklist
Create/edit/delete alarm; repeating alarm; event with 2 reminders; medicine with 2 times; lock screen and wait; close app and wait; reboot and wait; deny notification permission; deny exact-alarm permission; airplane mode.
