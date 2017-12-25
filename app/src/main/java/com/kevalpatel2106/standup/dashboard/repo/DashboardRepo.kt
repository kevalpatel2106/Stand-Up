package com.kevalpatel2106.standup.dashboard.repo

import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.Flowable

/**
 * Created by Kevalpatel2106 on 25-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
interface DashboardRepo {

    fun getTodayEvents(): Flowable<List<UserActivity>>
}