package org.christianalexandre.remotecontroller.common

sealed class WebSocketState {
    object Idle : WebSocketState()
    object Connecting : WebSocketState()
    object Connected : WebSocketState()
    data class Message(val content: String) : WebSocketState()
    data class Error(val reason: String) : WebSocketState()
    object Disconnected : WebSocketState()
}