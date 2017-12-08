package com.kevalpatel2106.standup.dashboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.base.BaseTextView
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.profile.EditProfileActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setToolbar(R.id.toolbar, "Dashboard", true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        setUpDrawer(savedInstanceState == null)
    }

    /**
     * Set he [drawer_layout] and sync the state of the [drawerToggle] with the drawer.
     */
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

            //Close the drawer
            drawer_layout.closeDrawer(Gravity.START)

            return@setNavigationItemSelectedListener when (it.itemId) {
                R.id.nav_home -> {
                    it.isChecked = true
                    //TODO Home fragment
                    false
                }
                R.id.nav_diary -> {
                    it.isChecked = true
                    //TODO Diary fragment
                    false
                }
                R.id.nav_stats -> {
                    it.isChecked = true
                    //TODO Stats fragment
                    false
                }
                R.id.nav_profile -> {
                    EditProfileActivity.launch(this@DashboardActivity)
                    false
                }
                R.id.nav_settings -> {
                    //TODO Settings fragment
                    false
                }
                R.id.nav_about -> {
                    //TODO About fragment
                    false
                }
                else -> {
                    throw IllegalStateException("No navigation item with id:" + it)
                }
            }
        }

        if (isFirstRun) {
            //Set the home as selected
            dashboard_navigation_view.setCheckedItem(R.id.nav_home)

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
            drawer_layout.closeDrawer(Gravity.START)
        } else {
            super.onBackPressed()
        }
    }

}
