package org.christianalexandre.remotecontroller.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.christianalexandre.remotecontroller.data.network.WebsocketClient
import org.christianalexandre.remotecontroller.data.repositories.WebsocketRepositoryImpl
import org.christianalexandre.remotecontroller.factories.createHttpClient
import org.christianalexandre.remotecontroller.presentation.modules.cockpit.Cockpit
import org.christianalexandre.remotecontroller.presentation.modules.wifichooser.WifiChooser
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen {
    object WifiChooser : Screen()
    object Cockpit : Screen()
}

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.WifiChooser) }
    val websocketRepository = remember { WebsocketRepositoryImpl() }
    val onNavigation: (Screen) -> Unit = { currentScreen = it }

    when (currentScreen) {
        is Screen.WifiChooser -> WifiChooser(websocketRepository, onNavigation)
        is Screen.Cockpit -> Cockpit(websocketRepository, onNavigation)
    }
}