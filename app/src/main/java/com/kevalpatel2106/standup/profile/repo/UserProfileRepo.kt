package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import io.reactivex.Flowable

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
interface UserProfileRepo {

    fun getUserProfile(userId: Long): Flowable<GetProfileResponse>

    fun saveUserProfile(name: String,
                        photo: String?,
                        height: Float,
                        weight: Float,
                        isMale: Boolean): Flowable<SaveProfileResponse>
}