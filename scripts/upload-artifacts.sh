#!/usr/bin/env bash

#Prepare output folder
mkdir ../output && cd ../output
mv ../app/build/outputs/apk ./
mv ../app/build/outputs/reports ./
mv ../app/build/reports/logs ./
mv ../build/reports/profile ./
rm -rf ../build/reports/profile
mv ../app/build/reports/ ./

cd ../scripts
chmod +x dropbox_uploader.sh

#Upload to artifacts to drop box
bash dropbox_uploader.sh -p upload /home/travis/build/kevalpatel2106/Stand-Up/outputs /Stand-Up/Artifacts/$TRAVIS_BUILD_NUMBER/output
