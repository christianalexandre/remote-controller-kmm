package org.christianalexandre.remotecontroller.factories

import kotlin.math.pow
import kotlin.math.roundToInt

actual fun formatFloat(value: Float, decimals: Int): String {
    if (decimals < 0) return value.toString()

    val factor = 10.0.pow(decimals)
    val roundedValue = (value * factor).roundToInt() / factor
    return roundedValue.toString()
}