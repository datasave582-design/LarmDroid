p='android/app/src/main/AndroidManifest.xml';s=open(p,encoding='utf-8').read()
if 'VikkaAlarm' not in s:
    perms=''.join(f'    <uses-permission android:name="android.permission.{x}"{y}/>\n' for x,y in [('POST_NOTIFICATIONS',''),('SCHEDULE_EXACT_ALARM',' android:maxSdkVersion="32"'),('USE_EXACT_ALARM',''),('RECEIVE_BOOT_COMPLETED',''),('USE_FULL_SCREEN_INTENT',''),('VIBRATE',''),('WAKE_LOCK',''),('RECORD_AUDIO',''),('MODIFY_AUDIO_SETTINGS','')])
    rec='''    <!-- VikkaAlarm -->
    <receiver android:name=".AlarmReceiver" android:exported="false"/>
    <receiver android:name=".BootReceiver" android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED"/>
            <action android:name="android.intent.action.MY_PACKAGE_REPLACED"/>
        </intent-filter>
    </receiver>
'''
    i=s.index('>',s.index('<manifest'))+1
    s=s[:i]+'\n'+perms+s[i:]
    s=s.replace('</application>',rec+'</application>',1)
    open(p,'w',encoding='utf-8').write(s)
print('manifest patched')
