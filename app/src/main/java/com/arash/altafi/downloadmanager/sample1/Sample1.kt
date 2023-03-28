package com.arash.altafi.downloadmanager.sample1

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.downloadmanager.R
import kotlinx.android.synthetic.main.activity_sample1.*
import android.R.attr.visibility
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import ir.siaray.downloadmanagerplus.classes.Downloader
import ir.siaray.downloadmanagerplus.enums.DownloadReason
import ir.siaray.downloadmanagerplus.enums.Storage
import ir.siaray.downloadmanagerplus.interfaces.DownloadListener
import ir.siaray.downloadmanagerplus.utils.Utils

class Sample1 : AppCompatActivity() {

    private val description: String = "This is a description For Test"
    private var mPermissionGranted = false
    private val RC_PERMISSION = 10
    private lateinit var downloader: Downloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample1)

        downloader = Downloader.getInstance(this)
        checkPermission()
        init()
    }

    private fun init() {
        btn_download_1_sample1.setOnClickListener {

            downloader
                .setUrl("https://arashaltafi.ir/Social_Media/story-00.jpg")
                .setListener(object : DownloadListener {
                    override fun onComplete(totalBytes: Int) {
                        Toast.makeText(this@Sample1, "onComplete", Toast.LENGTH_SHORT).show()
                        pr_sample1.visibility = View.GONE
                    }

                    override fun onPause(
                        percent: Int,
                        reason: DownloadReason?,
                        totalBytes: Int,
                        downloadedBytes: Int
                    ) {
                        Toast.makeText(this@Sample1, "onPause", Toast.LENGTH_SHORT).show()
                        pr_sample1.visibility = View.GONE
                    }

                    override fun onPending(percent: Int, totalBytes: Int, downloadedBytes: Int) {
                        Toast.makeText(this@Sample1, "onPending", Toast.LENGTH_SHORT).show()
                        downloader.showProgress()
                        pr_sample1.visibility = View.VISIBLE
                    }

                    override fun onFail(
                        percent: Int,
                        reason: DownloadReason?,
                        totalBytes: Int,
                        downloadedBytes: Int
                    ) {
                        Toast.makeText(this@Sample1, "onFail", Toast.LENGTH_SHORT).show()
                        pr_sample1.visibility = View.GONE
                    }

                    override fun onCancel(totalBytes: Int, downloadedBytes: Int) {
                        Toast.makeText(this@Sample1, "onCancel", Toast.LENGTH_SHORT).show()
                        pr_sample1.visibility = View.GONE
                    }

                    override fun onRunning(
                        percent: Int,
                        totalBytes: Int,
                        downloadedBytes: Int,
                        downloadSpeed: Float
                    ) {
//                        Toast.makeText(this@Sample1 , "onRunning" , Toast.LENGTH_SHORT).show()
                    }

                })
//                .setToken("123") // token
                .setAllowedOverRoaming(true) // Allowed in Roaming Network
                .setAllowedOverMetered(true) //Api 16 and higher => Allowed to be exceeded
                .setVisibleInDownloadsUi(false)
                .setDestinationDir(Storage.DIRECTORY_DOWNLOADS, "title")
                .setNotificationTitle("test notif title")
                .setDescription(description)
                .setNotificationVisibility(1)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
//                .setKeptAllDownload(allDownloadKept)
            downloader.start()
            downloader.showProgress()

//            downloader.cancel(token)
//            downloader.getStatus(token)
//            downloader.deleteFile(token, deleteListener);


        }
    }

    override fun onPause() {
        super.onPause()
        downloader.pause()
//        Downloader.pause(context, token)
    }

    override fun onResume() {
        super.onResume()
        downloader.resume()
//        Downloader.resume(context, token)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RC_PERMISSION
                )
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }
    }

}