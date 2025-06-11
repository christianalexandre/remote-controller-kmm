package org.christianalexandre.remotecontroller.data.repositories

import kotlinx.coroutines.flow.StateFlow
import org.christianalexandre.remotecontroller.common.WebSocketState
import org.christianalexandre.remotecontroller.data.network.WebsocketClient
import org.christianalexandre.remotecontroller.domain.WebsocketRepository

class WebsocketRepositoryImpl(
    private val client: WebsocketClient = WebsocketClient
) : WebsocketRepository {

    override val state: StateFlow<WebSocketState> = client.state

    override fun connect(url: String) = client.connect(url)

    override fun send(message: String) = client.send(message)

    override fun disconnect() = client.disconnect()
}
