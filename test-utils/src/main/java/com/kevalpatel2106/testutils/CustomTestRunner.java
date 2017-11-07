/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.testutils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import java.lang.reflect.Method;

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
            disableAnimations(context);
            unlockScreen(context, CustomTestRunner.class.getSimpleName());
            keepScreenAwake(context, CustomTestRunner.class.getSimpleName());
        });
        super.onStart();
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        super.finish(resultCode, results);

        //Re-enable all the animations.
        enableAnimations(getContext());
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

    /**
     * Disable all animations by applying 0 scale to {@link #setSystemAnimationsScale(float)}.
     *
     * @param context Instance of the caller.
     */
    private void disableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(0.0f);
        }
    }

    /**
     * Enable all animations by applying 1 scale to {@link #setSystemAnimationsScale(float)}.
     *
     * @param context Instance of the caller.
     */
    private void enableAnimations(Context context) {
        int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            setSystemAnimationsScale(1.0f);
        }
    }

    /**
     * Set the system animation scale.
     *
     * @param animationScale Scale.
     */
    private void setSystemAnimationsScale(float animationScale) {
        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            @SuppressLint("PrivateApi") Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                currentScales[i] = animationScale;
            }
            setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
            Log.d(TAG, "Changed permissions of animations");
        } catch (Exception e) {
            Log.e(TAG, "Could not change animation scale to " + animationScale + " :'(");
        }
    }
}