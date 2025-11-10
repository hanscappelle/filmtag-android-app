package be.hcpl.android.filmtag.ui.tranformer

import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer.Companion.TAG_SEPARATOR

class InputTransformer(
    private val settingsRepository: SettingsRepository,
) {

    private val digitsInt = ("[^\\d]").toRegex()
    private val digitsDouble = ("[^\\d.]").toRegex()

    fun sanitizeInt(text: CharSequence?): Int {
        return try {
            text?.replace(digitsInt, "")?.toInt() ?: 0
        } catch (_: Exception) {
            0
        }
    }

    fun sanitizeFloat(text: CharSequence?): Float {
        return try {
            // TODO also check for too many '.' in text
            text?.toString()?.replace(",", ".")?.replace(digitsDouble, "")?.toFloat() ?: 0.0f
        } catch (_: Exception) {
            0.0f
        }
    }

    fun sanitizeList(text: CharSequence?): List<String> {
        return text?.split(TAG_SEPARATOR)?.map { it.trim() } ?: emptyList()
    }

    fun formatFrameCount(frameCount: Int?) = frameCount?.takeIf { it > 0 }?.let { "$it" } ?: "${settingsRepository.retrieveSettings().frameCount}"

    fun formatIso(iso: Int?) = iso?.takeIf { it > 0 }?.let { "$it" } ?: "${settingsRepository.retrieveSettings().iso}"

    fun formatShutter(shutter: Int?) = shutter?.takeIf { it > 0 }?.let { "$it" } ?: "${settingsRepository.retrieveSettings().shutter}"

    fun formatAperture(aperture: Float?) = aperture?.takeIf { it > 0 }?.let { "$it" } ?: "${settingsRepository.retrieveSettings().aperture}"

}