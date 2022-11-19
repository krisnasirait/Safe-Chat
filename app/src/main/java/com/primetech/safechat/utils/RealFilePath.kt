package com.primetech.safechat.utils


import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import com.primetech.safechat.App
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream



class RealFilePath() {
    companion object {
        fun getFilePath(uri: Uri, context: Context): String? {
            val returnCursor = context.contentResolver.query(uri, null, null, null, null)

            val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            val size = returnCursor.getLong(sizeIndex).toString()
            val file = File(context.filesDir, name)
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(file)
                var read = 0
                val maxBufferSize = 1 * 1024 * 1024
                val bytesAvailable: Int = inputStream!!.available()

                val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                val buffers = ByteArray(bufferSize)
                while (inputStream.read(buffers).also { read = it } != -1) {
                    outputStream.write(buffers, 0, read)
                }
                inputStream.close()
                outputStream.close()
            } catch (e: Exception) {
                Toast.makeText(App.getAppContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            return file.path
        }
    }
}