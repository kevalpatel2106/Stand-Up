package com.kevalpatel2106.standup.dashboard

import android.annotation.SuppressLint
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
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.base.view.BaseTextView
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.about.AboutActivity
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.profile.EditProfileActivity
import com.kevalpatel2106.standup.settings.SettingsActivity
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_dashboard.*

/**
 * Main activity which user will see after opening the application.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
class DashboardActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [DashboardActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, DashboardActivity::class.java)
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(launchIntent)
        }
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var model: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this@DashboardActivity).get(DashboardViewModel::class.java)

        setContentView(R.layout.activity_dashboard)

        //Set the toolbar
        setToolbar(R.id.toolbar, "Dashboard", true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        setUpDrawer(savedInstanceState == null)
    }

    /**
     * Set he [drawer_layout] and sync the state of the [drawerToggle] with the drawer.
     */
    @SuppressLint("VisibleForTests")
    private fun setUpDrawer(isFirstRun: Boolean) {
        //Set up the header
        val header = dashboard_navigation_view.getHeaderView(0)
        header.findViewById<BaseTextView>(R.id.nav_email_tv).text = UserSessionManager.email ?: ""
        header.findViewById<BaseTextView>(R.id.nav_name_tv).text = UserSessionManager.displayName ?: "Hi there!"

        val headerProfileIv = header.findViewById<CircleImageView>(R.id.nav_profile_iv)
        if (UserSessionManager.photo.isNullOrEmpty()) {
            headerProfileIv.setImageResource(R.drawable.ic_user_profile_default)
        } else {
            Glide.with(this@DashboardActivity)
                    .load(UserSessionManager.photo)
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

        if (isFirstRun) {
            //Set the home as selected
            handleDrawerNavigation(DrawerItem.HOME)

            //Check if the drawer tutorial completed?
            if (!SharedPrefsProvider.getBoolFromPreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED)) {
                //Mark tutorial completed
                SharedPrefsProvider.savePreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED, true)

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
                false
            }
            DrawerItem.DIARY -> {
                dashboard_navigation_view.menu.findItem(R.id.nav_diary).isChecked = true
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_container, model.diaryFragment)
                        .commit()
                false
            }
            DrawerItem.STATS -> {
                dashboard_navigation_view.menu.findItem(R.id.nav_stats).isChecked = true
                supportFragmentManager.beginTransaction()
                        .replace(R.id.dashboard_container, model.statsFragment)
                        .commit()
                false
            }
            DrawerItem.PROFILE -> {
                //Open the edit profile
                EditProfileActivity.launch(this@DashboardActivity)
                false
            }
            DrawerItem.SETTING -> {
                //Open the edit profile
                SettingsActivity.launch(this@DashboardActivity)
                false
            }
            DrawerItem.ABOUT -> {
                //Launch the about screen
                AboutActivity.launch(this@DashboardActivity)
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