#!/usr/bin/env bash
./gradlew app:assembleDebug jacocoTestReportDebug --continue --profile --daemon --parallel

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    # Create the clean release build with the release test reports
    ./gradlew clean testRelease app:assembleRelease --continue --profile --daemon --parallel
fi