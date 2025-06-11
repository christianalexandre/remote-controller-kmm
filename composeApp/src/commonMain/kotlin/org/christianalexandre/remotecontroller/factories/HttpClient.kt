package org.christianalexandre.remotecontroller.factories

import io.ktor.client.HttpClient

expect fun createHttpClient(): HttpClient