package org.christianalexandre.remotecontroller.factories

actual fun formatFloat(value: Float, decimals: Int): String {
    val validDecimals = decimals.coerceAtLeast(0)
    return String.format("%.${validDecimals}f", value)
}