/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.core

import com.chillibits.adobecolor.generator.ACOBinaryGenerator
import com.chillibits.adobecolor.generator.ASEBinaryGenerator
import com.chillibits.adobecolor.model.AdobeColor

// -------------------------------------------- ACO --------------------------------------------

/**
 * Extension function for List<AdobeColor> to convert it
 * to a byte array, which can be written to a aco file / stream.
 */
fun List<AdobeColor>.toACOBytes(): ByteArray {
    val binGen = ACOBinaryGenerator()

    // Attach V1 header
    var result = binGen.getHeader(ACOBinaryGenerator.ProtocolVersion.V1, size)
    // Attach V1 body
    for (item in this) {
        result += binGen.getColorBytesV1(item.color)
    }

    // Attach V2 header
    result += binGen.getHeader(ACOBinaryGenerator.ProtocolVersion.V2, size)
    // Attach V2 body
    for (item in this) {
        result += binGen.getColorBytesV2(item.color, item.name)
    }

    return result
}

/**
 * Extension function for List<AdobeColor> to convert it
 * to a ACO encoded string, which can be exported to a file
 */
fun List<AdobeColor>.toACOString() = toACOBytes().toString()

// -------------------------------------------- ASE --------------------------------------------

/**
 * Extension function for List<AdobeColor> to convert it
 * to a byte array, which can be written to a aco file / stream.
 */
fun List<AdobeColor>.toASEBytes(): ByteArray {
    val binGen = ASEBinaryGenerator()

    // Attach header
    var result = binGen.getHeader(size)
    // Attach color blocks
    for (color in this) result += binGen.getColorBlockBody(color.color, color.name, ASEBinaryGenerator.ColorType.GLOBAL)

    return result
}

/**
 * Extension function for List<AdobeColor> to convert it
 * to a byte array, which can be written to a aco file / stream.
 * You can use it with a custom palette name.
 */
fun List<AdobeColor>.toASEBytes(paletteName: String): ByteArray {
    val binGen = ASEBinaryGenerator()

    // Attach header
    var result = binGen.getHeader(size + 2)
    // Attach color blocks
    result += binGen.getGroupStartBlockBody(paletteName)
    for (color in this) result += binGen.getColorBlockBody(color.color, color.name, ASEBinaryGenerator.ColorType.NORMAL)
    result += binGen.getGroupEndBlockBody()

    return result
}

/**
 * Extension function for List<AdobeColor> to convert it
 * to a ASE encoded string, which can be exported to a file
 */
fun List<AdobeColor>.toASEString() = toASEBytes().toString()