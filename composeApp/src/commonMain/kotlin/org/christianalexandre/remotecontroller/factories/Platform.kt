package org.christianalexandre.remotecontroller.factories

sealed class Platform {
    object Android : Platform()
    object IOS : Platform()
}

expect fun getPlatform(): Platform