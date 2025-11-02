package be.hcpl.android.filmtag.util

import be.hcpl.android.filmtag.model.Frame
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

object TextUtil {

    // TODO convert to some sort of transformer and inject

    private val frameFormat = DecimalFormat("00")
    private val apertureFormat = DecimalFormat("0.#")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

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
            str += TextUtil.formatAperture(aperture)
        }
        if (aperture != Frame.EMPTY_VALUE.toDouble() && shutter != Frame.EMPTY_VALUE) {
            str += " - "
        }
        if (shutter != Frame.EMPTY_VALUE) {
            str += TextUtil.formatShutter(shutter, longExposure)
        }
        return str
    }

    fun formatDate(date: Long) = dateFormatter.format(date)

    val SYSTEM_LINE_SEPARATOR = System.lineSeparator()
}
