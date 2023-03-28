package com.arash.altafi.downloadmanager.sample3

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@SuppressLint("LogNotTimber")
fun Any.logE(tag: String = "", throwable: Throwable? = null) {
    Log.e(tag, "$this\n", throwable)
}

@SuppressLint("LogNotTimber")
fun Any.logI(tag: String = "", throwable: Throwable? = null) {
    Log.i(tag, "$this\n", throwable)
}

@SuppressLint("LogNotTimber")
fun Any.logD(tag: String = "", throwable: Throwable? = null) {
    Log.d(tag, "$this\n", throwable)
}

@SuppressLint("LogNotTimber")
fun Any.logV(tag: String = "", throwable: Throwable? = null) {
    Log.v(tag, "$this\n", throwable)
}

fun String.createFolder() = try {
    val file = File(this)
    if (file.isDirectory.not()) {
        file.mkdirs()
    }
    file
} catch (e: Exception) {
    Log.d("createFolder", "create file Error: $e")
    null
}

/**
 * files set same name as zip file!
 */
/*suspend*/ fun File.unzip(
    destinationPath: String? = null,
//    progress: ((value: Int) -> Unit)? = null
): Boolean /*= ioCoroutine*/ {
    return try {
        val inputStream = FileInputStream(this.path)
        val zipStream = ZipInputStream(inputStream)
        var zEntry: ZipEntry? = null
        val zipName = this.name.substringBefore(".")

        val destination = destinationPath
            ?: this.parent?.also { it + zipName }
            ?: throw Exception("can't create destination")
        while (zipStream.nextEntry.also { zEntry = it } != null) {
//            val extractFileName = zEntry!!.name
            val extractFileName = zipName + "." + zEntry!!.name.substringAfter(".")

            Log.d(
                "Unzip",
                "Unzipping $extractFileName at $destination"
            )
            if (zEntry!!.isDirectory) {
                extractFileName.createFolder()
            } else {
                val fout = FileOutputStream("$destination/$extractFileName")
                val bufout = BufferedOutputStream(fout)
                val buffer = ByteArray(1024)
                var read = 0
                while (zipStream.read(buffer).also { read = it } != -1) {
                    bufout.write(buffer, 0, read)
                }
                zipStream.closeEntry()
                bufout.close()
                fout.close()
            }
        }
        zipStream.close()
        Log.d("Unzip", "Unzipping complete. path :  $destination")

        this.delete()

        true
    } catch (e: Exception) {
        Log.d("Unzip", "Unzipping failed")
        e.printStackTrace()

        false
    }
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}