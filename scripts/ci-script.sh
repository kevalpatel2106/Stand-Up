#!/usr/bin/env bash
if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

#    ./gradlew jacocoTestReportRelease lintRelease app:assembleRelease -PdisablePreDex --continue --profile --daemon --parallel
    ./gradlew lintRelease app:assembleRelease -PdisablePreDex --continue --profile --daemon --parallel
    exit $?
else
    exit 0
fi
