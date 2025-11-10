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

    private val defaultIso = 200
    private val defaultFrameCount = 24
    private val defaultShutter = 200
    private val defaultAperture = 2.8f

    private val prefs: SharedPreferences = preferencesProvider.sharedPreferences

    fun retrieveSettings() = Settings(
        frameCount = prefs.getInt(keyFrameCount, defaultFrameCount),
        iso = prefs.getInt(keyIso, defaultIso),
        shutter = prefs.getInt(keyShutter, defaultShutter),
        aperture = prefs.getFloat(keyAperture, defaultAperture),
    )

    fun saveSettings(settings: Settings) {
        prefs.edit()
            .putInt(keyFrameCount, settings.frameCount)
            .putInt(keyIso, settings.iso)
            .putInt(keyShutter, settings.shutter)
            .putFloat(keyAperture, settings.aperture)
            .apply()
    }
}