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

package com.kevalpatel2106.standup.reminder.notification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.fcm.NotificationChannelType
import com.kevalpatel2106.standup.fcm.addReminderNotificationChannel
import com.kevalpatel2106.standup.misc.UserSettingsManager
import com.kevalpatel2106.standup.reminder.di.DaggerReminderComponent
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@Suppress("DEPRECATION")
class ReminderNotification {

    @Inject lateinit var userSettingsManager: UserSettingsManager

    @VisibleForTesting
    constructor(userSettingsManager: UserSettingsManager) {
        this.userSettingsManager = userSettingsManager
    }

    constructor() {
        DaggerReminderComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this)
    }

    private val NOTIFICATION_ID = 3455

    @SuppressLint("VisibleForTests", "NewApi")
    internal fun notify(context: Context) {
        val shouldVibrate = shouldVibrate(context)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.addReminderNotificationChannel(context, userSettingsManager)

        //Fire notification
        nm.notify(NOTIFICATION_ID, buildNotification(context, shouldVibrate, userSettingsManager.ledColor)
                .build())

        if (shouldPlaySound(context)) {
            playSound(context, userSettingsManager.getReminderToneUri)
        }
    }

    @VisibleForTesting
    internal fun buildNotification(context: Context,
                                   vibrate: Boolean,
                                   @ColorInt lightColor: Int): NotificationCompat.Builder {

        return NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker(context.getString(R.string.reminder_notification_message))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
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

    private fun shouldPlaySound(context: Context): Boolean {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        return if (am.ringerMode == AudioManager.RINGER_MODE_SILENT
                || am.ringerMode == AudioManager.RINGER_MODE_VIBRATE) { /* Phone is in silent mode. */

            //Check which setting user has selected?
            when {
                userSettingsManager.shouldPlayReminderSoundAlways -> true
                userSettingsManager.shouldPlayReminderSoundWithHeadphones -> am.isWiredHeadsetOn
                else -> false
            }
        } else {
            true
        }
    }

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
}
