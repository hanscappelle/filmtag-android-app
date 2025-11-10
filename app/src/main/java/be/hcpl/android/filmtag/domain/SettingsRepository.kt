package be.hcpl.android.filmtag.domain

import android.content.SharedPreferences
import be.hcpl.android.filmtag.model.Settings

class SettingsRepository(
    preferencesProvider: SharedPreferencesProvider,
) {

    private val keyIso = "key_default_iso"
    private val keyFrameCount = "key_default_frames"
    private val keyShutter = "key_default_shutter"
    private val keyAperture = "key_default_aperture"
    private val keyLimitNotesPreview = "key_limit_notes_preview"

    private val defaultIso = "200"
    private val defaultFrameCount = "24"
    private val defaultShutter = "200"
    private val defaultAperture = "2.8"
    private val defaultPreviewLength = 0

    private val prefs: SharedPreferences = preferencesProvider.sharedPreferences

    fun retrieveSettings() = Settings(
        frameCount = prefs.getString(keyFrameCount, defaultFrameCount) ?: defaultFrameCount,
        iso = prefs.getString(keyIso, defaultIso) ?: defaultIso,
        shutter = prefs.getString(keyShutter, defaultShutter) ?: defaultShutter,
        aperture = prefs.getString(keyAperture, defaultAperture) ?: defaultAperture,
        limitNotesPreview = prefs.getInt(keyLimitNotesPreview, defaultPreviewLength),
    )

    fun saveSettings(settings: Settings) {
        prefs.edit()
            .putString(keyFrameCount, settings.frameCount)
            .putString(keyIso, settings.iso)
            .putString(keyShutter, settings.shutter)
            .putString(keyAperture, settings.aperture)
            .putInt(keyLimitNotesPreview, settings.limitNotesPreview)
            .apply()
    }
}