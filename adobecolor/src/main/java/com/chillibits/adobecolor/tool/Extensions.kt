/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.tool

import com.chillibits.adobecolor.core.ColorBinaryGenerator
import com.chillibits.adobecolor.model.AdobeColor

// -------------------------------------------- ACO --------------------------------------------

/**
 * Extension function for Map<String, Int> representing Map<<name>, <color>> to convert it
 * to a byte array, which can be written to a aco file / stream.
 */
fun List<AdobeColor>.toACOBytes(): ByteArray {
    val binGen = ColorBinaryGenerator()

    // Attach V1 header
    var result = binGen.getHeader(ColorBinaryGenerator.ProtocolVersion.V1, size)
    // Attach V1 body
    for (item in this) {
        result += binGen.getColorBytesV1(item.color)
    }

    // Attach V2 header
    result += binGen.getHeader(ColorBinaryGenerator.ProtocolVersion.V2, size)
    // Attach V2 body
    for (item in this) {
        result += binGen.getColorBytesV2(item.color, item.name)
    }

    return result
}

fun List<AdobeColor>.toACOString() = toACOBytes().toString()

// -------------------------------------------- ASE --------------------------------------------

fun List<AdobeColor>.toASEBytes(): ByteArray {
    return ByteArray(0)
}

fun List<AdobeColor>.toASEString() = toASEBytes().toString()