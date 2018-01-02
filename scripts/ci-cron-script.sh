#!/usr/bin/env bash
if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    ./gradlew jacocoTestReportRelease connectedReleaseAndroidTest lintRelease app:assembleRelease -PdisablePreDex --continue --profile --daemon --parallel
fi
