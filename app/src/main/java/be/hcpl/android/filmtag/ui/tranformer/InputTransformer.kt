package be.hcpl.android.filmtag.ui.tranformer

class InputTransformer {

    private val digitsInt = ("[^\\d]").toRegex()
    private val digitsDouble = ("[^\\d.]").toRegex()

    fun sanitizeInt(text: String?): Int {
        return text?.replace(digitsInt, "")?.toInt() ?: 0
    }

    fun sanitizeDouble(text: String?): Double {
        return try {
            // TODO also check for too many '.' in the text
            text?.replace(",", ".")?.replace(digitsDouble, "")?.toDouble() ?: 0.0
        } catch (_: Exception) {
            0.0
        }
    }
}