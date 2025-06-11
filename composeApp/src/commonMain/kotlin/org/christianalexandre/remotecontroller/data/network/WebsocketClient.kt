package org.christianalexandre.remotecontroller.data.network

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.christianalexandre.remotecontroller.factories.createHttpClient
import org.christianalexandre.remotecontroller.common.WebSocketState

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
                    send("connected")
                    listen(this)
                }
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    private fun listen(session: WebSocketSession) {
        scope.launch {
            try {
                while (scope.isActive) {
                    val frame = session.incoming.receive()
                    if (frame is Frame.Text) {
                        _state.value = WebSocketState.Message(frame.readText())
                    }
                }
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Erro ao receber mensagem")
                disconnect()
            }
        }
    }

    fun send(message: String) {
        scope.launch {
            try {
                (session as? DefaultClientWebSocketSession)?.send(Frame.Text(message))
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Erro ao enviar mensagem")
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try {
                session?.close()
                session = null
                _state.value = WebSocketState.Disconnected
            } catch (e: Exception) {
                _state.value = WebSocketState.Error(e.message ?: "Erro ao desconectar")
            }
        }
    }
}
