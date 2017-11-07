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

package com.kevalpatel2106.base;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.testutils.BaseTestClass;
import com.kevalpatel2106.testutils.CustomMatchers;
import com.kevalpatel2106.testutils.FragmentRuleActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 26-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseActivityTest extends BaseTestClass {
    private static final String ACTIVITY_TITLE = "Test Activity";

    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);

    private TestActivity mTestActivity;

    @Before
    public void init() {
        mTestActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void checkToolbar() throws Exception {
        //Test with the string title
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, true));
        CustomMatchers.matchToolbarTitle(ACTIVITY_TITLE).check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));

        //Test with the string resource title
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, R.string.test, true));
        CustomMatchers.matchToolbarTitle(mTestActivity.getString(R.string.test)).check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));

        //Hide home button
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, false));
        try {
            Espresso.onView(withContentDescription("Navigate up")).perform(click());
            fail();
        } catch (NoMatchingViewException e) {
            //Pass
        }
    }

    @Test
    public void checkAddDisposable() throws Exception {
        Assert.assertNotNull(mTestActivity.getDisposables());

        mTestActivity.addSubscription(null);
        Assert.assertEquals(mTestActivity.getDisposables().size(), 0);

        mTestActivity.addSubscription(Observable.just("1").subscribe());
        Assert.assertEquals(mTestActivity.getDisposables().size(), 1);
    }

    @After
    public void tearUp() {
        mTestActivity.finish();
    }

    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}