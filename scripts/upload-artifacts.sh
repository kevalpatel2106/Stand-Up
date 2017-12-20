#!/usr/bin/env bash

#Prepare output folder
mkdir /home/travis/build/kevalpatel2106/Stand-Up/output

mv /home/travis/build/kevalpatel2106/Stand-Up/app/build/outputs/apk /home/travis/build/kevalpatel2106/Stand-Up/output/

mv /home/travis/build/kevalpatel2106/Stand-Up/app/build/outputs/reports /home/travis/build/kevalpatel2106/Stand-Up/output/

mv /home/travis/build/kevalpatel2106/Stand-Up/app/build/reports/logs /home/travis/build/kevalpatel2106/Stand-Up/output/

mv /home/travis/build/kevalpatel2106/Stand-Up/build/reports/profile /home/travis/build/kevalpatel2106/Stand-Up/output/
rm -rf /home/travis/build/kevalpatel2106/Stand-Up/build/reports/profile
mv /home/travis/build/kevalpatel2106/Stand-Up/build/reports ./

cd /home/travis/build/kevalpatel2106/Stand-Up/scripts
chmod +x dropbox_uploader.sh

#Upload to artifacts to drop box
bash dropbox_uploader.sh -p upload /home/travis/build/kevalpatel2106/Stand-Up/output /Stand-Up/Artifacts/$TRAVIS_BUILD_NUMBER/output
