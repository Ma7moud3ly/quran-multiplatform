package com.ma7moud3ly.quran.platform

import android.media.MediaMetadataRetriever
import java.io.ByteArrayOutputStream

actual suspend fun getVideoThumbnail(videoPath: String, timeMs: Long): ByteArray? {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(videoPath)
        val bitmap = retriever.getFrameAtTime(
            timeMs * 1000,
            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
        )
        return bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            stream.toByteArray()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        retriever.release()
    }
}