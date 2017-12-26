#!/usr/bin/env bash

#Prepare output folder
mkdir ./output

# Move cloc file for code language stats
mv ./cloc.txt ./output

#project level artifacts
mv ./reports/profile ./output/profile/
mv ./build/reports/dependencyCheck ./output/dependencyCheck/

#app module data
mv ./app/build/outputs/apk ./output/
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
tar cvf artifacts-$TRAVIS_BUILD_NUMBER-$TRAVIS_EVENT_TYPE.tar output
chmod +x ./scripts/dropbox_uploader.sh
bash ./scripts/dropbox_uploader.sh -p -h upload artifacts-$TRAVIS_BUILD_NUMBER-$TRAVIS_EVENT_TYPE.tar /Stand-Up/Artifacts/
