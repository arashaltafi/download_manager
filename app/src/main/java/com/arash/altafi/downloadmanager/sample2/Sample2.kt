package com.arash.altafi.downloadmanager.sample2

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.arash.altafi.downloadmanager.R
import com.tonyodev.fetch2.*
import kotlinx.android.synthetic.main.activity_sample2.*
import com.tonyodev.fetch2.FetchConfiguration.Builder
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock
import ir.siaray.downloadmanagerplus.enums.Storage
import java.util.jar.Manifest

class Sample2 : AppCompatActivity() {

    private lateinit var fetch: Fetch
    private val url = "https://arashaltafi.ir/arash.jpg"
    private val file = Storage.DIRECTORY_DOWNLOADS
    val request = Request(url, file)

    private var mPermissionGranted = false
    private val RC_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample2)

        checkPermission()
        val fetchConfiguration: FetchConfiguration = Builder(this).setDownloadConcurrentLimit(3).build()
        fetch = Fetch.Impl.getInstance(fetchConfiguration)
        init()
    }

    private fun init() {
        btn_download_1_sample2.setOnClickListener {
            request.priority = Priority.HIGH
            request.networkType = NetworkType.ALL
//            request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG")

            fetch.enqueue(request, { updatedRequest: Request? ->
                //Request was successfully enqueued for download.
            }) { error: Error? ->
                Toast.makeText(this , error.toString() , Toast.LENGTH_SHORT).show()
            }


            val fetchListener: FetchListener = object : FetchListener {
                override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                    if (request.id == download.id) {
//                        showDownloadInList(download)
                    }
                    Toast.makeText(this@Sample2 , "onQueued" , Toast.LENGTH_SHORT).show()
                }
                override fun onCompleted(download: Download) {
                    Toast.makeText(this@Sample2 , "onCompleted" , Toast.LENGTH_SHORT).show()
                }
                override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                    if (request.id == download.id) {
//                        updateDownload(download, etaInMilliSeconds)
                    }
                    val progress = download.progress
                    Toast.makeText(this@Sample2 , "onProgress" , Toast.LENGTH_SHORT).show()
                }
                override fun onPaused(download: Download) {
                    Toast.makeText(this@Sample2 , "onPaused" , Toast.LENGTH_SHORT).show()
                }
                override fun onResumed(download: Download) {
                    Toast.makeText(this@Sample2 , "onResumed" , Toast.LENGTH_SHORT).show()
                }
                override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                    Toast.makeText(this@Sample2 , "onStarted" , Toast.LENGTH_SHORT).show()
                }
                override fun onWaitingNetwork(download: Download) {
                    Toast.makeText(this@Sample2 , "onWaitingNetwork" , Toast.LENGTH_SHORT).show()
                }
                override fun onAdded(download: Download) {
                    Toast.makeText(this@Sample2 , "onAdded" , Toast.LENGTH_SHORT).show()
                }
                override fun onCancelled(download: Download) {
                    Toast.makeText(this@Sample2 , "onCancelled" , Toast.LENGTH_SHORT).show()
                }
                override fun onRemoved(download: Download) {
                    Toast.makeText(this@Sample2 , "onRemoved" , Toast.LENGTH_SHORT).show()
                }
                override fun onDeleted(download: Download) {
                    Toast.makeText(this@Sample2 , "onDeleted" , Toast.LENGTH_SHORT).show()
                }
                override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {
                    Toast.makeText(this@Sample2 , "onDownloadBlockUpdated" , Toast.LENGTH_SHORT).show()
                }
                override fun onError(download: Download, error: Error, throwable: Throwable?) {
                    Toast.makeText(this@Sample2 , "onError" , Toast.LENGTH_SHORT).show()
                }
            }

            fetch.addListener(fetchListener)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ,
                                           android.Manifest.permission.WRITE_EXTERNAL_STORAGE), RC_PERMISSION)
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }
    }

    override fun onResume() {
        super.onResume()
        fetch.resume(request.id)
    }

    override fun onPause() {
        super.onPause()
        fetch.pause(request.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        fetch.close()
        fetch.remove(request.id)
    }

}