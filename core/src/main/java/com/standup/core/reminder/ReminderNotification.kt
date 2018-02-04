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

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.notifications.NotificationChannelType
import com.kevalpatel2106.common.notifications.addReminderNotificationChannel
import com.kevalpatel2106.utils.getColorCompat
import com.kevalpatel2106.utils.vibrate
import com.standup.core.R
import com.standup.core.di.DaggerCoreComponent
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@Suppress("DEPRECATION")
internal class ReminderNotification {

    @Inject
    internal lateinit var userSettingsManager: UserSettingsManager

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager) {
        this.userSettingsManager = userSettingsManager
    }

    constructor() {
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this)
    }

    private val NOTIFICATION_ID = 3455

    @SuppressLint("VisibleForTests", "NewApi")
    fun notify(context: Context) {

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addReminderNotificationChannel(context, userSettingsManager)

        //Fire notification
        nm.notify(NOTIFICATION_ID, buildNotification(context, userSettingsManager.ledColor)
                .build())

        if (shouldVibrate(context)) {
            context.vibrate(200)
        }

        if (shouldPlaySound(context)) {
            playSound(context, userSettingsManager.getReminderToneUri)
        }
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context,
                                   @ColorInt lightColor: Int): NotificationCompat.Builder {

        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_reminder_notification_small)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.getString(R.string.reminder_notification_message))
                .setAutoCancel(true)
                .setColor(context.getColorCompat(R.color.reminder_notification_color))
                .setWhen(System.currentTimeMillis())
                .setLights(lightColor, 100, 1000)
                .setChannelId(NotificationChannelType.REMINDER_NOTIFICATION_CHANNEL)
                .setStyle(NotificationCompat.BigTextStyle()
                        .setBigContentTitle(context.getString(R.string.reminder_notification_title))
                        .bigText(context.getString(R.string.reminder_notification_message)))
    }

    private fun playSound(context: Context, uri: Uri) {
        Completable.create({

            //If the media stream is in silent
            //Set the volume 4 points below the max volume.
            //We will reset the volume to current value when playback completes.
            val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (currentVolume <= 0) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC,
                        audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC).minus(4), 0)
            }

            val mediaPlayer = MediaPlayer.create(context, uri)
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setOnCompletionListener({ mp ->
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
                mp.release()
            })
            mediaPlayer.start()
        }).subscribeOn(Schedulers.newThread()).subscribe()
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
    private fun shouldPlaySound(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        return if (am.ringerMode == AudioManager.RINGER_MODE_SILENT
                || am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) { /* Phone is in silent mode. */

            //Check which setting user has selected?
            when {
                userSettingsManager.shouldPlayReminderSoundAlways -> true   /* No matter what, play the sound. */
                userSettingsManager.shouldPlayReminderSoundWithHeadphones -> isHeadsetOn(am) /* Play the sound only when head phone connected. */
                else -> false
            }
        } else {
            true
        }
    }

    /**
     * Check if the device should vibrate while playing the notifications or not?
     */
    private fun shouldVibrate(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return userSettingsManager.shouldVibrate && am.ringerMode != AudioManager.RINGER_MODE_SILENT
    }

    /**
     * Cancels any notifications of this type previously shown using
     * [.buildNotification].
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    internal fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

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
    private fun isHeadsetOn(audioManager: AudioManager): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return audioManager.isWiredHeadsetOn || audioManager.isBluetoothA2dpOn
        } else {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            for (i in devices.indices) {
                val device = devices[i]
                if (device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES
                        || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP
                        || device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
                    return true
                }
            }
        }
        return false
    }
}
