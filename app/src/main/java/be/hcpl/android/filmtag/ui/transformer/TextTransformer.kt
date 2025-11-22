package be.hcpl.android.filmtag.ui.transformer

import android.text.TextUtils
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import be.hcpl.android.filmtag.model.Roll
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

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

    fun formatDateAndTime(date: Long?, time: Int?) =
        when {
            date != null && time != null -> "${formatDate(date)} ${formatTime(time)}"
            date != null && time == null -> formatDate(date)
            date == null && time != null -> formatTime(time)
            else -> "-"
        }

    fun formatLocation(location: Location?) = location?.let { "${location.latitude}, ${location.longitude}" }

    fun formatTags(tags: List<String>) = TextUtils.join("${TAG_SEPARATOR}${TAG_SEPARATOR_SECONDARY}", tags)

    fun formatRoll(roll: Roll) = "film: ${roll.type?.ifEmpty { "..." } ?: "..."} @ ${roll.speed} # ${roll.frames}" +
            "\ntags: ${formatTags(roll.tags)}" +
            "\nnotes: ${roll.notes}" +
            "\ndeveloped: ${
                if (roll.isDeveloped) {
                    "Yes"
                } else {
                    "No"
                }
            }"

    fun formatFrame(frame: Frame) = "\n#${frame.number}" +
            " exposed at ${formatApertureAndShutter(frame.aperture, frame.shutter, frame.isLongExposure)}" +
            "\ntags: ${formatTags(frame.tags).ifEmpty { "-" }}" +
            "\nnotes: ${frame.notes?.ifEmpty { "-" } ?: "-"}" +
            "\ndate: ${frame.dateTaken?.let { formatDate(it) } ?: "-"}" +
            "\nlocation: ${formatLocation(frame.location) ?: "-"}"

    fun formatTime(hour: Int, minutes: Int) = "$hour:$minutes"

    fun formatTime(hourAndMinutes: Int): String {
        // it's really just a ':' in between :-)
        val hour = (hourAndMinutes.toFloat() / 100).roundToInt()
        val minutes = hourAndMinutes - hour * 100
        return formatTime(hour, minutes)
    }

}
