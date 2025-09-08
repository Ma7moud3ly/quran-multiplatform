package com.ma7moud3ly.quran.platform

import platform.Foundation.NSLog

actual object Log {
    actual fun i(tag: String, message: String) {
        NSLog("INFO [$tag]: $message")
    }

    actual fun v(tag: String, message: String) {
        NSLog("VERBOSE [$tag]: $message")
    }

    actual fun w(tag: String, message: String) {
        NSLog("WARN [$tag]: $message")
    }

    actual fun e(tag: String, message: String) {
        NSLog("ERROR [$tag]: $message")
    }
}