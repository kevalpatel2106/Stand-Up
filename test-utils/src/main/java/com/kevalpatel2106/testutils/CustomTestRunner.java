/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.testutils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * Tests can fail for other reasons than code, it´ because of the animations and espresso sync and
 * emulator state (screen off or locked)
 * <p/>
 * Before all the tests prepare the device to run tests and avoid these problems.
 * <p/>
 * - Disable animations
 * - Disable keyguard lock
 * - Set it to be awake all the time (dont let the processor sleep)
 *
 * @see <a href="u2020 open source app by Jake Wharton">https://github.com/JakeWharton/u2020</a>
 * @see <a href="Daj gist">https://gist.github.com/daj/7b48f1b8a92abf960e7b</a>
 */
public final class CustomTestRunner extends AndroidJUnitRunner {
    private static final String TAG = "CustomTestRunner";

    @Override
    public void onStart() {
        Context context = CustomTestRunner.this.getTargetContext().getApplicationContext();
        runOnMainSync(() -> {
            unlockScreen(context, CustomTestRunner.class.getSimpleName());
            keepScreenAwake(context, CustomTestRunner.class.getSimpleName());
        });
        super.onStart();
    }

    /**
     * Acquire the wakelock to keep the screen awake.
     *
     * @param context Instance of the app.
     * @param name    Name of the wakelock. (Tag)
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("WakelockTimeout")
    private void keepScreenAwake(Context context, String name) {
        PowerManager power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //noinspection ConstantConditions
        power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, name)
                .acquire();
    }

    /**
     * Unlock the screen.
     *
     * @param context Instance of the app.
     * @param name    Name of the keyguard. (Tag)
     */
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    private void unlockScreen(Context context, String name) {
        KeyguardManager keyguard = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        keyguard.newKeyguardLock(name).disableKeyguard();
    }
}