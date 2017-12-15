package com.kevalpatel2106.standup.reminder.repo

import com.kevalpatel2106.standup.StandUpDb
import com.kevalpatel2106.standup.reminder.UserActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Keval on 14/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityRepoImpl : UserActivityRepo {

    override fun insertNewAndTerminatePreviousActivity(newActivity: UserActivity) {

        getLatestActivity().subscribe(object : MaybeObserver<UserActivity> {

            override fun onSuccess(lastActivity: UserActivity) {

                //Check if the user activity is changed or not?
                if (lastActivity.type != newActivity.type) {

                    //This is new user activity.
                    //Update the end time of the last user event.
                    lastActivity.eventEndTimeMills = newActivity.eventStartTimeMills
                    update(lastActivity).subscribe()
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