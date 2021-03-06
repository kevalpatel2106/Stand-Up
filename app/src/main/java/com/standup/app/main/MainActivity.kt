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

package com.standup.app.main

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.VisibleForTesting
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.purchase.PurchaseActivity
import com.kevalpatel2106.common.view.BaseTextView
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.SwipeDetector
import com.standup.app.R
import com.standup.app.about.AboutApi
import com.standup.app.profile.ProfileApi
import com.standup.app.settings.SettingsApi
import com.standup.core.Core
import dagger.Lazy
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_drawer_footer.*
import javax.inject.Inject

/**
 * Main activity which user will see after opening the application.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class MainActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [MainActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, MainActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    internal lateinit var core: Lazy<Core>

    @Inject
    internal lateinit var mProfileApi: ProfileApi

    @Inject
    internal lateinit var mSettingsApi: SettingsApi

    @Inject
    internal lateinit var mAboutApi: AboutApi

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerMainComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@MainActivity)

        model = ViewModelProviders.of(this@MainActivity).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)

        //Set the toolbar
        setToolbar(R.id.toolbar, R.string.application_name, true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        setUpDrawer(savedInstanceState == null)
        setBuyProButton()
    }

    /**
     * Set he [drawer_layout] and sync the state of the [drawerToggle] with the drawer.
     */
    @SuppressLint("VisibleForTests")
    private fun setUpDrawer(isFirstRun: Boolean) {
        //Set up the header
        val header = dashboard_navigation_view.getHeaderView(0)
        header.findViewById<BaseTextView>(R.id.nav_email_tv).text = userSessionManager.email ?: ""
        header.findViewById<BaseTextView>(R.id.nav_name_tv).text = userSessionManager.displayName ?: "Hi there!"

        val headerProfileIv = header.findViewById<CircleImageView>(R.id.nav_profile_iv)
        if (userSessionManager.photo.isNullOrEmpty()) {
            headerProfileIv.setImageResource(R.drawable.ic_user_profile_default)
        } else {
            Glide.with(this@MainActivity)
                    .load(userSessionManager.photo)
                    .thumbnail(0.1f)
                    .into(headerProfileIv)
        }

        // Set the drawer toggle as the DrawerListener
        drawerToggle = object : ActionBarDrawerToggle(this, drawer_layout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state.  */
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                invalidateOptionsMenu() // creates call to onPrepareOptionsMenu()
            }
        }
        drawer_layout.addDrawerListener(drawerToggle)

        //Set the click listener
        dashboard_navigation_view.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener handleDrawerNavigation(getDrawerItem(it.itemId))
        }

        //Set the swipe gesture to open the drawer
        dashboard_container.setOnTouchListener(object : SwipeDetector() {
            override fun onLeftToRightSwipe() {
                super.onLeftToRightSwipe()

                //Open the drawer
                drawer_layout.openDrawer(Gravity.START)
            }

            override fun onRightToLeftSwipe() {
                super.onRightToLeftSwipe()

                //Close the drawer
                drawer_layout.closeDrawer(Gravity.START)
            }
        })

        if (isFirstRun) {
            //Set the home as selected
            handleDrawerNavigation(DrawerItem.HOME)

            //Check if the drawer tutorial completed?
            if (!sharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED)) {
                //Mark tutorial completed
                sharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED, true)

                headerProfileIv.scaleX = 0.7f
                headerProfileIv.scaleY = 0.7f

                //Open the drawer automatically
                drawer_layout.openDrawer(Gravity.START)

                //Animate the icon
                headerProfileIv.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300L)
                        .setStartDelay(600L)
                        .start()
            }
        }
    }

    /**
     * Set the buy pro button at the footer of the navigation drawer.
     */
    private fun setBuyProButton() {

        model.isDisplayBuyPro.observe(this@MainActivity, Observer {
            it?.let { nav_buy_pro.visibility = if (it) View.GONE else View.VISIBLE }
        })

        nav_buy_pro.setOnClickListener {
            //Launch the about screen
            PurchaseActivity.launch(this@MainActivity)
        }
    }

    /**
     * Navigate the user to next screen/fragment from the navigation drawer options based on the
     * selected [item]. This will close the navigation drawer and handle the item selection in
     * navigation drawer.
     */
    private fun handleDrawerNavigation(item: DrawerItem): Boolean {
        drawer_layout.closeDrawer(Gravity.START)

        return when (item) {
            DrawerItem.HOME -> {
                dashboard_navigation_view.menu.findItem(R.id.nav_home).isChecked = true
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_container, model.homeFragment)
                        .commit()
                setToolbar(R.id.toolbar, R.string.nav_header_home, true)
                false
            }
            DrawerItem.DIARY -> {
                dashboard_navigation_view.menu.findItem(R.id.nav_diary).isChecked = true
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_container, model.diaryFragment)
                        .commit()
                setToolbar(R.id.toolbar, R.string.nav_header_dairy, true)
                false
            }
            DrawerItem.STATS -> {
                dashboard_navigation_view.menu.findItem(R.id.nav_stats).isChecked = true
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_container, model.statsFragment)
                        .commit()
                setToolbar(R.id.toolbar, R.string.nav_header_stats, true)
                false
            }
            DrawerItem.PROFILE -> {
                //Open the edit profile
                mProfileApi.openProfile(this@MainActivity, userSessionManager)
                false
            }
            DrawerItem.SETTING -> {
                //Open the edit profile
                mSettingsApi.openSettings(this@MainActivity)
                false
            }
            DrawerItem.ABOUT -> {
                //Launch the about screen
                mAboutApi.openAbout(this@MainActivity)
                false
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (drawerToggle.onOptionsItemSelected(item)) {
            // Pass the event to ActionBarDrawerToggle, if it returns
            // true, then it has handled the app icon touch event
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.START)) {
            //Close the drawer if the drawer is open.
            drawer_layout.closeDrawer(Gravity.START)
        } else {
            //Handle back press
            super.onBackPressed()
        }
    }

    @VisibleForTesting
    internal fun getDrawerItem(@IdRes item: Int): DrawerItem {
        return when (item) {
            R.id.nav_home -> DrawerItem.HOME
            R.id.nav_diary -> DrawerItem.DIARY
            R.id.nav_stats -> DrawerItem.STATS

            R.id.nav_profile -> DrawerItem.PROFILE
            R.id.nav_settings -> DrawerItem.SETTING

            R.id.nav_about -> DrawerItem.ABOUT

            else -> throw IllegalStateException("Invalid menu resource id.")
        }
    }
}
