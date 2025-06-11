package org.christianalexandre.remotecontroller.presentation.modules.cockpit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.christianalexandre.remotecontroller.domain.WebsocketRepository
import org.christianalexandre.remotecontroller.presentation.Screen

@Composable
fun Cockpit(
    websocketRepository: WebsocketRepository,
    onNavigation: (Screen) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Cockpit")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigation(Screen.WifiChooser)  }) {
            Text("Voltar")
        }
    }
}
