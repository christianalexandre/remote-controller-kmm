package org.christianalexandre.remotecontroller

import androidx.compose.ui.window.ComposeUIViewController
import org.christianalexandre.remotecontroller.presentation.App

fun MainViewController() = ComposeUIViewController { App() }