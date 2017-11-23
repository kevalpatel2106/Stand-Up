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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.test.uiautomator.UiDevice;
import android.view.WindowManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * Created by Keval on 10-Apr-17.
 */

public abstract class BaseTestClass {
    @SuppressLint("StaticFieldLeak")
    private static SystemAnimations systemAnimations = new SystemAnimations(getInstrumentation().getContext());

    @BeforeClass
    public static void disableAnimation() {
        systemAnimations.disableAll();
    }

    @AfterClass
    public static void enableAnimation() {
        systemAnimations.enableAll();
    }

    public abstract Activity getActivity();

    @Before
    public void setup() {
        // Unlock the screen if it's locked
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        try {
            device.wakeUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Set the flags on our activity so it'll appear regardless of lock screen state
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getActivity() == null) return;
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });
    }

    protected void switchToLandscape() {
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    protected void switchToPortrait() {
        if (getActivity() != null)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
