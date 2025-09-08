package com.ma7moud3ly.quran.model


sealed interface VersesMode {
    data object Single : VersesMode
    data object Multiple : VersesMode
    data object Continues : VersesMode
}

val VersesMode.isContinues: Boolean get() = this is VersesMode.Continues
val VersesMode.isSingle: Boolean get() = this is VersesMode.Single