#!/usr/bin/env bash
mkdir ../build
mkdir ../build/reports
./gradlew dependencyUpdates -Drevision=release -DoutputFormatter=json,xml -DoutputDir=../build/reports/