package org.christianalexandre.remotecontroller.presentation.modules.wifichooser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.christianalexandre.remotecontroller.common.WebSocketState
import org.christianalexandre.remotecontroller.domain.WebsocketRepository
import org.christianalexandre.remotecontroller.factories.LocationPermissionHandler
import org.christianalexandre.remotecontroller.factories.getWifiSSID
import org.christianalexandre.remotecontroller.factories.goToWifiSettings
import org.christianalexandre.remotecontroller.presentation.Screen

@Composable
fun WifiChooser(
    websocketRepository: WebsocketRepository,
    onNavigation: (Screen) -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }
    var ssid by remember { mutableStateOf<String?>(null) }
    val webSocketState by websocketRepository.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LocationPermissionHandler { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            while (true) {
                ssid = getWifiSSID()
                delay(3000)
            }
        }
    }

    LaunchedEffect(webSocketState) {
        when (val currentState = webSocketState) {
            is WebSocketState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: ${currentState.reason}")
                }
            }
            is WebSocketState.Connected -> {
                onNavigation(Screen.Cockpit)
            }
            else -> { }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .systemBarsPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You're connect in: ${ssid ?: "unknown"}")

            Spacer(modifier = Modifier.height(16.dp))

            if (ssid == null || ssid?.contains("ESP32", ignoreCase = true) == false) {
                Text("Couldn't confirm connection with ESP32 board")

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { goToWifiSettings() }) {
                    Text("Go to Wi-Fi settings")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (ssid == null || ssid?.contains("ESP32", ignoreCase = true) == true) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("If you're connect in ESP32 network go ahead")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { websocketRepository.connect("ws://192.168.4.1:8080") },
                    enabled = webSocketState !is WebSocketState.Connecting
                ) {
                    if (webSocketState is WebSocketState.Connecting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Handshake")
                    }
                }
            }
        }
    }
}
