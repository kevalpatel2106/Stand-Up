package com.kevalpatel2106.utils

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
fun String.toFloatSafe(): Float = if (isNullOrEmpty()) 0F else toFloat()

fun String.toIntSafe(): Int = if (isNullOrEmpty()) 0 else toInt()