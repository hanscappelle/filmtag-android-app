package be.hcpl.android.filmtag.model

import java.io.Serializable
import java.util.ArrayList

class Frame : Serializable {

    /**
     * check for long exposures so we can show full seconds
     */
    var isLongExposure = false

    /**
     * notes for this frame
     */
    var notes: String? = null

    /**
     * the frame number, set by the system based on the number of frames of a film roll
     */
    var number: Int = 0

    /**
     * the shutter speed used for this frame
     */
    var shutter = EMPTY_VALUE

    /**
     * the aperture value used for this frame
     */
    var aperture = EMPTY_VALUE.toDouble()

    /**
     * path to selected preview image
     */
    var pathToImage: String? = null

    /**
     * where the picture was taken
     */
    var location: Location? = null

    /**
     * tags for frame
     */
    var tags: List<String> = ArrayList()

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
