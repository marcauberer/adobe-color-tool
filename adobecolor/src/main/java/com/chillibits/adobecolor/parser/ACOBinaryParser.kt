/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.parser

import android.graphics.Color
import android.util.Log
import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.from2Bytes

/**
 * More information here:
 * - http://www.nomodes.com/aco.html
 * - https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577411_pgfId-1055819
 */
class ACOBinaryParser {

    enum class ProtocolVersion(val value: Int) {
        V1(1),
        V2(2)
    }

    enum class ColorSpace(val value: Int) {
        RGB(0),
        HSB(1),
        CMYK(2),
        Lab(7),
        Grayscale(8)
    }

    fun parse(data: ByteArray): List<AdobeColor> {
        val colors = ArrayList<AdobeColor>()
        // Read first header
        val version = from2Bytes(data[0], data[1])
        val noc = from2Bytes(data[2], data[3])
        if (version == ProtocolVersion.V1.value) {
            // Parse more V1 blocks
            for (i in 0 until noc) colors.add(AdobeColor(parseV1Block(data, i)))
        } else {
            // Parse more V2 blocks

        }

        return emptyList()
    }

    private fun parseV1Block(data: ByteArray, i: Int): Int {
        val colorSpace = from2Bytes(data[i * 10 + 4], data[i * 10 + 5])
        val w = from2Bytes(data[i * 10 + 6], data[i * 10 + 7])
        val x = from2Bytes(data[i * 10 + 8], data[i * 10 + 9])
        val y = from2Bytes(data[i * 10 + 10], data[i * 10 + 11])
        val z = from2Bytes(data[i * 10 + 12], data[i * 10 + 13])
        // Calculate int from w, x, y, z
        return when (colorSpace) {
            ColorSpace.RGB.value -> Color.rgb(w, x, y)
            // TODO: Define more color spaces
            else -> 0
        }
    }

    private fun parseV2Block() {

    }

    fun isACO(data: ByteArray) = data[0].toInt() == 0 && (data[1].toInt() == ProtocolVersion.V1.value ||
            data[1].toInt() == ProtocolVersion.V2.value)
}