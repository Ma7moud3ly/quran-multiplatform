package com.ma7moud3ly.quran.platform

expect object Log {
    fun i(tag: String, message: String)
    fun v(tag: String, message: String)
    fun w(tag: String, message: String)
    fun e(tag: String, message: String)
}
