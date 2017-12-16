#!/usr/bin/env bash
../gradlew dependencyUpdates -Drevision=release -DoutputFormatter=json,xml -DoutputDir=../reports