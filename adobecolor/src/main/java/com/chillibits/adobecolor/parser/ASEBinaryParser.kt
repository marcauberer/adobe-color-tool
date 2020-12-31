/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.parser

import com.chillibits.adobecolor.model.AdobeColor
import com.chillibits.adobecolor.tool.from2Bytes
import com.chillibits.adobecolor.tool.from4Bytes

class ASEBinaryParser(data: ByteArray) {

    private var stack = data

    fun parse(): List<AdobeColor> {



        return emptyList()
    }

    private fun pop2BytesFront(): Int {
        val value = from2Bytes(stack[0], stack[1])
        stack = stack.sliceArray(2 until stack.size)
        return value
    }

    private fun pop4BytesFront(): Int {
        val value = from4Bytes(stack[0], stack[1], stack[2], stack[3])
        stack = stack.sliceArray(4 until stack.size)
        return value
    }

    fun isASE(data: ByteArray) = data[0].toInt() == 65 && data[1].toInt() == 83 &&
            data[2].toInt() == 69 && data[3].toInt() == 70
}