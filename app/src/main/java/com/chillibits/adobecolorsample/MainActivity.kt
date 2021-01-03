/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.adobecolorsample

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.adobecolor.core.AdobeColorTool
import com.chillibits.adobecolor.core.toACOBytes
import com.chillibits.adobecolor.core.toASEBytes
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.printBytesPretty
import java.util.*

class MainActivity : AppCompatActivity() {

    // Constants
    private val PERMISSON_EXPORT_ACO = 10001
    private val PERMISSON_EXPORT_ASE = 10002
    private val PERMISSON_IMPORT = 10003

    // Variables as objects
    private val colors = ArrayList<AdobeColor>()
    private val random = Random(System.currentTimeMillis())
    private lateinit var colorAdapter: ColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize toolbar
        setSupportActionBar(findViewById(R.id.toolbar))

        // Set ClickListeners
        findViewById<Button>(R.id.exportColorsACO).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportColorsACO()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSON_EXPORT_ACO)
            }
        }
        findViewById<Button>(R.id.exportColorsASE).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                exportColorsASE()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), PERMISSON_EXPORT_ASE)
            }
        }
        findViewById<Button>(R.id.importColors).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                importColors()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), PERMISSON_IMPORT)
            }
        }

        // Initialize RecyclerView
        colorAdapter = ColorAdapter(this)
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = colorAdapter
        }

        // Prepare colors
        generateRandomColors()
        Log.d("AC", "ACO: " + colors.toACOBytes().printBytesPretty())
        Log.d("AC", "ASE: " + colors.toASEBytes("Imaginary").printBytesPretty())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_github -> openGitHub()
            R.id.action_refresh -> generateRandomColors()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                PERMISSON_EXPORT_ACO -> exportColorsACO()
                PERMISSON_EXPORT_ASE -> exportColorsASE()
                PERMISSON_IMPORT -> importColors()
            }
            exportColorsACO()
        }
    }

    private fun exportColorsACO() {
        AdobeColorTool(this).exportColorListAsACO(colors)
    }

    private fun exportColorsASE() {
        AdobeColorTool(this).exportColorListAsASE(colors, "AdobeColorTool")
    }

    private fun importColors() {
        AdobeColorTool(this).importColorList(this, object : AdobeColorTool.AdobeImportListener {
            override fun onComplete(groups: Map<String, List<AdobeColor>>) {
                Log.d("AC", colors.toString())

                colors.clear()
                groups.forEach { colors.addAll(it.value) }
                colorAdapter.updateData(colors)

                Toast.makeText(this@MainActivity, R.string.import_completed, Toast.LENGTH_SHORT).show()
            }

            override fun onError(e: Exception) {
                Toast.makeText(this@MainActivity, R.string.import_failed, Toast.LENGTH_SHORT).show()
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, R.string.import_cancelled, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getIntFromRGB(red: Int, green: Int, blue: Int) = Color.rgb(red, green, blue)

    private fun generateRandomColors() {
        colors.clear()
        for (i in 0..(random.nextInt(5) +2)) { // Min 2, max 7 colors
            val randomColor = getRandomColor()
            val name = "%06X".format(0xFFFFFF and randomColor).toUpperCase(Locale.getDefault())
            colors.add(AdobeColor(randomColor, name))
        }
        colorAdapter.updateData(colors)
    }

    private fun openGitHub() {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://github.com/marcauberer/adobe-color-tool")
        })
    }

    private fun getRandomColor(): Int {
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.rgb(red, green, blue)
    }
}