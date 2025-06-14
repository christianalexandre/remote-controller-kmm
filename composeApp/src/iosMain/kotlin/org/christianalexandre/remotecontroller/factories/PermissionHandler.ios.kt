package org.christianalexandre.remotecontroller.factories

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun LocationPermissionHandler(
    onPermissionResult: (isGranted: Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        onPermissionResult(true)
    }
}