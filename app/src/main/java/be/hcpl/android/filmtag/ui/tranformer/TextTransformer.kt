package be.hcpl.android.filmtag.ui.tranformer

import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TextTransformer {

    private val frameFormat = DecimalFormat("00")
    private val apertureFormat = DecimalFormat("0.#")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    val SYSTEM_LINE_SEPARATOR = System.lineSeparator()
    val TAG_SEPARATOR = ", "

    fun formatFrameNumber(frameNumber: Int): String {
        return "#" + frameFormat.format(frameNumber.toLong())
    }

    fun formatAperture(aperture: Double): String {
        return "f/" + apertureFormat.format(aperture)
    }

    fun formatShutter(shutter: Int, longExposure: Boolean): String {
        return (if (longExposure) "" else "1/") + shutter + " s"
    }

    fun formatApertureAndShutter(aperture: Double, shutter: Int, longExposure: Boolean): String {
        var str = ""
        if (aperture != Frame.EMPTY_VALUE.toDouble()) {
            str += formatAperture(aperture)
        }
        if (aperture != Frame.EMPTY_VALUE.toDouble() && shutter != Frame.EMPTY_VALUE) {
            str += " - "
        }
        if (shutter != Frame.EMPTY_VALUE) {
            str += formatShutter(shutter, longExposure)
        }
        return str
    }

    fun formatDate(date: Long) = dateFormatter.format(date)

    fun formatLocation(location: Location?) = location?.let { "${location.latitude}, ${location.longitude}" }

}
