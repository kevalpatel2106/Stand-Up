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

package com.kevalpatel2106.testutils

import android.support.test.rule.ActivityTestRule
import android.support.v4.app.Fragment

/**
 * Created by Keval on 21-Jul-17.
 */

class FragmentTestRule<F : Fragment>(private val mFragmentClass: Class<F>)
    : ActivityTestRule<FragmentRuleActivity>(FragmentRuleActivity::class.java, true, false) {

    lateinit var fragment: F

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()

        try {
            //Instantiate and insert the fragment into the container layout
            val manager = activity.supportFragmentManager
            val transaction = manager.beginTransaction()
            fragment = mFragmentClass.newInstance()
            transaction.replace(R.id.container, fragment)
            transaction.commit()
        } catch (e: InstantiationException) {
            org.junit.Assert.fail(String.format("%s: Could not insert %s into FragmentRuleActivity: %s",
                    javaClass.simpleName,
                    mFragmentClass.simpleName,
                    e.message))
        } catch (e: IllegalAccessException) {
            org.junit.Assert.fail(String.format("%s: Could not insert %s into FragmentRuleActivity: %s", javaClass.simpleName, mFragmentClass.simpleName, e.message))
        }

    }
}
