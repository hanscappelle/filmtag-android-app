package be.hcpl.android.filmtag.ui.tranformer

import be.hcpl.android.filmtag.ui.tranformer.TextTransformer.Companion.TAG_SEPARATOR

class InputTransformer {

    private val digitsInt = ("[^\\d]").toRegex()
    private val digitsDouble = ("[^\\d.]").toRegex()

    fun sanitizeInt(text: CharSequence?): Int {
        return text?.replace(digitsInt, "")?.toInt() ?: 0
    }

    fun sanitizeDouble(text: CharSequence?): Double {
        return try {
            // TODO also check for too many '.' in the text
            text?.toString()?.replace(",", ".")?.replace(digitsDouble, "")?.toDouble() ?: 0.0
        } catch (_: Exception) {
            0.0
        }
    }

    fun sanitizeList(text: CharSequence?): List<String> {
        return text?.split(TAG_SEPARATOR)?.map { it.trim() } ?: emptyList()
    }

}