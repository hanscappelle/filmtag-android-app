package be.hcpl.android.filmtag.ui.transformer

import android.text.TextUtils
import be.hcpl.android.filmtag.model.Location
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TextTransformer {

    private val frameFormat = DecimalFormat("00")
    private val apertureFormat = DecimalFormat("0.#")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    companion object {
        const val TAG_SEPARATOR = ","
        const val TAG_SEPARATOR_SECONDARY = " "
    }

    fun formatFrameNumber(frameNumber: Int): String {
        return "#" + frameFormat.format(frameNumber.toLong())
    }

    fun formatAperture(aperture: Float?): String {
        return "f/" + apertureFormat.format(aperture)
    }

    fun formatShutter(shutter: Int?, longExposure: Boolean): String {
        return (if (longExposure) "" else "1/") + shutter + " s"
    }

    fun formatApertureAndShutter(aperture: Float?, shutter: Int?, longExposure: Boolean): String {
        var str = ""
        if (validNumber(aperture)) {
            str += formatAperture(aperture)
        }
        str += " - "
        if (validNumber(shutter)) {
            str += formatShutter(shutter, longExposure)
        }
        return str
    }

    private fun validNumber(value: Float?): Boolean = value != null && value > 0

    private fun validNumber(value: Int?): Boolean = value != null && value > 0

    fun formatDate(date: Long) = dateFormatter.format(date)

    fun formatLocation(location: Location?) = location?.let { "${location.latitude}, ${location.longitude}" }

    fun formatTags(tags: List<String>) = TextUtils.join("${TAG_SEPARATOR}${TAG_SEPARATOR_SECONDARY}", tags)

}
