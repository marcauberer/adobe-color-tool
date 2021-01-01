/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.adobecolor.parser

import android.graphics.Color
import android.util.Log
import com.chillibits.adobecolor.generator.ACOBinaryGenerator
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.from2Bytes
import java.nio.charset.Charset

/**
 * More information here:
 * - http://www.nomodes.com/aco.html
 * - https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577411_pgfId-1055819
 */
class ACOBinaryParser(data: ByteArray) {

    // Variables as objects
    private var stack = data
    private val colors = ArrayList<AdobeColor>()

    enum class ColorSpace(val value: Int) {
        RGB(0),
        HSB(1),
        CMYK(2),
        Lab(7),
        Grayscale(8)
    }

    fun parse(): List<AdobeColor> {
        // Read first header
        val version = pop2BytesFront()
        var noc = pop2BytesFront()
        if (version == ACOBinaryGenerator.ProtocolVersion.V1.value) {
            // Parse more V1 color blocks
            for (i in 0 until noc) parseV1ColorBlock()
            if (stack.isNotEmpty()) {
                // V2 is also attached
                colors.clear()
                // Parse V2 header
                pop2BytesFront()
                noc = pop2BytesFront()
                // Parse V2 color blocks
                for (i in 0 until noc) parseV2ColorBlock()
            }
        } else {
            // Contains only V2 blocks
            for (i in 0 until noc) parseV2ColorBlock()
        }
        return colors
    }

    private fun parseV1ColorBlock() {
        val colorSpace = pop2BytesFront()
        val w = pop2BytesFront()
        val x = pop2BytesFront()
        val y = pop2BytesFront()
        val z = pop2BytesFront()
        val color = when (colorSpace) {
            ColorSpace.RGB.value -> Color.rgb(w / 256, x / 256, y / 256)
            else -> 0
        }
        colors.add(AdobeColor(color))
    }

    private fun parseV2ColorBlock() {
        val colorSpace = pop2BytesFront()
        val w = pop2BytesFront()
        val x = pop2BytesFront()
        val y = pop2BytesFront()
        val z = pop2BytesFront()
        val color = when (colorSpace) {
            ColorSpace.RGB.value -> Color.rgb(w / 256, x / 256, y / 256)
            else -> 0
        }
        pop2BytesFront() // Constant 0
        val nameLength = pop2BytesFront()
        var name = ""
        for (i in 0 until nameLength) name += pop2BytesFront().toChar()
        colors.add(AdobeColor(color, name))
    }

    private fun pop2BytesFront(): Int {
        val value = from2Bytes(stack[0], stack[1])
        stack = stack.sliceArray(2 until stack.size)
        return value
    }

    fun isACO(data: ByteArray) = data[0].toInt() == 0 && (data[1].toInt() == ACOBinaryGenerator.ProtocolVersion.V1.value ||
            data[1].toInt() == ACOBinaryGenerator.ProtocolVersion.V2.value)
}