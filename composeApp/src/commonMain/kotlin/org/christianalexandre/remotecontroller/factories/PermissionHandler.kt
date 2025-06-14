package org.christianalexandre.remotecontroller.factories

import androidx.compose.runtime.Composable

@Composable
expect fun LocationPermissionHandler(
    onPermissionResult: (isGranted: Boolean) -> Unit
)