package org.christianalexandre.remotecontroller.data.network

import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.christianalexandre.remotecontroller.common.WebSocketState
import org.christianalexandre.remotecontroller.factories.createHttpClient

object WebsocketClient {
    private val httpClient = createHttpClient()

    private val _state = MutableStateFlow<WebSocketState>(WebSocketState.Disconnected)
    val state: StateFlow<WebSocketState> = _state.asStateFlow()

    private var session: WebSocketSession? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun connect(url: String) {
        if (_state.value is WebSocketState.Connected || _state.value is WebSocketState.Connecting) return

        scope.launch {
            _state.value = WebSocketState.Connecting
            try {
                httpClient.webSocket(urlString = url) {
                    session = this
                    _state.value = WebSocketState.Connected
                    listen()
                }
                _state.value = WebSocketState.Disconnected
                session = null
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Unknown error on connect")
                session = null
            }
        }
    }

    private suspend fun listen() {
        try {
            session?.let { currentSession ->
                for (frame in currentSession.incoming) {
                    if (frame is Frame.Text) {
                        _state.value = WebSocketState.Message(frame.readText())
                    }
                }
            }
        } catch (e: Exception) {
            _state.value = WebSocketState.Error(e.message ?: "Unknown error on listen")
        }
    }

    fun send(message: String) {
        scope.launch {
            if (session == null) {
                _state.value = WebSocketState.Error("Session null on send message")
                return@launch
            }
            try {
                session?.send(Frame.Text(message))
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Unknown error on send message")
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try {
                session?.close()
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Unknown error on disconnect")
            }
        }
    }
}
