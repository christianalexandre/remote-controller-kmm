package org.christianalexandre.remotecontroller.factories

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.websocket.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(Darwin) {
        install(WebSockets)
    }
}