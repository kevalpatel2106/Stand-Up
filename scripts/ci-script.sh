#!/usr/bin/env bash
./gradlew app:assembleDebug jacocoTestReportDebug --continue --profile --daemon --parallel

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh
fi