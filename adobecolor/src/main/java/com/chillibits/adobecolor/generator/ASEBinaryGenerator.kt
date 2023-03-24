/*
 * Copyright © Marc Auberer 2020-2023. All rights reserved
 */

package com.chillibits.adobecolor.generator

import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.chillibits.adobecolor.tool.to2Bytes
import com.chillibits.adobecolor.tool.to4Bytes

/**
 * More information here:
 * - https://www.cyotek.com/blog/reading-adobe-swatch-exchange-ase-files-using-csharp
 * - https://www.cyotek.com/blog/writing-adobe-swatch-exchange-ase-files-using-csharp
 * - http://www.selapa.net/swatches/colors/fileformats.php#adobe_ase
 */
class ASEBinaryGenerator {

    enum class BlockType(val value: Int) {
        GROUP_START(0xC001),
        GROUP_END(0xC002),
        COLOR(0x0001)
    }

    enum class ColorModel(val value: String) {
        CMYK("CMYK"),
        RGB("RGB "),
        GRAY("Gray")
    }

    enum class ColorType(val value: Int) {
        GLOBAL(0),
        SPOT(1),
        NORMAL(2)
    }

    /**
     * Header structure
     * Byte 1-4: Signature: ASEF (Adobe Swatch Exchange Format)
     * Byte 5+6: Major protocol version
     * Byte 7+8: Minor protocol version
     * Byte 9-12: Number of blocks
     */
    fun getHeader(nob: Int): ByteArray {
        var result = "ASEF".toByteArray(Charsets.UTF_8) // Signature
        result += 1.to2Bytes() // Major version
        result += 0.to2Bytes() // Minor version
        result += nob.to4Bytes() // Number of blocks
        return result
    }

    /**
     * Block structure:
     * Byte 1+2: Block type (Group start: 0xC002)
     * Byte 3-6: Block length
     * Byte 7+8: Name length
     * Following Bytes: UTF-16 decoding of group name
     */
    fun getGroupStartBlockBody(name: String): ByteArray {
        val nameLength = name.length +1
        val blockLength = 2 + nameLength * 2
        var result = BlockType.GROUP_START.value.to2Bytes() // Block type
        result += blockLength.to4Bytes() // Block length
        result += nameLength.to2Bytes() // Name length
        for (c in name) result += c.code.to2Bytes() // Group name
        result += ByteArray(2) // Group name ending
        return result
    }

    /**
     * Block structure:
     * Byte 1+2: Block type (Group end: 0xC001)
     * Byte 3-6: Block length (Group end has length 0)
     */
    fun getGroupEndBlockBody(): ByteArray {
        var result = BlockType.GROUP_END.value.to2Bytes() // Block type
        result += 0.to4Bytes() // Block length
        return result
    }

    /**
     * Block structure:
     * Byte 1+2: Block type (Color: 0x0001)
     * Byte 3-6: Block length
     * Byte 7+8: Name length
     * Following Bytes: UTF-16 decoding of color name
     * Next 4 Bytes: Color model (CMYK / RGB / Gray)
     * Next n Bytes: Color values (CMYK => n = 4, RGB => n = 3, Gray => n = 1)
     * Next 2 Bytes: Color type (0 = Global, 1 = Spot, 2 = Normal)
     */
    fun getColorBlockBody(color: Int, name: String, colorType: ColorType): ByteArray {
        val colorModel = ByteArray(1) + ColorModel.RGB.value.toByteArray(Charsets.UTF_8)
        val nameLength = name.length +1
        val blockLength = 2 + nameLength * 2 + 4 + 12 + 2
        var result = BlockType.COLOR.value.to2Bytes() // Block type
        result += blockLength.to4Bytes() // Block length
        result += nameLength.to2Bytes() // Name length
        for (c in name) result += c.code.to2Bytes() // Color name
        result += ByteArray(1)
        result += colorModel // Color model
        result += (color.red / 255f).to4Bytes() // Color value red
        result += (color.green / 255f).to4Bytes() // Color value green
        result += (color.blue / 255f).to4Bytes() // Color value blue
        result += colorType.value.to2Bytes() // Color type
        return result
    }
}