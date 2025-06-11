package org.christianalexandre.remotecontroller.factories

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(WebSockets)
    }
}