package com.ma7moud3ly.quran.platform

actual object Log {
    actual fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
    }

    actual fun v(tag: String, message: String) {
        android.util.Log.v(tag, message)
    }

    actual fun w(tag: String, message: String) {
        android.util.Log.w(tag, message)
    }

    actual fun e(tag: String, message: String) {
        android.util.Log.e(tag, message)
    }
}