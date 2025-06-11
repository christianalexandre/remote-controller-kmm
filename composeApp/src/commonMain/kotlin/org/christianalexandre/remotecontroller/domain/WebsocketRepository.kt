package org.christianalexandre.remotecontroller.domain

import kotlinx.coroutines.flow.StateFlow
import org.christianalexandre.remotecontroller.common.WebSocketState

interface WebsocketRepository {
    val state: StateFlow<WebSocketState>
    fun connect(url: String)
    fun send(message: String)
    fun disconnect()
}