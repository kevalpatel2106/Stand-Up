#!/usr/bin/env bash
./gradlew jacocoTestReportDebug app:assembleDebug -PdisablePreDex --continue --profile --daemon --parallel
if [ $? -ne "0" ]; then
    exit $?
fi

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    ./gradlew jacocoTestReportRelease lintRelease app:assembleRelease -PdisablePreDex --continue --profile --daemon --parallel
    exit $?
else
    exit 0
fi
