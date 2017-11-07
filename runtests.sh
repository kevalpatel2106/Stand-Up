#!/usr/bin/env bash
./gradlew clean connectedAndroidTest test jacocoTestReport mergeAndroidReports --continue --stacktrace --profile