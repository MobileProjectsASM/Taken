package com.asm.data.sources.local.impl

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import javax.inject.Inject

class MultimediaDeviceSource @Inject constructor(
    @ApplicationContext val context: Context
): MultimediaLocalSource {

    companion object {
        const val TAG = "Multimedia_Device_Source"
    }

    override suspend fun getFileContent(path: String): Result<ByteArray, GeneralError> {
        return try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            context.contentResolver.openInputStream(path.toUri()).use { inStream ->
                inStream?.let {
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (it.read(buffer).also { bytesRead -> length = bytesRead } != -1) {
                        byteArrayOutputStream.write(buffer, 0, length)
                    }
                    byteArrayOutputStream.toByteArray()
                }
            }?.let { Result.Successful(it) } ?: Result.Unsuccessful(GeneralError.ServerError())
        } catch (exception: FileNotFoundException) {
            Log.e(TAG, "get content file error", exception)
            Result.Unsuccessful(GeneralError.ClientError())
        }
    }

    override suspend fun saveImage(
        folderPath: String,
        imageName: String,
        byteArray: ByteArray
    ): Result<String, GeneralError> {
        TODO("Not yet implemented")
    }

    override suspend fun existsImage(path: String): Result<Boolean, GeneralError> {
        TODO("Not yet implemented")
    }

    /*override suspend fun saveImage(folderPath: String, imageName: String, base64: String): String {
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
            throw Exception("Error to saveImage remote source")
        }
    }*/
}