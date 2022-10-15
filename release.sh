#!/bin/bash
cd "$(dirname "$0")"
./gradlew bundleRelease -DreleaseBuild=true && open androidApp/build/outputs/bundle/release/
