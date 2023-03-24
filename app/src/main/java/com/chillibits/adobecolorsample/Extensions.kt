/*
 * Copyright © Marc Auberer 2020-2023. All rights reserved
 */

package com.chillibits.adobecolorsample

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import kotlin.math.max
import kotlin.math.roundToInt

fun ImageView.setTint(color: Int) =
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))