package com.arash.altafi.downloadmanager.sample3

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.arash.altafi.downloadmanager.sample3.FetchManager.Companion.DOWNLOAD_FOLDER_NAME
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.DownloadBlock
import ir.siaray.downloadmanagerplus.enums.Storage
import java.io.File
import java.io.FileFilter

class FetchManager(
    private val context: Context,
    private val fetch: Fetch,
    private val downloadLink: String,
    private val parentFolderName: String,
    private val fileName: String,
    private val fileSizeKb: Long? = null
) {

    internal companion object {
        const val DOWNLOAD_FOLDER_NAME = "/download/"
        const val TEMP_DOWNLOAD_FOLDER_NAME = "/download/temp/"
    }

    private lateinit var request: Request
    private lateinit var resultFunc: (status: Boolean, uri: Uri?) -> Unit
    private lateinit var progressFunc: (value: Int) -> Unit


    fun start(
        result: (status: Boolean, uri: Uri?) -> Unit,
        progress: (value: Int) -> Unit
    ) {
        resultFunc = result
        progressFunc = progress

        setupTempPath()?.let {
//            val name = Storage.DIRECTORY_DOWNLOADS
            val name = "$it/$fileName"

            fetch.getDownloadsWithStatus(Status.COMPLETED) { downloads ->
                downloads.firstOrNull { it.file == name }?.let { d ->
                    if (File(d.file).exists())
                        completeDownload(name)
                    else {
                        request = Request(downloadLink, name).apply {
                            priority = Priority.HIGH
                            networkType = NetworkType.ALL
                        }
                        enqueue()
                    }
                } ?: run {
                    fetch.getDownloadsWithStatus(Status.DOWNLOADING) { downloads ->
                        downloads.firstOrNull { it.file == name }?.let { d ->
                            request = d.request

                            fetch.addListener(listener)
                        } ?: run {
                            request = Request(downloadLink, name).apply {
                                priority = Priority.HIGH
                                networkType = NetworkType.ALL
                            }

                            enqueue()
                        }
                    }
                }
            }
        } ?: kotlin.run {
            result.invoke(false, null)
        }

    }

    fun cancel() {
        try {
            if (::request.isInitialized)
                fetch.cancel(request.id,
                    func = { download ->
                        File(download.fileUri.path ?: "").apply {
                            if (exists()) {
                                delete()
                                "func file deleted".logE("FetchManager")
                            }
                        }

                        "func download:$download".logE("FetchManager")
                    }, func2 = {
                        "func2 error: $it".logE("FetchManager")
                    }
                )
        } catch (e: Exception) {
            "cancel download error:".logE("FetchManager")
            e.printStackTrace()
        }
    }

    fun removeListener() {
        fetch.removeListener(listener)
    }

    private fun setupTempPath(): String? =
        (context.cacheDir.path + TEMP_DOWNLOAD_FOLDER_NAME).createFolder()?.path

    private fun setupPath(): String? =
        (context.cacheDir.path + DOWNLOAD_FOLDER_NAME + parentFolderName).createFolder()?.path

    private fun enqueue() {
        fetch.enqueue(
            request,
            {
                fetch.addListener(listener)

                fetch.resume(request.id)
            },
            {
                "enqueue error-> $it".logE("FetchManager")

                onResult(false, null)
            }
        )
    }

    private fun completeDownload(file: String) {
        setupPath()?.let {
//            val path = Storage.DIRECTORY_DOWNLOADS
            val path = setupPath() + "/$fileName"
            val f = File(file).copyTo(File(path), true)
            File(file).delete()

            if (path.endsWith(".zip"))
                path.takeIf { f.unzip() }?.let {
                    onResult(true, File(file).toUri())
                } ?: run { onResult(false, null) }
            else
                onResult(true, File(file).toUri())

            fetch.removeListener(listener)
        }
    }

    val listener =
        object : FetchListener {

            override fun onAdded(download: Download) {
                "onAdded: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onCancelled(download: Download) {
                "onCancelled: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
                fetch.remove(request.id)
            }

            override fun onCompleted(download: Download) {
                "onCompleted: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return

                completeDownload(download.file)

//                fetch.close()
            }

            override fun onDeleted(download: Download) {
                "onDeleted: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onDownloadBlockUpdated(
                download: Download,
                downloadBlock: DownloadBlock,
                totalBlocks: Int
            ) {
                "onDownloadBlockUpdated: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                "onError download: ${download.url}, throw: $throwable".logE("FetchManager")
                if (download.url != downloadLink)
                    return

                fetch.remove(request.id)

                onResult(false, null)
            }

            override fun onPaused(download: Download) {
                "onPaused: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onProgress(
                download: Download,
                etaInMilliSeconds: Long,
                downloadedBytesPerSecond: Long
            ) {
                if (download.url != downloadLink)
                    return

                var progress = download.progress
                "onProgress: $progress".logE("FetchManager")
                if (progress == -1 && fileSizeKb != null && fileSizeKb > 0) {
                    progress = ((download.downloaded / 10) / fileSizeKb).toInt() // percent
                    "onProgress-calculated: $progress".logE("FetchManager")
                }

                progressFunc.invoke(progress)
            }

            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                "onQueued: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onRemoved(download: Download) {
                "onRemoved: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onResumed(download: Download) {
                "onResumed: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return
            }

            override fun onStarted(
                download: Download,
                downloadBlocks: List<DownloadBlock>,
                totalBlocks: Int
            ) {
                "onStarted: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return

                progressFunc.invoke(download.progress)
            }

            override fun onWaitingNetwork(download: Download) {
                "onWaitingNetwork: $download".logE("FetchManager")
                if (download.url != downloadLink)
                    return

                //TODO("Not yet implemented")
            }

        }

    private fun onResult(status: Boolean, uri: Uri?) {
        resultFunc.invoke(status, uri)
        fetch.removeListener(listener)
    }

}

