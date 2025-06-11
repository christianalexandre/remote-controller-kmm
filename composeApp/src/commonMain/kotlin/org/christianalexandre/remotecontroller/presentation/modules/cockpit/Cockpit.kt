package org.christianalexandre.remotecontroller.presentation.modules.cockpit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.christianalexandre.remotecontroller.domain.WebSocketState
import org.christianalexandre.remotecontroller.domain.WebsocketRepository
import org.christianalexandre.remotecontroller.presentation.Screen
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Cockpit(
    websocketRepository: WebsocketRepository,
    onNavigation: (Screen) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val webSocketState by websocketRepository.state.collectAsState()

    val joystickSize = 200.dp
    val thumbSize = 50.dp
    val joystickRadius = joystickSize.value / 2f // Use float for calculations
    val thumbRadius = thumbSize.value / 2f   // Use float for calculations
    val maxJoystickDisplacement = joystickRadius - thumbRadius

    // Calculate normalized values
    val normalizedX = offsetX / maxJoystickDisplacement
    // Y is typically inverted in screen coordinates, so we might send -offsetY
    // or adjust on the receiver side. For now, let's send it as calculated.
    val normalizedY = -offsetY / maxJoystickDisplacement


    LaunchedEffect(normalizedX, normalizedY) {
        // Only send if connected
        if (webSocketState is WebSocketState.Connected) {
            val jsonMessage = "{\"x\": ${normalizedX.coerceIn(-1f, 1f)}, \"y\": ${normalizedY.coerceIn(-1f, 1f)}}"
            websocketRepository.send(jsonMessage)
        }
    }

    LaunchedEffect(webSocketState) {
        if (webSocketState is WebSocketState.Disconnected || webSocketState is WebSocketState.Error) {
            onNavigation(Screen.WifiChooser)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Changed to SpaceBetween
    ) {
        // Content at the top (like Title or status)
        Text("Cockpit", style = MaterialTheme.typography.headlineMedium)

        // Normalized values display (optional, can be removed)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Normalized X: %.2f".format(normalizedX))
            Text(text = "Normalized Y: %.2f".format(normalizedY))
        }

        // Joystick Area
        Box(
            modifier = Modifier
                .size(joystickSize)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            offsetX = 0f
                            offsetY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()

                        val newOffsetX = offsetX + dragAmount.x
                        val newOffsetY = offsetY + dragAmount.y

                        val distance = sqrt(newOffsetX * newOffsetX + newOffsetY * newOffsetY)

                        if (distance > maxJoystickDisplacement) {
                            // Normalize the position to keep the thumb on the edge
                            val angle = atan2(newOffsetY, newOffsetX)
                            offsetX = (maxJoystickDisplacement * cos(angle))
                            offsetY = (maxJoystickDisplacement * sin(angle))
                        } else {
                            offsetX = newOffsetX
                            offsetY = newOffsetY
                        }
                    }
                }
        ) {
            // Outer Circle (Base)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.LightGray,
                    radius = joystickRadius, // Use the original joystickRadius for drawing
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Inner Circle (Thumb)
            Box(
                modifier = Modifier
                    .offset(
                        x = (offsetX).dp,
                        y = (offsetY).dp
                    )
                    .size(thumbSize)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .align(Alignment.Center) // Align thumb to the center of the Box initially
            )
        }

        // Content at the bottom (like the back button)
        Button(onClick = { onNavigation(Screen.WifiChooser) }) {
            Text("Voltar")
        }
    }
}
