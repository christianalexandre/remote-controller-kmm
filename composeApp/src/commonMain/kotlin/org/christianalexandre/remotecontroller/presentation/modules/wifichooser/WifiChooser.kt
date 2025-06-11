package org.christianalexandre.remotecontroller.presentation.modules.wifichooser

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import org.christianalexandre.remotecontroller.domain.WebSocketState
import org.christianalexandre.remotecontroller.domain.WebsocketRepository
import org.christianalexandre.remotecontroller.factories.Platform
import org.christianalexandre.remotecontroller.factories.getPlatform
import org.christianalexandre.remotecontroller.factories.getWifiSSID
import org.christianalexandre.remotecontroller.factories.goToWifiSettings
import org.christianalexandre.remotecontroller.presentation.Screen

@Composable
fun WifiChooser(
    websocketRepository: WebsocketRepository,
    onNavigation: (Screen) -> Unit
) {
    // region common
    val platform = getPlatform()
    var hasPermission by remember { mutableStateOf(false) }
    var ssid by remember { mutableStateOf<String?>(null) }
    val webSocketState by websocketRepository.state.collectAsState()
    // endregion

    // region android
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }
    // endregion

    LaunchedEffect(Unit) {
        when (platform) {
            Platform.Android -> {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    hasPermission = true
                }
            }
            Platform.IOS -> hasPermission = true
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            ssid = getWifiSSID()
            delay(3000)
        }
    }

    LaunchedEffect(webSocketState) {
        if (webSocketState is WebSocketState.Connected) {
            onNavigation(Screen.Cockpit)
        }
    }

    Column(
        modifier = Modifier
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

            Button(onClick = { websocketRepository.connect("ws://192.168.4.1:8080") }) {
                Text("Handshake")
            }
        }

        when (val state = webSocketState) {
            is WebSocketState.Error -> {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
