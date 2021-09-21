/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.adobecolor.parser

import android.graphics.Color
import com.chillibits.adobecolor.generator.ASEBinaryGenerator
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.from2Bytes
import com.chillibits.adobecolor.tool.from4Bytes
import com.chillibits.adobecolor.tool.toFloat

/**
 * More information here:
 * - https://www.cyotek.com/blog/reading-adobe-swatch-exchange-ase-files-using-csharp
 * - https://www.cyotek.com/blog/writing-adobe-swatch-exchange-ase-files-using-csharp
 * - http://www.selapa.net/swatches/colors/fileformats.php#adobe_ase
 */
class ASEBinaryParser(data: ByteArray) {

    // Variables as objects
    private var stack = data
    private val colors = HashMap<String, List<AdobeColor>>()
    private val currentGroup = ArrayList<AdobeColor>()
    private var currentName = ""

    fun parse(): Map<String, List<AdobeColor>> {
        // Parse header
        pop4BytesFront() // Signature
        pop4BytesFront() // Version
        val nob = pop4BytesFront() // Number of blocks
        for(i in 0 until nob) {
            when (pop2BytesFront()) {
                -16383 -> parseGroupStartBlock()
                -16382 -> parseGroupEndBlock()
                1 -> parseColorEntryBlock()
            }
        }
        return colors
    }

    private fun parseGroupStartBlock() {
        pop4BytesFront() // Block length = nameLength + 2
        val nameLength = pop2BytesFront()
        currentName = ""
        for (i in 0 until nameLength -1) currentName += String(pop2BytesFrontByteArray(), Charsets.UTF_16)
        pop2BytesFront()
    }

    private fun parseGroupEndBlock() {
        pop4BytesFront() // Block length, always 0
        colors[currentName] = currentGroup.clone() as List<AdobeColor>
        currentGroup.clear()
    }

    private fun parseColorEntryBlock() {
        pop4BytesFront() // Block length
        val nameLength = pop2BytesFront()
        var name = ""
        for (i in 0 until nameLength -1) name += String(pop2BytesFrontByteArray(), Charsets.UTF_16)
        pop2BytesFront()
        var colorModel = ""
        for (i in 0 until 4) colorModel += pop1ByteFront().toChar()
        var color = 0
        when (colorModel) {
             ASEBinaryGenerator.ColorModel.RGB.value -> {
                 val r = pop4BytesFrontToFloat() * 255f
                 val g = pop4BytesFrontToFloat() * 255f
                 val b = pop4BytesFrontToFloat() * 255f
                 color = Color.rgb(r.toInt(), g.toInt(), b.toInt())
             }
            ASEBinaryGenerator.ColorModel.CMYK.value -> {
                val c = pop4BytesFrontToFloat() * 255f
                val m = pop4BytesFrontToFloat() * 255f
                val y = pop4BytesFrontToFloat() * 255f
                val k = pop4BytesFrontToFloat() * 255f
            }
            ASEBinaryGenerator.ColorModel.GRAY.value -> {
                val gray = pop4BytesFrontToFloat() * 255f
            }
        }
        pop2BytesFront() // Color mode

        currentGroup.add(AdobeColor(color, name))
    }

    private fun pop1ByteFront(): Int {
        val value = stack[0].toInt()
        stack = stack.sliceArray(1 until stack.size)
        return value
    }

    private fun pop2BytesFront(): Int {
        val value = from2Bytes(stack[0], stack[1])
        stack = stack.sliceArray(2 until stack.size)
        return value
    }

    private fun pop2BytesFrontByteArray(): ByteArray {
        val value = stack.sliceArray(0 until 2)
        stack = stack.sliceArray(2 until stack.size)
        return value
    }

    private fun pop4BytesFront(): Int {
        val value = from4Bytes(stack[0], stack[1], stack[2], stack[3])
        stack = stack.sliceArray(4 until stack.size)
        return value
    }

    private fun pop4BytesFrontToFloat(): Float {
        val value = arrayOf(stack[0], stack[1], stack[2], stack[3]).toByteArray().toFloat()
        stack = stack.sliceArray(4 until stack.size)
        return value
    }

    fun isASE(data: ByteArray) = data[0].toInt() == 65 && data[1].toInt() == 83 &&
            data[2].toInt() == 69 && data[3].toInt() == 70
}