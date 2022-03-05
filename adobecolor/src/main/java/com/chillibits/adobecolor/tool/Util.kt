/*
 * Copyright © Marc Auberer 2020-2022. All rights reserved
 */

package com.chillibits.adobecolor.tool

import java.nio.ByteBuffer

fun Int.to2Bytes() = ByteArray(2).apply {
    set(0, ((this@to2Bytes shr 8) and 0xFF).toByte())
    set(1, (this@to2Bytes and 0xFF).toByte())
}

fun Int.to4Bytes() = ByteArray(4).apply {
    set(0, ((this@to4Bytes shr 24) and 0xFF).toByte())
    set(1, ((this@to4Bytes shr 16) and 0xFF).toByte())
    set(2, ((this@to4Bytes shr 8) and 0xFF).toByte())
    set(3, (this@to4Bytes and 0xFF).toByte())
}

fun from2Bytes(b_2: Byte, b_1: Byte) = (b_2.toInt() shl 8) + b_1.toInt()

fun from4Bytes(b_8: Byte, b_4: Byte, b_2: Byte, b_1: Byte) =
    (b_8.toInt() shl 24) + (b_4.toInt() shl 16) + (b_2.toInt() shl 8) + b_1.toInt()

fun Float.to4Bytes(): ByteArray = ByteBuffer.allocate(4).putFloat(this).array()

fun ByteArray.toFloat() = ByteBuffer.wrap(this).float

fun ByteArray.printBytesPretty() = StringBuilder().apply {
    append("[ ")
    for (b in this@printBytesPretty) append(String.format("0x%02X ", b))
    append("]")
}.toString()