package com.udacity

import android.app.NotificationManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        //Retrieve information passed with Intent
        val filename =
            intent.getStringExtra(applicationContext.getString(R.string.download_request))
        val status = intent.getStringExtra(applicationContext.getString(R.string.download_status))
        val errorMessage =
            intent.getIntExtra(applicationContext.getString(R.string.error_message), -1)
        val id = intent.getLongExtra(applicationContext.getString(R.string.id), 0)

        //Cancel the notification (specific, not all notifications)
        cancelNotification(id)

        //Set values of retrieved information to TextViews
        val showText = determineDownloadDisplayText(filename)
        filename_textview.text = showText
        status_textview.text = status

        //Show status as GREEN if success, or RED if failed
        setStatusColor(status)

        //Show the error code textview if needed (download failed)
        val errorText = setErrorMessage(errorMessage)
        error_textview.text = errorText
        showError(status)

        //
        button_return.setOnClickListener {
            finish()
        }
    }

    private fun cancelNotification(id: Long) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotification(id)
    }

    private fun determineDownloadDisplayText(filename: String?): String {
        var displayText = ""
        when (filename) {
            resources.getString(R.string.glide_filename) -> displayText =
                applicationContext.getString(R.string.glide_description)
            resources.getString(R.string.loadapp_filename) -> displayText =
                applicationContext.getString(R.string.loadapp_description)
            resources.getString(R.string.retrofil_filename) -> displayText =
                applicationContext.getString(R.string.retrofit_description)
        }
        return displayText
    }

    private fun showError(status: String?) {
        if (status == applicationContext.getString(R.string.download_success)) {
            error_label_textview.visibility = View.INVISIBLE
            error_textview.visibility = View.INVISIBLE
        } else {
            error_label_textview.visibility = View.VISIBLE
            error_textview.visibility = View.VISIBLE
        }
    }

    //Translate error code into user readable text
    private fun setErrorMessage(errorMessage: Int): String {
        var errorMessageReadable = ""
        when (errorMessage) {
            404 -> errorMessageReadable = "File not found"
            1008 -> errorMessageReadable = "Unable to resume download"
            1007 -> errorMessageReadable = "External storage not found"
            1006 -> errorMessageReadable = "Insufficient space"
            1001 -> errorMessageReadable = "Error saving file"
            1000 -> errorMessageReadable = "Unknown error"
        }
        return errorMessageReadable
    }

    private fun setStatusColor(status: String?) {
        if (status == applicationContext.getString(R.string.download_success)) {
            status_textview.setTextColor(Color.GREEN)
        } else {
            status_textview.setTextColor(Color.RED)
        }
    }

}
