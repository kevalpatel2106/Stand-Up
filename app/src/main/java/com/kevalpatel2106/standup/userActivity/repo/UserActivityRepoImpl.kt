package com.kevalpatel2106.standup.userActivity.repo

import com.kevalpatel2106.standup.StandUpDb
import com.kevalpatel2106.standup.userActivity.UserActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityRepoImpl : UserActivityRepo {

    override fun getTodayEvents(): Flowable<List<UserActivity>> {
        val today12am = Calendar.getInstance()
        today12am.set(Calendar.MINUTE, 0)
        today12am.set(Calendar.HOUR, 0)
        today12am.set(Calendar.SECOND, 0)
        today12am.set(Calendar.MILLISECOND, 0)

        return StandUpDb.getDb()
                .userActivityDao()
                .getActivityBetweenDuration(today12am.timeInMillis, System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity) {

        getLatestActivity().subscribe(object : MaybeObserver<UserActivity> {

            override fun onSuccess(lastActivity: UserActivity) {

                //Check if the user activity is changed or not?
                if (lastActivity.type != newActivity.type) {

                    //This is new user activity.
                    //Update the end time of the last user event.
                    lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
                    update(lastActivity).subscribe()

                    //Add the event to the database
                    insert(newActivity).subscribe()
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
                insert(newActivity).subscribe()
            }

            override fun onSubscribe(d: Disposable) {
                //Do nothing
            }
        })
    }

    override fun insert(userActivity: UserActivity): Flowable<Long> {
        return Flowable.create(FlowableOnSubscribe<Long> {

            it.onNext(StandUpDb.getDb().userActivityDao().insert(userActivity))
            it.onComplete()
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun update(userActivity: UserActivity): Flowable<Int> {
        return Flowable.create(FlowableOnSubscribe<Int> {

            it.onNext(StandUpDb.getDb().userActivityDao().update(userActivity))
            it.onComplete()
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getLatestActivity(): Maybe<UserActivity> = StandUpDb.getDb()
            .userActivityDao()
            .getLatestActivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


}