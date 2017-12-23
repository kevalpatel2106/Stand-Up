package com.kevalpatel2106.standup.diary

import android.arch.lifecycle.MutableLiveData
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.userActivity.UserActivity

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(DiaryFragment::class)
internal class DiaryViewModel : BaseViewModel() {

    val activities = MutableLiveData<ArrayList<UserActivity>>()

    init {
        activities.value = ArrayList()

    }

    internal fun loadNext() {

    }

}