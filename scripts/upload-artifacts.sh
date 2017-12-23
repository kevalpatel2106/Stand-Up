#!/usr/bin/env bash

#Prepare output folder
mkdir ./output

#project level artifacts
mv ./reports/profile ./output/profile/
mv ./build/reports/dependencyCheck ./output/dependencyCheck/

#app module data
mv ./app/build/outputs/apk ./output/app/
mv ./app/build/outputs/dexcount ./output/app/
mv ./app/build/reports ./output/app/

#base module
mv ./base/build/reports ./output/base/

#facebook-auth module
mv ./base/build/reports ./output/base/

#google-auth module
mv ./facebook-auth/build/reports ./output/facebook-auth/

#network module
mv ./network/build/reports ./output/network/

#ruler-view module
mv ./ruler-view/build/reports ./output/ruler-view/

#timeline-view module
mv ./timeline-view/build/reports ./output/timeline-view/

#utils module
mv ./utils/build/reports ./output/utils/

#Upload to artifacts to drop box
chmod +x ./scripts/dropbox_uploader.sh
bash ./scripts/dropbox_uploader.sh -p -h upload ./output /Stand-Up/Artifacts/$TRAVIS_BUILD_NUMBER/output
