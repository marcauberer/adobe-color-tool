/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.core

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.chillibits.adobecolor.R
import com.chillibits.adobecolor.model.AdobeColor
import java.io.IOException

class AdobeColorExporter(
    private val context: Context
) {

    private enum class EXT(val value: String) {
        ACO("aco"),
        ASE("ase")
    }

    fun exportColorListAsACO(colors: List<AdobeColor>) {
        val bytes = colors.toACOBytes()
        writeToFileAndShare(bytes, "export", EXT.ACO)
    }

    fun exportColorListAsASE(colors: List<AdobeColor>) {
        val bytes = colors.toASEBytes()
        writeToFileAndShare(bytes, "export", EXT.ASE)
    }

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
                context, "com.chillibits.adobecolor",
                context.getFileStreamPath("$filename.${ext.value}")
            )
            // Share
            val i = Intent(Intent.ACTION_SEND).apply {
                type = "application/x-photoshop"
                putExtra(Intent.EXTRA_STREAM, uri)
            }
            context.startActivity(Intent.createChooser(i, context.getString(R.string.export_adobe_palette)))
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }
}