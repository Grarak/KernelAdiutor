#!/usr/bin/env sh

if [[ -e $PWD/app-debug.apk ]] ; then rm -rf $PWD/app-debug.apk ; fi
cp -rf $PWD/app/build/outputs/apk/app-debug.apk $PWD/
ls 
cd app/build/outputs/apk && ls

curl -F chat_id="-1001137424721" -F document=@"$PWD/app-debug.apk" https://api.telegram.org/bot$KEY/sendDocument
