package com.kevalpatel2106.standup.about.report

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.Validator

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class ReportIssueViewModel : BaseViewModel() {
    internal val versionUpdateResult = MutableLiveData<Update>()

    fun checkForUpdate(context: Context) {
        //Check application updates.
        AppUpdaterUtils(context.applicationContext)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .withListener(object : AppUpdaterUtils.UpdateListener {
                    override fun onSuccess(update: Update?, isUpdateAvailable: Boolean) {
                        if (isUpdateAvailable) {
                            //Update available
                            update?.let { versionUpdateResult.value = update }
                        } else {
                            //No update available
                            //Do nothing
                            versionUpdateResult.value = null
                        }
                    }

                    override fun onFailed(error: AppUpdaterError?) {
                        errorMessage.value = ErrorMessage(R.string.check_update_error_message)
                        versionUpdateResult.value = null
                    }
                })
                .start()
    }

    fun reportIssue(title: String, message: String) {
        //Check title
        if (!Validator.isValidTitle(title)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_issue_title)
            return
        }

        //Check description
        if (!Validator.isValidDescription(message)) {
            errorMessage.value = ErrorMessage(R.string.error_invalid_issue_description)
            return
        }

        //Report the issue
        //TODO Report to the server
    }
}