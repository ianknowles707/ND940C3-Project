package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

fun NotificationManager.sendNotification(
    fileName: String,
    status: String,
    errorMessage: Int,
    id: Long,
    messageBody: String,
    applicationContext: Context
) {
    //Create the Intent to open Details Activity from notification
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    //Add extra information to the Intent request, for display on the Details Activity
    contentIntent.putExtra(applicationContext.getString(R.string.download_request), fileName)
    contentIntent.putExtra(applicationContext.getString(R.string.error_message), errorMessage)
    contentIntent.putExtra(applicationContext.getString(R.string.download_status), status)
    val detailPendingIntent: PendingIntent = PendingIntent.getActivity(
        applicationContext,
        id.toInt(),
        contentIntent,
        0
    )
    val notificationBuilder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .addAction(
            R.drawable.download_image,
            applicationContext.getString(R.string.notification_button),
            detailPendingIntent
        )

    notify(id.toInt(), notificationBuilder.build())
}