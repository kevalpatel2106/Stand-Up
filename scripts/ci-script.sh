#!/usr/bin/env bash
./gradlew test app:assembleDebug jacocoTestReport --continue --profile --parallel --daemon

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh
fi