fun Fetch.getDownloadedList(func: (List<Uri>) -> Unit) {
    if (this.isClosed) {
        "fetch is Closed!".logE("FetchManager")
        return func.invoke(emptyList())
    }

    this.getDownloadsWithStatus(Status.COMPLETED) { downloadList ->
        func.invoke(downloadList.map { it.fileUri })
    }
}

/*fun Fetch.getDownload(fileName: String, func: (Uri) -> Unit) {
    getDownloadedList { downloadList ->
        func.invoke(
            downloadList.first { it.lastPathSegment == fileName.md5() }
        )
    }
}

fun Context.getDownloadedFolders(): Array<File> = try {
    val file = File(ContextCompat.getCodeCacheDir(this), DOWNLOAD_FOLDER_NAME)

    takeIf { file.exists() }?.let { file.listFiles() } ?: emptyArray()
} catch (e: Exception) {
    "getDownloadedFolders: $e".logE("FetchManager")

    emptyArray()
}*/

fun Context.getDownloadedFolder(name: String): File? = try {
    val file = File(this.cacheDir.path, DOWNLOAD_FOLDER_NAME)

    takeIf { file.exists() }?.let { file.listFiles(FileFilter { it.name == name }) }?.get(0)
} catch (e: Exception) {
    "getDownloadedFolder(name: $name): $e".logE("FetchManager")

    null
}

fun File.findFile(fullName: String, withSuffix: Boolean = true): String? =
    try {
        this.listFiles { _, fileName ->
            if (withSuffix)
                fileName == fullName
            else
                fileName.substringBefore(".") == fullName.substringBefore(".")
        }?.getOrNull(0)?.path
    } catch (e: Exception) {
        "error $e".logE("File.findFile by name ($fullName)")

        null
    }

fun File.findBySuffix(vararg suffix: String): List<String> =
    try {
        this.listFiles { _, fileName ->
            suffix.contains(fileName.split(".").last())
        }?.map { it.path } ?: emptyList()
    } catch (e: Exception) {
        "error $e".logE("File.findFile by suffix ($suffix)")

        emptyList()
    }

fun File.findImage(vararg format: String = arrayOf("jpg", "jpeg", "png", "gif", "webp")) =
    findBySuffix(*format)

fun File.findPdf(vararg format: String = arrayOf("pdf")) = findBySuffix(*format)

fun File.findEpub(vararg format: String = arrayOf("epub")) = findBySuffix(*format)


fun File.findAudio(vararg format: String = arrayOf("mp3")) = findBySuffix(*format)