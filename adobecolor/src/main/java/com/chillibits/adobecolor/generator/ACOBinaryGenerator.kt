/*
 * Copyright © Marc Auberer 2020-2022. All rights reserved
 */

package com.chillibits.adobecolor.generator

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.chillibits.adobecolor.tool.to2Bytes

/**
 * More information here:
 * - http://www.nomodes.com/aco.html
 * - https://www.adobe.com/devnet-apps/photoshop/fileformatashtml/#50577411_pgfId-1055819
 */
class ACOBinaryGenerator {

    enum class ProtocolVersion(val value: Int) {
        V1(1),
        V2(2)
    }

    /**
     * Header structure
     * Byte 1+2: 0x0001 for version 1 or 0x0002 for version 2
     * Byte 3+4: number of palette colors in binary
     */
    fun getHeader(version: ProtocolVersion, cn: Int): ByteArray {
        var result = version.value.to2Bytes() // Version
        result += cn.to2Bytes() // Number of colors
        return result
    }

    /**
     * Color item structure
     * Byte 0+1: 0x00 for RGB
     * Byte 2+3: Binary of color red value
     * Byte 4+5: Binary of color green value
     * Byte 6+7: Binary of color blue value
     * Byte 8+9: 0 (has other value on other color spaces than RGB)
     */
    fun getColorBytesV1(color: Int): ByteArray {
        var result = 0.to2Bytes() // RGB color space
        result += listOf(color.red.toByte(), color.red.toByte()).toByteArray() // w = red
        result += listOf(color.green.toByte(), color.green.toByte()).toByteArray() // x = green
        result += listOf(color.blue.toByte(), color.blue.toByte()).toByteArray() // y = blue
        result += 0.to2Bytes() // z = 0 (Has a value on other color spaces than RGB)
        return result
    }

    /**
     * Color item structure
     * Byte 0+1: 0 for RGB color space
     * Byte 2+3: Binary of color red value
     * Byte 4+5: Binary of color green value
     * Byte 6+7: Binary of color blue value
     * Byte 8+9: 0 (has other value on other color spaces than RGB)
     * Byte 10+11: 0 (constant)
     * Byte 12+13: name length in binary
     * Following Bytes: UTF-16 decoding of color name
     */
    fun getColorBytesV2(color: Int, name: String): ByteArray {
        var result = 0.to2Bytes() // RGB color space
        result += listOf(color.red.toByte(), color.red.toByte()).toByteArray() // w = red
        result += listOf(color.green.toByte(), color.green.toByte()).toByteArray() // x = green
        result += listOf(color.blue.toByte(), color.blue.toByte()).toByteArray() // y = blue
        result += 0.to2Bytes() // z = 0 (Has a value on other color spaces than RGB)
        result += 0.to2Bytes() // 0 (constant)
        result += (name.length +1).to2Bytes() // Name length
        for (c in name) result += c.code.to2Bytes() // Name
        result += 0.to2Bytes() // 0 (constant)
        return result
    }
}