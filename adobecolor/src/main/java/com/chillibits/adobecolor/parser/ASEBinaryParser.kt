/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.adobecolor.parser

import com.chillibits.adobecolor.model.AdobeColor

class ASEBinaryParser {

    fun parse(data: ByteArray): List<AdobeColor> {
        return emptyList()
    }

    fun isASE(data: ByteArray) = data[0].toInt() == 65 && data[1].toInt() == 83 &&
            data[2].toInt() == 69 && data[3].toInt() == 70
}