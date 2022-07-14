package com.arash.altafi.downloadmanager.sample3

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.util.*
import kotlin.concurrent.timerTask

class DownloadManagerUtil(private val context: Context, private val fileName: String) {

    fun downloadPdfFile(url: String) {
        val path: String = Environment.DIRECTORY_DOWNLOADS
        try {
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.apply {
                setTitle(fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(path, fileName)
            }
            val downloadID = downloadManager.enqueue(request)
            Timer().schedule(timerTask {
                val query: DownloadManager.Query = DownloadManager.Query()
                query.setFilterByStatus(
                    DownloadManager.STATUS_FAILED or DownloadManager.STATUS_PAUSED or DownloadManager.STATUS_SUCCESSFUL or
                            DownloadManager.STATUS_RUNNING or DownloadManager.STATUS_PENDING
                )
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    when (cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            //do something
                        }
                        else -> downloadManager.remove(downloadID)
                    }
                }
            }, 5000)
        } catch (e: Exception) {
        }
    }

    companion object {
        fun newInstance(context: Context, fileName: String): DownloadManagerUtil {
            return DownloadManagerUtil(context, fileName)
        }
    }
}