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

package com.kevalpatel2106.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.ButterKnife
import com.kevalpatel2106.utils.ViewUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber

/**
 * Created by Keval on 17-Dec-16.
 * This is the root class for the activity that extends [AppCompatActivity]. Use this class instead
 * of [AppCompatActivity] through out the application.

 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {
    val disposables = CompositeDisposable()

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        //Bind butter knife
        ButterKnife.bind(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) runItForFirstCreation()
    }

    open fun runItForFirstCreation() {
        /* Do nothing */
    }

    /**
     * Set the toolbar of the activity.

     * @param toolbarId    resource id of the toolbar
     * *
     * @param title        title of the activity
     * *
     * @param showUpButton true if toolbar should display up indicator.
     */
    fun setToolbar(toolbarId: Int,
                   @StringRes title: Int,
                   showUpButton: Boolean) {
        val toolbar = findViewById<Toolbar>(toolbarId)
        setSupportActionBar(toolbar)
        setToolbar(title, showUpButton)
    }

    /**
     * Set the toolbar of the activity.

     * @param toolbarId    resource id of the toolbar
     * *
     * @param title        title of the activity
     * *
     * @param showUpButton true if toolbar should display up indicator.
     */
    fun setToolbar(toolbarId: Int,
                   title: String,
                   showUpButton: Boolean) {
        val toolbar = findViewById<Toolbar>(toolbarId)
        Timber.e(toolbar.toString())
        setSupportActionBar(toolbar)
        setToolbar(title, showUpButton)
    }

    /**
     * Set the toolbar.

     * @param title        Activity title string resource
     * *
     * @param showUpButton true if toolbar should display up indicator.
     */
    protected fun setToolbar(@StringRes title: Int,
                             showUpButton: Boolean) {
        setToolbar(getString(title), showUpButton)
    }

    /**
     * Set the toolbar.

     * @param title        Activity title string.
     * *
     * @param showUpButton true if toolbar should display up indicator.
     */
    @SuppressLint("RestrictedApi")
    protected fun setToolbar(title: String,
                             showUpButton: Boolean) {
        //set the title
        supportActionBar!!.title = title

        //Set the up indicator
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(showUpButton)
        supportActionBar!!.setHomeButtonEnabled(showUpButton)
        supportActionBar!!.setDisplayHomeAsUpEnabled(showUpButton)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            //Hide the keyboard if any view is currently in focus.
            if (currentFocus != null) ViewUtils.hideKeyboard(currentFocus!!)
            supportFinishAfterTransition()
            false
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Add the subscription to the [CompositeDisposable].

     * @param disposable [Disposable]
     */
    fun addSubscription(disposable: Disposable?) {
        if (disposable == null) return
        disposables.add(disposable)
    }

    public override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}
