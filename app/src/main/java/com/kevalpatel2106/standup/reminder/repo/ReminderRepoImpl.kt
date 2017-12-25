package com.kevalpatel2106.standup.reminder.repo

import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ReminderRepoImpl : ReminderRepo {


    override fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity) {

        getLatestActivity().subscribe(object : MaybeObserver<UserActivity> {

            override fun onSuccess(lastActivity: UserActivity) {

                //Check if the user activity is changed or not?
                if (lastActivity.type != newActivity.type) {

                    //This is new user activity.
                    //Update the end time of the last user event.
                    lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
                    StandUpDb.getDb().userActivityDao().update(lastActivity)

                    //Add the event to the database
                    StandUpDb.getDb().userActivityDao().insert(newActivity)
                } else {
                    //Activity type did not changed/
                    //Do noting
                }
            }

            override fun onError(e: Throwable) {
                Timber.e(e)
            }

            override fun onComplete() {
                //Add the event to the database
                StandUpDb.getDb().userActivityDao().insert(newActivity)
            }

            override fun onSubscribe(d: Disposable) {
                //Do nothing
            }
        })
    }

    override fun getLatestActivity(): Maybe<UserActivity> = StandUpDb.getDb()
            .userActivityDao()
            .getLatestActivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}