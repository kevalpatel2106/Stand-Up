package com.kevalpatel2106.standup.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.BaseActivity
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.rulerview.ObservableHorizontalScrollView
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.profile.repo.GetProfileResponse
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

    private val KEY_SELECTED_HEIGHT = "key_selected_height"
    private val KEY_SELECTED_WEIGHT = "key_selected_weight"

    private var selectedWeight: Float = 0.0f
    private var selectedHeight: Float = 0.0f

    @VisibleForTesting
    lateinit var mModel: EditProfileModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = ViewModelProviders.of(this).get(EditProfileModel::class.java)
        mModel.mUserProfile.observe(this@EditProfileActivity, Observer<GetProfileResponse> {
            //TODO Update the UI
        })
        mModel.mProfileUpdateStatus.observe(this@EditProfileActivity, Observer<GetProfileResponse> {
            //TODO Update the UI
        })
        mModel.mIsApiRunning.observe(this@EditProfileActivity, Observer<Boolean> {
            //TODO Update the UI
        })

        setContentView(R.layout.activity_edit_profile)

        setToolbar(R.id.toolbar, getString(R.string.title_activity_user_profile), true)

        //Initialize the height seek bar
        setHeightPicker()

        //Initialize the weight picker
        setWeightPicker()

        if (savedInstanceState == null) {
            //Load current user profile from the repo.
            mModel.loadMyProfile()
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
     */
    private fun setWeightPicker() {
        edit_profile_weight_picker.setMinMaxValue(AppConfig.MIN_WEIGHT, AppConfig.MAX_WEIGHT)
        edit_profile_weight_picker.viewMultipleSize = 3f
        edit_profile_weight_picker.setValueTypeMultiple(10)
        edit_profile_weight_picker.setInitValue(selectedWeight)
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
     */
    private fun setHeightPicker() {
        edit_profile_height_picker.setMinMaxValue(AppConfig.MIN_HEIGHT, AppConfig.MAX_HEIGHT)
        edit_profile_height_picker.viewMultipleSize = 3f
        edit_profile_height_picker.setValueTypeMultiple(10)
        edit_profile_height_picker.setInitValue(selectedHeight)
        edit_profile_height_picker.setOnScrollChangedListener(object : ObservableHorizontalScrollView.OnScrollChangedListener {
            override fun onScrollChanged(p0: ObservableHorizontalScrollView?, p1: Int, p2: Int) {
                edit_profile_selected_height_tv.text = String.format("%d cms", edit_profile_height_picker.getCurrentValue(p1))
            }

            override fun onScrollStopped(p0: Int, p1: Int) {
                selectedHeight = edit_profile_height_picker.getValueAndScrollItemToCenter(p0).toFloat()
            }

        })
    }
}
