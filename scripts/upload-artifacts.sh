#!/usr/bin/env bash

#Prepare output folder
mkdir ./output

mv ./app/build/outputs/apk ./output/

mv ./app/build/outputs/reports ./output/

mv ./app/build/reports/logs ./output/

mv ./build/reports ./output/

chmod +x ./scripts/dropbox_uploader.sh

#Upload to artifacts to drop box
bash ./scripts/dropbox_uploader.sh -p -h upload ./output /Stand-Up/Artifacts/$TRAVIS_BUILD_NUMBER/output
