package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.model.Settings
import be.hcpl.android.filmtag.ui.tranformer.InputTransformer
import be.hcpl.android.filmtag.ui.view.EditSettingsViewState

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val inputTransformer: InputTransformer,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        with(settingsRepository.retrieveSettings()) {
            state.postValue(
                State(
                    editState = EditSettingsViewState(
                        defaultIso = "$iso",
                        defaultFrameCount = "$frameCount",
                        defaultShutter = "$shutter",
                        defaultAperture = "$aperture",
                    )
                )
            )
        }
    }

    fun saveSettings() {
        // create settings from input state here
        state.value?.editState?.let { editState ->
            val settings = Settings(
                iso = inputTransformer.sanitizeInt(editState.isoState.text),
                frameCount = inputTransformer.sanitizeInt(editState.frameState.text),
                shutter = inputTransformer.sanitizeInt(editState.shutterState.text),
                aperture = inputTransformer.sanitizeDouble(editState.apertureState.text).toFloat(),
            )
            settingsRepository.saveSettings(settings)
            events.postValue(Event.Close)
        }
    }

    data class State(
        val editState: EditSettingsViewState = EditSettingsViewState(
            defaultIso = "200",
            defaultFrameCount = "24",
            defaultShutter = "200",
            defaultAperture = "2.8",
        ),
    )

    sealed class Event {
        data object Close : Event()
    }
}