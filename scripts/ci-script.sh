#!/usr/bin/env bash
./gradlew jacocoTestReportDebug --continue --daemon
./gradlew app:assembleDebug lintDebug --profile --daemon --parallel

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    # Create the clean release build with the release test reports
    ./gradlew --stop
    ./gradlew jacocoTestReportRelease --continue --daemon
    ./gradlew app:assembleRelease lintRelease --profile --daemon --parallel
fi