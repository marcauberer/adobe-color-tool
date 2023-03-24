/*
 * Copyright © Marc Auberer 2020-2023. All rights reserved
 */

package com.chillibits.adobecolor.core

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.chillibits.adobecolor.R
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.printBytesPretty
import com.github.florent37.inlineactivityresult.InlineActivityResult.startForResult
import com.github.florent37.inlineactivityresult.Result
import com.github.florent37.inlineactivityresult.callbacks.ActivityResultListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class AdobeColorTool(
    private val context: Context
) {

    private enum class EXT(val value: String) {
        ACO("aco"),
        ASE("ase")
    }

    interface AdobeImportListener {
        fun onComplete(groups: Map<String, List<AdobeColor>>)
        fun onError(e: Exception)
        fun onCancel() {}
    }

    // ------------------------------------------- Export ------------------------------------------

    /**
     * Exports a list of colors as ACO file without setting a custom palette name.
     * The resulting file will have the name export.aco
     *
     * @param colors List of colors to be exported
     */
    fun exportColorListAsACO(colors: List<AdobeColor>) {
        val bytes = colors.toACOBytes()
        writeToFileAndShare(bytes, "export", EXT.ACO)
    }

    /**
     * Exports a list of colors as ACO file with a custom palette name
     *
     * @param colors List of colors to be exported
     * @param paletteName Name that will be set as file name
     */
    fun exportColorListAsACO(colors: List<AdobeColor>, paletteName: String) {
        val bytes = colors.toACOBytes()
        writeToFileAndShare(bytes, paletteName, EXT.ACO)
    }

    /**
     * Exports a list of colors as ASE file without setting a custom palette name.
     * The resulting file will have the name export.ase
     *
     * @param colors List of colors to be exported
     */
    fun exportColorListAsASE(colors: List<AdobeColor>) {
        val bytes = colors.toASEBytes()
        writeToFileAndShare(bytes, "export", EXT.ASE)
    }

    /**
     * Exports a list of colors as ASE file with a custom palette name
     *
     * @param colors List of colors to be exported
     * @param paletteName Name that will be set as palette and file name
     */
    fun exportColorListAsASE(colors: List<AdobeColor>, paletteName: String) {
        val bytes = colors.toASEBytes(paletteName)
        writeToFileAndShare(bytes, paletteName, EXT.ASE)
    }

    private fun writeToFileAndShare(data: ByteArray, filename: String, ext: EXT) {
        try {
            // Write to file
            val out = context.openFileOutput("$filename.${ext.value}", Context.MODE_PRIVATE)
            out.write(data)
            out.close()
            val uri = FileProvider.getUriForFile(
                context, context.packageName,
                context.getFileStreamPath("$filename.${ext.value}")
            )
            // Share
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "application/x-photoshop"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(
                Intent.createChooser(i, context.getString(R.string.export_adobe_palette))
            )
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    // ------------------------------------------- Import ------------------------------------------

    /**
     * Function to open up a file picker and import colors from the selected file.
     * Valid file extensions are: .aco, .ase
     */
    fun importColorList(activity: AppCompatActivity, listener: AdobeImportListener) {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/octet-stream"
        }
        startForResult(activity, i, object : ActivityResultListener {
            override fun onSuccess(result: Result?) {
                result?.data?.data?.let {
                    activity.contentResolver.openInputStream(it)?.let { data ->
                        try {
                            val bytes = getBytes(data)
                            bytes.toColorList()?.let { list ->
                                listener.onComplete(list)
                                return
                            }
                            listener.onError(Exception("Wrong file type"))
                            return
                        } catch (e: Exception) { listener.onError(e) }
                    }
                }
                listener.onCancel()
            }

            override fun onFailed(result: Result?) {
                listener.onCancel()
            }
        })
    }

    private fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }
}