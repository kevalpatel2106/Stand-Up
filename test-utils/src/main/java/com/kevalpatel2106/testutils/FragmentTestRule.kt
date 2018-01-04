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

package com.kevalpatel2106.testutils

import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment

/**
 * Created by Keval on 21-Jul-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class FragmentTestRule<F : Fragment>(fragmentClass: Class<F>,
                                     fragmentInstance: F? = null)
    : ActivityTestRule<FragmentRuleActivity>(FragmentRuleActivity::class.java) {

    lateinit var fragment: F

    init {
        if (fragmentInstance == null) {
            try {
                fragment = fragmentClass.newInstance()
            } catch (e: InstantiationException) {
                org.junit.Assert.fail(String.format("%s: Could not insert %s into FragmentRuleActivity: %s",
                        javaClass.simpleName,
                        fragmentClass.simpleName,
                        e.message))
            } catch (e: IllegalAccessException) {
                org.junit.Assert.fail(String.format("%s: Could not insert %s into FragmentRuleActivity: %s", javaClass.simpleName, fragmentClass.simpleName, e.message))
            }
        } else {
            fragment = fragmentInstance
        }
    }

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()

        //Instantiate and insert the fragment into the container layout
        val manager = activity.supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()


    }
}
