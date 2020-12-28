package com.chillibits.adobecolorsample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chillibits.adobecolor.core.toACOBytes
import com.chillibits.adobecolor.model.AdobeColor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val colors = listOf(
            AdobeColor("ad0d34", getIntFromColor(173, 13, 52)),
            AdobeColor("c77a31", getIntFromColor(199, 122, 49)),
            AdobeColor("d46b54", getIntFromColor(212, 107, 84)),
            AdobeColor("d7525f", getIntFromColor(215, 82, 95)),
            AdobeColor("f10f6b", getIntFromColor(241, 15, 107))
        )
        Log.d("AC", print(colors.toACOBytes()))
    }

    fun print(bytes: ByteArray): String {
        val sb = StringBuilder()
        sb.append("[ ")
        for (b in bytes) sb.append(String.format("0x%02X ", b))
        sb.append("]")
        return sb.toString()
    }

    private fun getIntFromColor(red: Int, green: Int, blue: Int) = Color.rgb(red, green, blue)
}