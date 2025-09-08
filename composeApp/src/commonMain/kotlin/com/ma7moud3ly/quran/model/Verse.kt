package com.ma7moud3ly.quran.model

import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val id: Int,
    val text: String,
    val textNormalized: String = "",
    val transliteration: String = ""
) {
    val verseNumber: String get() = id.asVerseNumber()
    /**
     * Formats the verse ID by padding it with leading zeros to ensure it is 3 digits long.
     * For example, if the ID is 1, it will be formatted as "001".
     * @return A string representation of the formatted verse ID.
     */
    fun idFormatted(): String = this.id.toString().padStart(3, '0')

    /**
     * Generates the MP3 filename for the verse based on the chapter ID and verse ID.
     *
     * The filename is formatted as "CCCVVV.mp3", where CCC is the chapter ID padded with leading zeros
     * to a length of 3, and VVV is the verse ID padded with leading zeros to a length of 3.
     *
     * @param chapterId The ID of the chapter.
     * @return The MP3 filename.
     */
    fun mp3FileName(chapterId: Int): String {
        val chapterIdFormatted = chapterId.toString().padStart(3, '0')
        val verseIdFormatted = this.idFormatted()
        return "$chapterIdFormatted$verseIdFormatted.mp3"
    }
}


/**
 * Converts an integer to its corresponding Quranic verse number symbol.
 *
 * This function takes an integer representing a verse number and returns
 * the Unicode character that visually represents that number in the Quran.
 * It handles verse numbers from 1 to 286 (the maximum number of verses in a Surah).
 * If the input integer is outside this range, an empty string is returned.
 *
 * The conversion relies on the fact that the Unicode characters for Quranic verse
 * numbers are sequential, starting from `\uE95A` for verse number 1.
 * These Unicode characters are typically rendered correctly when using font "hafs_smart.ttf".
 *
 * @return A string containing the Unicode character for the verse number,
 *         or an empty string if the input is out of range.
 * @receiver The integer representing the verse number.
 */
fun Int.asVerseNumber(): String {
    if (this < 1 || this > 286) return ""
    // The Unicode values are sequential.
    // \uE95A is the character for number 1.
    // So, for number 'n', the character is \uE95A + (n - 1)
    val baseUnicode = 0xE95A
    val targetUnicode = baseUnicode + (this - 1)
    return targetUnicode.toChar().toString()
}
