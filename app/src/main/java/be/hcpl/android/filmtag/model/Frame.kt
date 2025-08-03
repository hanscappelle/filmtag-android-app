package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
data class Frame(
    var isLongExposure: Boolean = false,
    var notes: String? = null,
    var number: Int = 0,
    var shutter: Int = EMPTY_VALUE,
    var aperture: Double = EMPTY_VALUE.toDouble(),
    var pathToImage: String? = null,
    var location: Location? = null,
    var tags: List<String> = ArrayList(),
) : Serializable {

    override fun toString(): String {
        return StringBuilder(number.toString()).append(" - (s) ").append(shutter.toString()).append(" - (a) f/").append(aperture.toString()).toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Frame) return false

        val frame = o as Frame?

        return number == frame!!.number

    }

    override fun hashCode(): Int {
        return number
    }

    companion object {

        /**
         * The value that signifies unspecified aperture or shutter value
         */
        const val EMPTY_VALUE = 0
    }
}
