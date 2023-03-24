/*
 * Copyright © Marc Auberer 2020-2023. All rights reserved
 */

package com.chillibits.adobecolorsample

import android.content.Context
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.RecyclerView
import com.chillibits.adobecolor.model.AdobeColor
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

class ColorAdapter(private val context: Context): RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    // Variables as objects
    private var colors = emptyList<AdobeColor>()

    fun updateData(colors: List<AdobeColor>) {
        this.colors = colors
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val color = colors[pos]
        holder.itemView.run {
            val itemColor = findViewById<ImageView>(R.id.itemColor)
            itemColor.setTint(color.color)

            val itemName = findViewById<TextView>(R.id.itemColorName)
            itemName.text = color.name

            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV(color.color.red, color.color.green, color.color.blue, hsv)
            val cmyk = getCmykFromRgb(color.color.red, color.color.green, color.color.blue)

            val itemSummary = findViewById<TextView>(R.id.itemColorValues)
            itemSummary.text = String.format(
                    context.getString(R.string.color_summary),
                    color.color.red, color.color.green, color.color.blue,
                    color.name,
                    String.format("%.02f", hsv[0]),
                    String.format("%.02f", hsv[1]),
                    String.format("%.02f", hsv[2]),
                    cmyk[0], cmyk[1], cmyk[2], cmyk[3]
            )
            isSelected = true
        }
    }

    override fun getItemCount() = colors.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun getCmykFromRgb(red: Int, green: Int, blue: Int): Array<Int> {
        if(red == 0 && green == 0 && blue == 0) return arrayOf(0, 0, 0, 100)
        val redPercentage = red / 255.0 * 100.0
        val greenPercentage = green / 255.0 * 100.0
        val bluePercentage = blue / 255.0 * 100.0
        val k = 100.0 - max(max(redPercentage, greenPercentage), bluePercentage)
        val c = (100.0 - redPercentage - k) / (100.0 - k) * 100.0
        val m = (100.0 - greenPercentage - k) / (100.0 - k) * 100.0
        val y = (100.0 - bluePercentage - k) / (100.0 - k) * 100.0
        return arrayOf(c.roundToInt(), m.roundToInt(), y.roundToInt(), k.roundToInt())
    }
}