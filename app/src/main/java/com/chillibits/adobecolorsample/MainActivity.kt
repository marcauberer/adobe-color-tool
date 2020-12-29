package com.chillibits.adobecolorsample

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chillibits.adobecolor.core.AdobeColorExporter
import com.chillibits.adobecolor.tool.toACOBytes
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.printBytesPretty
import com.chillibits.adobecolor.tool.to4Bytes
import com.chillibits.adobecolor.tool.toASEBytes

class MainActivity : AppCompatActivity() {

    // Constants
    private val PERMISSION_REQUEST_CODE = 10001

    // Variables as objects
    private lateinit var colors: List<AdobeColor>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Prepare colors
        colors = listOf(
            AdobeColor("ad0d34", getIntFromColor(173, 13, 52)),
            AdobeColor("c77a31", getIntFromColor(199, 122, 49)),
            AdobeColor("d46b54", getIntFromColor(212, 107, 84)),
            AdobeColor("d7525f", getIntFromColor(215, 82, 95)),
            AdobeColor("f10f6b", getIntFromColor(241, 15, 107))
        )
        Log.d("AC", "ACO: " + colors.toACOBytes().printBytesPretty())
        Log.d("AC", "ASE: " + colors.toASEBytes("Imaginary").printBytesPretty())

        // Set ClickListeners
        findViewById<Button>(R.id.exportColorsACO).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportColorsACO()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        }
        findViewById<Button>(R.id.exportColorsASE).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportColorsASE()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportColorsACO()
        }
    }

    private fun exportColorsACO() {
        AdobeColorExporter(this).exportColorListAsACO(colors)
    }

    private fun exportColorsASE() {
        AdobeColorExporter(this).exportColorListAsASE(colors, "ColorConverter")
    }

    private fun getIntFromColor(red: Int, green: Int, blue: Int) = Color.rgb(red, green, blue)
}