#!/usr/bin/env bash
./gradlew app:assembleDebug connectedAndroidTest jacocoTestReportDebug --continue --profile --daemon --parallel

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    # Create the clean release build with the release test reports
    ./gradlew --stop
    ./gradlew clean app:assembleRelease connectedAndroidTest jacocoTestReportRelease lintRelease --continue --profile --daemon --parallel
fi