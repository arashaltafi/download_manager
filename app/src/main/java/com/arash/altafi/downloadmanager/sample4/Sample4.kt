package com.arash.altafi.downloadmanager.sample4

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.arash.altafi.downloadmanager.R
import com.arash.altafi.downloadmanager.sample3.toast
import kotlinx.android.synthetic.main.activity_sample4.*

class Sample4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample4)

        toast("Sample DownloadManager Basic")
        init()
    }

    private fun init() {
        btnDownload4.setOnClickListener {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse("https://arashaltafi.ir/arash.jpg")
            val request = DownloadManager.Request(uri)
            request.setTitle("test Title")
            request.setDescription("test Description")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/TestDownload/${System.currentTimeMillis()}.jpg" //need to set suffix
            )
            downloadManager.enqueue(request)
        }
    }

}