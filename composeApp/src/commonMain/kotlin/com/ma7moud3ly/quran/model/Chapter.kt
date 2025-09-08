package com.ma7moud3ly.quran.model

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: Int,
    val name: String = "",
    val transliteration: String = "",
    val type: String = "",
    val verses: List<Verse> = listOf(),
    val count: Int = verses.size
) {
    val isMeccan: Boolean = type == "meccan"
    fun idFormatted(i: Int = this.id): String = i.toString().padStart(3, '0')
    
    /**
     * Returns the name of the chapter in the format "surahNNN", where NNN is the chapter ID formatted to 3 digits.
     * This code used for font representation of the sura name with font (surah_name_v4.ttf).
     *
     * @return The chapter name string.
     */
    fun chapterName(): String {
        return "surah${idFormatted()}"
    }
    
    /**
     * Returns the full name of the chapter, including an icon identifier, in the format "surahNNN  surah-icon".
     * This code used for font representation of the sura name with font (surah_name_v4.ttf).
     *
     * @return The full chapter name string.
     */
    fun chapterFullName(): String {
        return "surah${idFormatted()}  surah-icon"
    }
    
    /**
     * Returns the full name of the next chapter, including an icon identifier, if it exists.
     */
    fun nextChapterName(): String? {
        return if (this.id < 114) "surah${idFormatted(this.id + 1)} surah-icon"
        else null
    }
    
    /**
     * Returns the full name of the previous chapter, including an icon identifier, if it exists.
     */
    fun previousChapterName(): String? {
        return if (this.id >= 2) "surah${idFormatted(this.id - 1)} surah-icon"
        else null
    }
}

