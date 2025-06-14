package org.christianalexandre.remotecontroller.presentation.modules.cockpit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.christianalexandre.remotecontroller.common.WebSocketState
import org.christianalexandre.remotecontroller.domain.WebsocketRepository
import org.christianalexandre.remotecontroller.factories.formatFloat
import org.christianalexandre.remotecontroller.presentation.Screen
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cockpit(
    websocketRepository: WebsocketRepository,
    onNavigation: (Screen) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var joystickState by remember { mutableStateOf(Pair(0f, 0f)) }
    val webSocketState by websocketRepository.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val joystickSize = 275.dp
    val thumbSize = 70.dp
    val joystickRadius = joystickSize.value / 2f
    val thumbRadius = thumbSize.value / 2f
    val maxJoystickDisplacement = joystickRadius - thumbRadius

    LaunchedEffect(webSocketState) {
        if (webSocketState is WebSocketState.Connected) {
            while (isActive) {
                val (x, y) = joystickState
                val jsonMessage = "{\"x\": ${x.coerceIn(-1f, 1f)}, \"y\": ${y.coerceIn(-1f, 1f)}}"
                websocketRepository.send(jsonMessage)
                delay(100)
            }
        }
    }

    LaunchedEffect(webSocketState) {
        if (webSocketState is WebSocketState.Disconnected || webSocketState is WebSocketState.Error) {
            onNavigation(Screen.WifiChooser)
            snackbarHostState.showSnackbar("Disconnected")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Normalized X: ${formatFloat(joystickState.first, 2)}")
                Text(text = "Normalized X: ${formatFloat(joystickState.second, 2)}")
            }

            Box(
                modifier = Modifier
                    .size(joystickSize)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                offsetX = 0f
                                offsetY = 0f
                                joystickState = Pair(0f, 0f)
                            }
                        ) { change, dragAmount ->
                            change.consume()

                            val newOffsetX = offsetX + dragAmount.x
                            val newOffsetY = offsetY + dragAmount.y
                            val distance = sqrt(newOffsetX * newOffsetX + newOffsetY * newOffsetY)

                            if (distance > maxJoystickDisplacement) {
                                val angle = atan2(newOffsetY, newOffsetX)
                                offsetX = (maxJoystickDisplacement * cos(angle))
                                offsetY = (maxJoystickDisplacement * sin(angle))
                            } else {
                                offsetX = newOffsetX
                                offsetY = newOffsetY
                            }

                            joystickState = Pair(
                                offsetX / maxJoystickDisplacement,
                                -offsetY / maxJoystickDisplacement
                            )
                        }
                    }
            ) {
                val circleColor = MaterialTheme.colorScheme.surfaceVariant
                val strokeWidthInPx = with(LocalDensity.current) { 2.dp.toPx() }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = circleColor,
                        radius = joystickRadius,
                        style = Stroke(width = strokeWidthInPx)
                    )
                }

                Box(
                    modifier = Modifier
                        .offset(x = offsetX.dp, y = offsetY.dp)
                        .size(thumbSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Center)
                )
            }

            Button(onClick = { websocketRepository.disconnect() }) {
                Text("Disconnect")
            }
        }
    }
}