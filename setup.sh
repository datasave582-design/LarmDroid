#!/usr/bin/env bash
set -e
npm install
[ -d android ] || npx cap add android
cp native/*.java android/app/src/main/java/com/vikka/smartalarm/
python3 native/patch_manifest.py
npx cap sync android
(cd android && ./gradlew assembleDebug)
echo "APK: android/app/build/outputs/apk/debug/app-debug.apk"
