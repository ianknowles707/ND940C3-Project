package com.udacity

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

fun NotificationManager.sendNotification(id: Long, messageBody: String, applicationContext: Context) {

    val notificationBuilder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

    notify(id.toInt(), notificationBuilder.build())
}