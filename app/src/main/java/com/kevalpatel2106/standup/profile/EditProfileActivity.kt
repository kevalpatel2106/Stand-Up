package com.kevalpatel2106.standup.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.annotation.VisibleForTesting
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.rulerview.ObservableHorizontalScrollView
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.dashboard.DashboardActivity
import com.kevalpatel2106.standup.profile.repo.GetProfileResponse
import com.kevalpatel2106.standup.profile.repo.SaveProfileResponse
import com.kevalpatel2106.utils.showSnack
import kotlinx.android.synthetic.main.activity_edit_profile.*


/**
 * This activity will take the user information like weight, height and sleep hours while user
 * opens app for the first time.
 *
 * @author <a href="https://github.com/kevalpatel2106">Keval</a>
 */
@UIController
class EditProfileActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [EditProfileActivity] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    private var selectedWeight: Float = 0.0f
    private var selectedHeight: Float = 0.0f

    @VisibleForTesting
    internal lateinit var mModel: EditProfileModel
    private var btnMenuSave: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setToolbar(R.id.toolbar, getString(R.string.title_activity_user_profile), true)

        setHeightPicker()
        setWeightPicker()

        setViewModel()
    }

    /**
     * Set the [EditProfileModel] and the observers.
     */
    private fun setViewModel() {
        mModel = ViewModelProviders.of(this).get(EditProfileModel::class.java)

        //Observe user profile
        mModel.mUserProfile.observe(this@EditProfileActivity, Observer<GetProfileResponse> {
            it?.let {
                edit_profile_height_picker.scrollToValue(it.heightFloat())
                edit_profile_weight_picker.scrollToValue(it.weightFloat())
                edit_profile_name_et.setText(it.name)
                profile_gender_radio_male.isChecked = it.gender == AppConfig.GENDER_MALE
                profile_gender_radio_female.isChecked = it.gender == AppConfig.GENDER_FEMALE
            }
        })

        //Monitor save profile api call.
        mModel.mProfileUpdateStatus.observe(this@EditProfileActivity, Observer<SaveProfileResponse> {
            showSnack(R.string.message_profile_updated)
            Handler().postDelayed({

                //Open the dashboard if nothing in the back stack
                if (isTaskRoot) DashboardActivity.launch(this@EditProfileActivity)

                finish()
            }, AppConfig.SNACKBAR_TIME)

            //Log the event
            logEvent(AnalyticsEvents.EVENT_PROFILE_UPDATED)
        })

        //Monitor all error messages.
        mModel.errorMessage.observe(this@EditProfileActivity, Observer {
            it!!.getMessage(this@EditProfileActivity)?.let { showSnack(it) }
        })

        //Monitor if the current user profile loaded or not.
        mModel.isLoadingProfile.observe(this@EditProfileActivity, Observer<Boolean> {
            invalidateOptionsMenu()

            if (it!!) {
                edit_profile_view_flipper.displayedChild = 0
            } else {
                edit_profile_view_flipper.displayedChild = 1
            }
        })

        //Monitor if the saving profile completed or not?
        mModel.isSavingProfile.observe(this@EditProfileActivity, Observer<Boolean> {
            invalidateOptionsMenu()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)

        //Remove any action overlay
        btnMenuSave = menu.findItem(R.id.action_save)
        btnMenuSave!!.isVisible = !mModel.isLoadingProfile.value!!
        if (mModel.isSavingProfile.value!!) {

            //Display the progressbar in toolbar
            val progressBar = ProgressBar(this)
            progressBar.setPadding(25, 25, 25, 25)
            btnMenuSave?.actionView = progressBar
            btnMenuSave?.isEnabled = false
        } else {

            btnMenuSave?.isEnabled = true          // Enable click
            btnMenuSave?.actionView = null       // Remove action view.
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                //Save the profile.
                mModel.saveMyProfile(name = edit_profile_name_et.getTrimmedText(),
                        photo = null,
                        height = selectedHeight,
                        weight = selectedWeight,
                        isMale = profile_gender_radio_male.isChecked)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * This method will be called whenever activity is created for the first time and view is still
     * not inflated.
     */
    override fun runItForFirstCreation() {
        super.runItForFirstCreation()

        //These are the initial values of the height and weight pickers./
        selectedWeight = AppConfig.MIN_WEIGHT + 30
        selectedHeight = AppConfig.MIN_HEIGHT + 60
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.let {
            //Save the state of pickers
            outState.putFloat(KEY_SELECTED_HEIGHT, selectedHeight)
            outState.putFloat(KEY_SELECTED_WEIGHT, selectedWeight)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {

            //Restore the state of pickers.
            selectedHeight = savedInstanceState.getFloat(KEY_SELECTED_HEIGHT)
            edit_profile_height_picker.setInitValue(selectedHeight)

            selectedWeight = savedInstanceState.getFloat(KEY_SELECTED_WEIGHT)
            edit_profile_weight_picker.setInitValue(selectedWeight)
        }
    }

    /**
     * Set up the weight picker.
     *
     * @see edit_profile_weight_picker
     */
    private fun setWeightPicker() {
        edit_profile_weight_picker.setMinMaxValue(AppConfig.MIN_WEIGHT, AppConfig.MAX_WEIGHT)
        edit_profile_weight_picker.setValueTypeMultiple(10)
        edit_profile_weight_picker.setOnScrollChangedListener(object : ObservableHorizontalScrollView.OnScrollChangedListener {

            override fun onScrollChanged(p0: ObservableHorizontalScrollView?, p1: Int, p2: Int) {
                edit_profile_selected_weight_tv.text = String.format("%d Kgs", edit_profile_weight_picker.getCurrentValue(p1))
            }

            override fun onScrollStopped(p0: Int, p1: Int) {
                selectedWeight = edit_profile_weight_picker.getValueAndScrollItemToCenter(p0).toFloat()
            }
        })
    }

    /**
     * Set up the height picker.
     *
     * @see edit_profile_height_picker
     */
    private fun setHeightPicker() {
        edit_profile_height_picker.setMinMaxValue(AppConfig.MIN_HEIGHT, AppConfig.MAX_HEIGHT)
        edit_profile_height_picker.setValueTypeMultiple(10)
        edit_profile_height_picker.setOnScrollChangedListener(object : ObservableHorizontalScrollView.OnScrollChangedListener {
            override fun onScrollChanged(p0: ObservableHorizontalScrollView?, p1: Int, p2: Int) {
                edit_profile_selected_height_tv.text = String.format("%d cms", edit_profile_height_picker.getCurrentValue(p1))
            }

            override fun onScrollStopped(p0: Int, p1: Int) {
                selectedHeight = edit_profile_height_picker.getValueAndScrollItemToCenter(p0).toFloat()
            }
        })
    }

    private val KEY_SELECTED_HEIGHT = "key_selected_height"
    private val KEY_SELECTED_WEIGHT = "key_selected_weight"
}
