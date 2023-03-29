package com.arash.altafi.downloadmanager.sample3

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.arash.altafi.downloadmanager.R
import com.tonyodev.fetch2.*
import kotlinx.android.synthetic.main.activity_sample3.*
import java.io.File

class Sample3 : AppCompatActivity() {

    private lateinit var fetch: Fetch
    private var fetchManager: FetchManager? = null

    private val registerStorageResult = PermissionUtils.register(
        this,
        object : PermissionUtils.PermissionListener {
            override fun observe(permissions: Map<String, Boolean>) {
                if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) {
                    Toast.makeText(
                        this@Sample3,
                        "permission storage is granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@Sample3,
                        "permission storage is not granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)

        val fetchConfiguration: FetchConfiguration =
            FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(10)
//                .setNotificationManager(object: FetchNotificationManager {
//                    override val broadcastReceiver: BroadcastReceiver
//                        get() = TODO("Not yet implemented")
//                    override val notificationManagerAction: String
//                        get() = TODO("Not yet implemented")
//
//                    override fun cancelNotification(notificationId: Int) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun cancelOngoingNotifications() {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun createNotificationChannels(
//                        context: Context,
//                        notificationManager: NotificationManager
//                    ) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getActionPendingIntent(
//                        downloadNotification: DownloadNotification,
//                        actionType: DownloadNotification.ActionType
//                    ): PendingIntent {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getChannelId(notificationId: Int, context: Context): String {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getDownloadNotificationTitle(download: Download): String {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getFetchInstanceForNamespace(namespace: String): Fetch {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getGroupActionPendingIntent(
//                        groupId: Int,
//                        downloadNotifications: List<DownloadNotification>,
//                        actionType: DownloadNotification.ActionType
//                    ): PendingIntent {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getNotificationBuilder(
//                        notificationId: Int,
//                        groupId: Int
//                    ): NotificationCompat.Builder {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getNotificationTimeOutMillis(): Long {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun getSubtitleText(
//                        context: Context,
//                        downloadNotification: DownloadNotification
//                    ): String {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun notify(groupId: Int) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun postDownloadUpdate(download: Download): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun registerBroadcastReceiver() {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun shouldCancelNotification(downloadNotification: DownloadNotification): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun shouldUpdateNotification(downloadNotification: DownloadNotification): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun unregisterBroadcastReceiver() {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun updateGroupSummaryNotification(
//                        groupId: Int,
//                        notificationBuilder: NotificationCompat.Builder,
//                        downloadNotifications: List<DownloadNotification>,
//                        context: Context
//                    ): Boolean {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun updateNotification(
//                        notificationBuilder: NotificationCompat.Builder,
//                        downloadNotification: DownloadNotification,
//                        context: Context
//                    ) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
                .build()
        fetch = Fetch.Impl.getInstance(fetchConfiguration)
        init()
    }

    private fun init() {
        val fileName = "fileName"
        val downloadLink = "https://arashaltafi.ir/arash.jpg"

        btn_download_3.setOnClickListener {
            progressBar.toShow()
            fetchManager = FetchManager(
                this,
                fetch,
                downloadLink = downloadLink,
                parentFolderName = "FolderName",
                fileName = fileName,
                fileSizeKb = 1000L
            ).apply {
                start(
                    result = { success, uri ->
                        if (success) {
                            toast("download success")
                        } else {
                            toast("download error")
                        }

                        image.setImageURI(uri)
                        "success $success, uri $uri".logE("FetchManager-download")
                        progressBar.toGone()
                    },
                    progress = { progress ->
                        "progress $progress".logE("FetchManager-download")
                    }
                )
            }
        }

        btn_get_download_3.setOnClickListener {
            val folder = getDownloadedFolder("FolderName")
            val targetPath =
                folder?.findImage()?.firstOrNull {
                    it.fileName(withSuffix = false) == "fileName".fileName(withSuffix = false)
                }

            targetPath?.let {
                val imageUri = Uri.parse(targetPath)
                image.setImageURI(imageUri)
            }
        }

        btn_save_to_download.setOnClickListener {
            if (!PermissionUtils.isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        PermissionUtils.requestPermission(
                            this, registerStorageResult,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    } else {
                        intentToSetting()
                    }
                } else {
                    PermissionUtils.requestPermission(
                        this, registerStorageResult,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            } else {
                val fileName = "fileName.jpg"

                val cacheDir = getDownloadedFolder("FolderName")
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                val cacheFile = File(cacheDir, fileName.substringBeforeLast("."))
                val downloadFile = File(downloadDir, fileName)

                if (downloadFile.exists()) {
                    toast("file already saved in DIRECTORY_DOWNLOADS")
                    image.setImageURI(downloadFile.toUri())
                } else {
                    if (cacheFile.exists()) {
                        cacheFile.copyTo(downloadFile, true)
                        toast("file added in download")

                        runAfter(500, {
                            val targetPath =
                                cacheFile.findImage().firstOrNull {
                                    it.fileName(withSuffix = false) == "fileName".fileName(
                                        withSuffix = false
                                    )
                                }
                            targetPath?.let {
                                val imageUri = Uri.parse(targetPath)
                                image.setImageURI(imageUri)
                            }
                        })
                    } else {
                        toast("file not exists please download first")
                    }
                }
            }
        }

    }

    private fun intentToSetting() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        fetchManager?.removeListener()
        fetch.close()
    }

}