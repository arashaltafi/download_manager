package com.arash.altafi.downloadmanager.sample3

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.downloadmanager.R
import kotlinx.android.synthetic.main.activity_sample3.*

class Sample3 : AppCompatActivity() {

    private lateinit var permissionUtils: PermissionsUtil
    private lateinit var downloadManager: DownloadManagerUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)

        init()
    }

    private fun init() {
        permissionUtils = PermissionsUtil.getInstance(this, this.activityResultRegistry)
        lifecycle.addObserver(permissionUtils)
        downloadManager = DownloadManagerUtil.newInstance(this, "Demo PDF")
        btn_download_3.setOnClickListener {
            permissionUtils.requestPermission(
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                object : PermissionsUtil.PermissionsListenerCallback {
                    override fun onPermissionGranted() {
                        downloadManager.downloadPdfFile("https://arashaltafi.ir/arash.jpg")
                    }

                    override fun onPermissionDialogCancel() {}
                })
        }
    }

}