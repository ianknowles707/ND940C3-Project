package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    //Long type variable to store the download identifier ID
    private var downloadID: Long = 0
    private var filename = ""

    //Define the URL variable for DownloadManager to use
    private lateinit var url: String
    private lateinit var baseURL: String

    //Define boolean to check if download file has been selected
    private var downloadFileSelected = false
    private var downloadStatus = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //call function to create the notification channel
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        //Start download and start animated View
        animated_button.setOnClickListener {
            animated_button.buttonState = ButtonState.Loading
            download(filename)
        }

    }

    //Create the notification channel for download notifications
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableVibration(true)
        notificationChannel.description =
            applicationContext.getString(R.string.notification_channel_description)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    //Call the notification function and include status of download to
    //show in the notification
    fun sendNotification(
        fileName: String,
        errorMessage: Int,
        id: Long,
        status: String
    ) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        val bodyText = applicationContext.getString(R.string.download_complete)
        val bodyMessage = String.format(bodyText, downloadStatus)
        notificationManager.sendNotification(
            fileName,
            status,
            errorMessage,
            id,
            bodyMessage,
            applicationContext
        )
    }


    //On receipt of 'download complete' broadcast, check for download status
    //and error code (if applicable). Log messages added to record the status.
    //Additional info passed to notification function so it can be passed as 'extra'
    //in the Activity Intent
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                //Check status of download
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val returnedError =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                    //Log successful download and set user readable message text
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Log.i("Download", "Success")
                        downloadStatus = resources.getString(R.string.download_success)
                    }
                    //Log failed download and set user readable message text
                    if (status == DownloadManager.STATUS_FAILED) {
                        Log.i("Download", "Failed")
                        Log.i("Download ", returnedError.toString())
                        downloadStatus = resources.getString(R.string.download_failed)
                    }
                    sendNotification(filename, returnedError, downloadID, downloadStatus)

                }

            }
        }
    }

    //Use DownloadManager to get the repository requested
    private fun download(filename: String) {
        if (downloadFileSelected) {
            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        filename
                    )
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager =
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            // enqueue puts the download request in the queue.
            downloadID = downloadManager.enqueue(request)
            Log.i("Download", "Started")
        } else {
            val messageText = resources.getString(R.string.no_option_selected_message)
            Toast.makeText(
                this,
                messageText,
                Toast.LENGTH_SHORT
            ).show()
            animated_button.buttonState = ButtonState.Completed
        }

    }


    //Set the download URL and filename depending on which option was selected
    fun onRadioButtonClicked(view: android.view.View) {
        downloadFileSelected = true
        when (view.id) {
            R.id.radio_button_glide -> {
                baseURL = resources.getString(R.string.glide_base_url)
                filename = resources.getString(R.string.glide_filename)
            }
            R.id.radio_loadapp -> {
                baseURL = resources.getString(R.string.loadapp_base_url)
                filename = resources.getString(R.string.loadapp_filename)
            }
            R.id.radio_retrofit -> {
                baseURL = resources.getString(R.string.retrofit_base_url)
                filename = resources.getString(R.string.retrofil_filename)
            }
        }
        val githubEnd = resources.getString(R.string.github_url_end)
        url = "$baseURL$githubEnd"
    }

}
