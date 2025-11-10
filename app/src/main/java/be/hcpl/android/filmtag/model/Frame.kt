package be.hcpl.android.filmtag.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
data class Frame(
    var number: Int = 0,
    var notes: String? = null,
    var shutter: Int? = null,
    var isLongExposure: Boolean = false,
    var aperture: Float? = null,
    var pathToImage: String? = null,
    var location: Location? = null,
    var tags: List<String> = ArrayList(),
    var dateTaken: Long? = null,
) : Serializable {

    override fun toString(): String {
        return StringBuilder(number.toString()).append(" - (s) ").append(shutter.toString()).append(" - (a) f/").append(aperture.toString())
            .toString()
    }

    override fun equals(o: Any?): Boolean {
        return if (this === o) true
        else (o as? Frame)?.number == number
    }

    override fun hashCode(): Int {
        return number
    }

}
