package com.arash.altafi.downloadmanager.sample3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arash.altafi.downloadmanager.R
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import kotlinx.android.synthetic.main.activity_sample3.*

class Sample3 : AppCompatActivity() {

    private lateinit var fetch: Fetch
    private var fetchManager: FetchManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)

        val fetchConfiguration: FetchConfiguration =
            FetchConfiguration.Builder(this).setDownloadConcurrentLimit(10).build()
        fetch = Fetch.Impl.getInstance(fetchConfiguration)
        init()
    }

    private fun init() {
        val fileName = "fileName.jpg"
        val downloadLink = "https://arashaltafi.ir/arash.jpg"

        btn_download_3.setOnClickListener {
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
                    },
                    progress = { progress ->
                        "progress $progress".logE("FetchManager-download")
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fetchManager?.removeListener()
    }

}