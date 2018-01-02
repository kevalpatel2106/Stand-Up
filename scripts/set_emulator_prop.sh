#!/usr/bin/env bash
sed -i -e 's/hw.ramSize=512/hw.ramSize=1024/g' ${HOME}/.android/avd/test.avd/config.ini
sed -i -e 's/vm.heapSize=48/vm.heapSize=96/g' ${HOME}/.android/avd/test.avd/config.ini
cat ${HOME}/.android/avd/test.avd/config.ini