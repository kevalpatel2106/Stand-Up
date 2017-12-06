package com.kevalpatel2106.standup.dashboard

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.standup.R
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

        setUpDrawer()
    }

    /**
     * Set he [drawer_layout] and sync the state of the [drawerToggle] with the drawer.
     */
    private fun setUpDrawer() {
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

        dashboard_navigation_view.setNavigationItemSelectedListener {
            return@setNavigationItemSelectedListener when (it.itemId) {
                R.id.nav_home -> {
                    //TODO Home fragment
                    false
                }
                else -> {
                    throw IllegalStateException("No navigation item with id:" + it)
                }
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

}
