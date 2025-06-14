package org.christianalexandre.remotecontroller.presentation.modules.cockpit

import kotlin.test.Test
import kotlin.test.assertEquals

// Values from Cockpit.kt for joystick dimensions
private const val JOYSTICK_SIZE_DP_VALUE = 275f // as in 275.dp
private const val THUMB_SIZE_DP_VALUE = 70f    // as in 70.dp

class CockpitLogicTest {

    private fun calculateMaxJoystickDisplacement(joystickSize: Float, thumbSize: Float): Float {
        val joystickRadius = joystickSize / 2f
        val thumbRadius = thumbSize / 2f
        return joystickRadius - thumbRadius
    }

    private fun normalizeOffset(offset: Float, maxDisplacement: Float): Float {
        if (maxDisplacement == 0f) return 0f
        return offset / maxDisplacement
    }

    @Test
    fun testMaxJoystickDisplacementCalculation() {
        val expectedMaxDisplacement = (JOYSTICK_SIZE_DP_VALUE / 2f) - (THUMB_SIZE_DP_VALUE / 2f) // 137.5 - 35 = 102.5
        val actualMaxDisplacement = calculateMaxJoystickDisplacement(JOYSTICK_SIZE_DP_VALUE, THUMB_SIZE_DP_VALUE)
        assertEquals(expectedMaxDisplacement, actualMaxDisplacement, 0.001f, "Max joystick displacement calculation should be correct.")
    }

    @Test
    fun testNormalizationLogic() {
        val maxDisplacement = calculateMaxJoystickDisplacement(JOYSTICK_SIZE_DP_VALUE, THUMB_SIZE_DP_VALUE) // Should be 102.5f

        // Test case 1: Zero displacement
        var offsetX = 0f
        var offsetY = 0f
        var normalizedX = normalizeOffset(offsetX, maxDisplacement)
        var normalizedY = normalizeOffset(-offsetY, maxDisplacement) // Y is inverted in Cockpit.kt
        assertEquals(0f, normalizedX, 0.001f, "Normalized X should be 0 for zero displacement.")
        assertEquals(0f, normalizedY, 0.001f, "Normalized Y should be 0 for zero displacement.")

        // Test case 2: Partial displacement (e.g., halfway to max)
        offsetX = maxDisplacement / 2f // 51.25f
        offsetY = maxDisplacement / 4f // 25.625f
        normalizedX = normalizeOffset(offsetX, maxDisplacement)
        normalizedY = normalizeOffset(-offsetY, maxDisplacement)
        assertEquals(0.5f, normalizedX, 0.001f, "Normalized X should be 0.5 for halfway displacement.")
        assertEquals(-0.25f, normalizedY, 0.001f, "Normalized Y should be -0.25 for quarter displacement.")

        // Test case 3: Full displacement
        offsetX = maxDisplacement // 102.5f
        offsetY = -maxDisplacement // -102.5f (testing negative Y input for positive Y output due to inversion)
        normalizedX = normalizeOffset(offsetX, maxDisplacement)
        normalizedY = normalizeOffset(-offsetY, maxDisplacement)
        assertEquals(1.0f, normalizedX, 0.001f, "Normalized X should be 1.0 for full displacement.")
        assertEquals(1.0f, normalizedY, 0.001f, "Normalized Y should be 1.0 for full negative Y displacement (inverted).")

        // Test case 4: Displacement beyond max (raw normalization, not clamped as in Composable)
        // The Composable itself has logic to cap offsetX/Y at maxDisplacement.
        // Here we test the raw normalization function.
        offsetX = maxDisplacement * 1.5f // 153.75f
        offsetY = -maxDisplacement * 2f // -205.0f
        normalizedX = normalizeOffset(offsetX, maxDisplacement)
        normalizedY = normalizeOffset(-offsetY, maxDisplacement)
        assertEquals(1.5f, normalizedX, 0.001f, "Normalized X should be 1.5 for 1.5x max displacement.")
        assertEquals(2.0f, normalizedY, 0.001f, "Normalized Y should be 2.0 for 2x max negative Y displacement (inverted).")

        // Note: The actual values sent via WebSocket are coercedIn(-1f, 1f) in Cockpit.kt.
        // This test focuses on the raw normalization before coercion.
    }
}
