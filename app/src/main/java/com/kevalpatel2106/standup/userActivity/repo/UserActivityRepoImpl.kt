package com.kevalpatel2106.standup.userActivity.repo

import com.kevalpatel2106.standup.StandUpDb
import com.kevalpatel2106.standup.constants.AppConfig
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

    override fun loadUserActivities(afterMills: Long): Flowable<List<UserActivity>> {
        return Flowable.create(FlowableOnSubscribe<List<UserActivity>> { e ->
            //Query the database.
            val dbList = StandUpDb.getDb().userActivityDao().getActivityAfter(afterMills)

            //Check if the enough results are received?
            when {
                dbList.isEmpty() -> {
                    //No more items into the database.
                    //Oldest event time we have is the onw we received in arguments.

                    //TODO make network call
                    e.onNext(dbList)
                }
                dbList.size < AppConfig.PAGE_LIMIT -> {
                    //Not enough items in the database so that we can fill the page.
                    //May be it's time to hit the server for more items.
                    val oldestItemTime = dbList.last().eventStartTimeMills

                    //TODO make network call
                    //Emmit data into the stream
                    e.onNext(dbList)
                }
                else -> {
                    //We have enough data into the database.
                    //Emmit data into the stream
                    e.onNext(dbList)
                    e.onComplete()
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun loadUserActivityByDay(calendar: Calendar): Flowable<List<UserActivity>> {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTimeMills = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 24)
        val endTimeMills = calendar.timeInMillis
        return StandUpDb.getDb().userActivityDao()
                .getActivityBetweenDuration(startTimeMills, endTimeMills)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getTodayEvents(): Flowable<List<UserActivity>> {
        return loadUserActivityByDay(Calendar.getInstance())
    }

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