package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    //Long type variable to store the download identifier ID
    private var downloadID: Long = 0

    //Define the URL variable for DownloadManager to use
    private lateinit var URL: String
    private lateinit var baseURL: String
    private lateinit var filename: String

    //Define boolean to check if download file has been selected
    var downloadFileSelected = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download(filename)
        }
    }

    //On receipt of 'download complete' broadcast, check for download status
    //and error code (if applicable)
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
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        Log.i("Download", "Success")
                    }
                    if (status == DownloadManager.STATUS_FAILED) {
                        //If download failed, get additional information to display
                        val error =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                        Log.i("Download", "Failed")
                        Log.i("Download ", error.toString())
                    }
                }
            }
        }
    }

    //Use DownloadManager to get the repository requested
    private fun download(filename: String) {
        if (downloadFileSelected) {
            val request =
                DownloadManager.Request(Uri.parse(URL))
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
                getSystemService(DOWNLOAD_SERVICE) as DownloadManager            // enqueue puts the download request in the queue.
            // enqueue puts the download request in the queue.
            downloadID = downloadManager.enqueue(request)
            Log.i("Download", "Started")
        } else {
            Toast.makeText(
                this,
                "Please select a file to download",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

    //Set the download URL and filename depending on which option was selected
    fun OnRadioButtonClicked(view: android.view.View) {
        downloadFileSelected = true
        when (view.id) {
            R.id.radio_button_glide -> {
                baseURL = "https://github.com/bumptech/glide"
                filename = "glide.zip"
            }
            R.id.radio_loadapp -> {
                baseURL =
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                filename = "nd940c3.zip"
            }
            R.id.radio_retrofit -> {
                baseURL = "https://github.com/square/retrofit"
                filename = "retrofit.zip"
            }
        }
        val githubEnd = "/archive/refs/heads/master.zip"
        URL = "$baseURL$githubEnd"
    }

}
