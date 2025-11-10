package be.hcpl.android.filmtag.ui.tranformer

import android.text.TextUtils
import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Location
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.split

class TextTransformer(
    private val settingsRepository: SettingsRepository,
) {

    private val frameFormat = DecimalFormat("00")
    private val apertureFormat = DecimalFormat("0.#")
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    val SYSTEM_LINE_SEPARATOR = System.lineSeparator()

    companion object {
        const val TAG_SEPARATOR = ","
        const val TAG_SEPARATOR_SECONDARY = " "
    }

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

    fun formatTags(tags: List<String>) = TextUtils.join("${TAG_SEPARATOR}${TAG_SEPARATOR_SECONDARY}", tags)

    fun formatFrameCount(frameCount: Int?) = frameCount?.let { "$it" } ?: "${settingsRepository.retrieveSettings().frameCount}"

    fun formatIso(iso: Int?) = iso?.let { "$it" } ?: "${settingsRepository.retrieveSettings().iso}"

}
