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

class MainActivity : AppCompatActivity() {

    // Constants
    private val PERMISSION_REQUEST_CODE = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.exportColors).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportColors()
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
            exportColors()
        }
    }

    private fun exportColors() {
        val colors = listOf(
            AdobeColor("ad0d34", getIntFromColor(173, 13, 52)),
            AdobeColor("c77a31", getIntFromColor(199, 122, 49)),
            AdobeColor("d46b54", getIntFromColor(212, 107, 84)),
            AdobeColor("d7525f", getIntFromColor(215, 82, 95)),
            AdobeColor("f10f6b", getIntFromColor(241, 15, 107))
        )
        Log.d("AC", print(colors.toACOBytes()))

        AdobeColorExporter(this).exportColorListAsACO(colors)
    }

    private fun print(bytes: ByteArray): String {
        val sb = StringBuilder()
        sb.append("[ ")
        for (b in bytes) sb.append(String.format("0x%02X ", b))
        sb.append("]")
        return sb.toString()
    }

    private fun getIntFromColor(red: Int, green: Int, blue: Int) = Color.rgb(red, green, blue)
}