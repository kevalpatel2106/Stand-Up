#!/usr/bin/env bash
./gradlew test app:assembleDebug jacocoTestReport mergeAndroidReports --continue --profile --parallel --daemon