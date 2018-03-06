/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.standup.core.reminder

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import com.evernote.android.job.JobManager
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.annotations.Helper
import com.kevalpatel2106.utils.isDeviceLocked
import com.kevalpatel2106.utils.isScreenOn
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Keval on 05/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Helper(NotificationSchedulerJob::class)
internal object NotificationSchedulerHelper {

    /**
     * Is it okay to run/schedule the [NotificationSchedulerJob]?
     *
     * [NotificationSchedulerJob] should only be running if DND is turned off (i.e.
     * [UserSettingsManager.isCurrentlyInDnd] is false), Sleep is turned off (i.e.
     * [UserSettingsManager.isCurrentlyInSleepMode] is false) and user is logged in (i.e.
     * [UserSessionManager.isUserLoggedIn] is true.).
     *
     * @return True if application is good to schedule/run the [NotificationSchedulerJob] or else false.
     */
    internal fun shouldDisplayNotification(userSessionManager: UserSessionManager,
                                           userSettingsManager: UserSettingsManager): Boolean {
        return userSessionManager.isUserLoggedIn
                && !userSettingsManager.isCurrentlyInSleepMode()
                && !userSettingsManager.isCurrentlyInDnd
    }

    /**
     * Is it okay to run/schedule the [NotificationSchedulerJob]?
     *
     * [NotificationSchedulerJob] should only be running if DND is turned off (i.e.
     * [UserSettingsManager.isCurrentlyInDnd] is false), Sleep is turned off (i.e.
     * [UserSettingsManager.isCurrentlyInSleepMode] is false) and user is logged in (i.e.
     * [UserSessionManager.isUserLoggedIn] is true.).
     *
     * @return True if application is good to schedule/run the [NotificationSchedulerJob] or else false.
     */
    internal fun shouldDisplayPopUp(userSettingsManager: UserSettingsManager,
                                    context: Context): Boolean {
        return userSettingsManager.shouldDisplayPopUp && context.isScreenOn() && !context.isDeviceLocked()
    }

    /**
     * Check if any [NotificationSchedulerJob] is scheduled?
     *
     * @return True if [NotificationSchedulerJob] is scheduled else false.
     * @see JobManager.getAllJobRequestsForTag
     */
    internal fun isReminderScheduled(): Boolean = JobManager.instance()
            .getAllJobRequestsForTag(NotificationSchedulerJob.REMINDER_NOTIFICATION_JOB_TAG)
            .isNotEmpty()


    /**
     * Checks weather the headphones or the bluetooth headphones are connected based on the android
     * version. This does not grantee that sound is being played over headphones or the bluetooth.
     *
     * @return True if either bluetooth A2DP device or headphones are connected else false.
     * @see [AudioManager.isWiredHeadsetOn]
     * @see [AudioManager.isBluetoothA2dpOn]
     * @see [AudioManager.isBluetoothScoOn]
     * @see [SOF](https://stackoverflow.com/a/45726363)
     */
    internal fun isHeadsetOn(audioManager: AudioManager): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return audioManager.isWiredHeadsetOn || audioManager.isBluetoothA2dpOn
        } else {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (i in devices.indices) {
                val device = devices[i]
                if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
                        || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true
                }
            }
        }
        return false
    }


    /**
     * Check if the device should vibrate while playing the notifications or not?
     */
    internal fun shouldVibrate(context: Context, userSettingsManager: UserSettingsManager): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return userSettingsManager.shouldVibrate && am.ringerMode != AudioManager.RINGER_MODE_SILENT
    }


    internal fun playSound(context: Context, uri: Uri): Completable {
        return Completable.create({

            //If the media stream is in silent
            //Set the volume 4 points below the max volume.
            //We will reset the volume to current value when playback completes.
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume <= 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).minus(4), 0)
            }

            try {

                val mediaPlayer = MediaPlayer.create(context, uri)
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setOnCompletionListener({ mp ->
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
                    mp.release()

                    it.onComplete()
                })
                mediaPlayer.start()
            } catch (e: Exception) {
                Timber.e(e)
                it.onError(Throwable(e))
            }
        }).subscribeOn(Schedulers.newThread())
    }

    /**
     * Check weather to play the reminder notification sound or not?
     *
     * - If phone is currently in ringer mode, the sound should always play.
     * - If the phone is in silent or the vibrate more, notification sound will only play if the
     * [UserSettingsManager.shouldPlayReminderSoundAlways] is true or the headphones are connected
     * and [UserSettingsManager.shouldPlayReminderSoundWithHeadphones] is true.
     *
     * @return true if the sound should play or else false.
     * @see isHeadsetOn
     * @see [AudioManager.getRingerMode]
     */
    internal fun shouldPlaySound(context: Context, userSettingsManager: UserSettingsManager): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (am.ringerMode == AudioManager.RINGER_MODE_SILENT
                || am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) { /* Phone is in silent mode. */

            return if (userSettingsManager.shouldPlayReminderSoundInSilent) {

                //Check which setting user has selected?
                when {
                    userSettingsManager.shouldPlayReminderSoundAlways -> true   /* No matter what, play the sound. */
                    userSettingsManager.shouldPlayReminderSoundWithHeadphones -> NotificationSchedulerHelper.isHeadsetOn(am) /* Play the sound only when head phone connected. */
                    else -> false
                }
            } else {
                false
            }
        } else {
            return true
        }
    }
}
