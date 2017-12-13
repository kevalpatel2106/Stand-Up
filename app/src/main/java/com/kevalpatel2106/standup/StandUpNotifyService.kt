package com.kevalpatel2106.standup

import com.kevalpatel2106.standup.engine.StandUpNotifier
import com.kevalpatel2106.standup.notification.TestStandUpNowNotification

/**
 * Created by Kevalpatel2106 on 13-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class StandUpNotifyService : StandUpNotifier() {

    override fun notifyUserToStandUp() {
        TestStandUpNowNotification.notify(this)
    }

}