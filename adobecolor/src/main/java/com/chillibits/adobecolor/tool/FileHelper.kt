package com.chillibits.adobecolor.tool

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.chillibits.adobecolor.R
import java.io.File
import java.io.IOException
import java.net.URLConnection

class FileHelper {
    fun writeToFileAndShare(context: Context, data: ByteArray, ext: String) {
        try {
            // Write to file
            val out = context.openFileOutput("export.$ext", Context.MODE_PRIVATE)
            out.write(data)
            out.close()
            val uri = FileProvider.getUriForFile(context, context.packageName, context.getFileStreamPath("export.$ext"))
            // Share
            val i = Intent(Intent.ACTION_SEND).apply {
                type = URLConnection.guessContentTypeFromName(uri.path)
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(Intent.createChooser(i, context.getString(R.string.export_adobe_palette)))
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }
}