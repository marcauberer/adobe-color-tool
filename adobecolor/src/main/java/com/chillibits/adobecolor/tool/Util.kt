package com.chillibits.adobecolor.tool

fun Int.to2Bytes() = ByteArray(2).apply {
    set(0, ((this@to2Bytes shr 8) and 0xFF).toByte())
    set(1, (this@to2Bytes and 0xFF).toByte())
}