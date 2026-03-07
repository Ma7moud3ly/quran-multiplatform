package com.ma7moud3ly.quran.platform

import android.widget.Toast

actual object Toast {
    actual fun show(message: String) {
        val context = AndroidApp.requireContext()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}