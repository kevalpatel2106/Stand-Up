#!/usr/bin/env bash
./gradlew clean connectedAndroidTest --continue --stacktrace --profile
./gradlew clean test --continue --stacktrace --profile