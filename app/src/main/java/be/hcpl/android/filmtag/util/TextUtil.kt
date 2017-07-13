package be.hcpl.android.filmtag.util

import java.text.DecimalFormat

object TextUtil {

    private val frameFormat = DecimalFormat("00")
    private val apertureFormat = DecimalFormat("0.#")

    fun formatFrameNumber(frameNumber: Int): String {
        return "#" + frameFormat.format(frameNumber.toLong())
    }

    fun formatAperture(aperture: Double): String {
        return "f/" + apertureFormat.format(aperture)
    }

    fun formatShutter(shutter: Int, longExposure: Boolean): String {
        return (if (longExposure) "" else "1/") + shutter + " s"
    }
}
