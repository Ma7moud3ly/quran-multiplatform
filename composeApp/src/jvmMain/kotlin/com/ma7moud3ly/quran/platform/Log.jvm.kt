package com.ma7moud3ly.quran.platform

actual object Log {
    actual fun i(tag: String, message: String) {
        if (AppConfig.isDebug) println("INFO [$tag]: $message")
    }

    actual fun v(tag: String, message: String) {
        if (AppConfig.isDebug) println("VERBOSE [$tag]: $message")
    }

    actual fun w(tag: String, message: String) {
        if (AppConfig.isDebug) println("WARN [$tag]: $message")
    }

    actual fun e(tag: String, message: String) {
        if (AppConfig.isDebug) println("ERROR [$tag]: $message")
    }
}