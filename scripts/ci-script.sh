#!/usr/bin/env bash
./gradlew connectedAndroidTest jacocoTestReportDebug app:assembleDebug -PdisablePreDex --continue --profile --daemon --parallel
if [ $? != 0 ]
then
 exit $?
fi

if [ "$TRAVIS_EVENT_TYPE" == "cron" ]; then

    # Check for the dependency report
    chmod +x ./scripts/dependency_check.sh
    bash ./scripts/dependency_check.sh

    # Create the clean release build with the release test reports
    ./gradlew --stop
    ./gradlew jacocoTestReportRelease lintRelease app:assembleRelease -PdisablePreDex --continue --profile --daemon --parallel
    if [ $? != 0 ]
    then
     exit $?
    fi

fi