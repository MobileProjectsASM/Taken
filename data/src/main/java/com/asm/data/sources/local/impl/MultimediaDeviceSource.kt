package com.asm.data.sources.local.impl

import android.content.Context
import android.os.Environment
import android.util.Log
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import java.io.File

class MultimediaDeviceSource(
    val context: Context
): MultimediaLocalSource {

    companion object {
        const val TAG = "Multimedia_Device_Source"
    }

    override suspend fun saveImage(folderPath: String, imageName: String, base64: String): String {
        try {
            val folders = getDirsFromPath(folderPath)
            var auxFile: File? = null
            return if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.P) {
                if (!isExternalStorageWritable()) throw Exception("External Storage not writable")
                folders.forEach { folder ->
                    auxFile = File(auxFile, folder)
                }
                val imageFile = File(context.getExternalFilesDir(null), imageName)
                imageFile.writeBytes(base64.toByteArray())
                imageFile.path
            } else  {
                folders.forEachIndexed { index, folder ->
                    if (index == 0) {
                        auxFile = context.getDir(folder, Context.MODE_PRIVATE)
                        return@forEachIndexed
                    }
                    auxFile = File(auxFile, folder)
                }
                val imageFile = File(auxFile, imageName)
                imageFile.writeBytes(base64.toByteArray())
                imageFile.path
            }
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }

    override suspend fun existsImage(path: String): Boolean {
        try {
            val imageFile = File(path)
            return imageFile.exists()
        } catch (exception: Exception) {
            Log.e(TAG, exception.stackTraceToString())
            throw exception
        }
    }

    private fun getDirsFromPath(folderPath: String): List<String> = folderPath.split("/")

    private fun isExternalStorageWritable(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}