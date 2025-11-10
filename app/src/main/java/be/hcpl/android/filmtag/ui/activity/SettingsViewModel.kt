package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.SettingsRepository
import be.hcpl.android.filmtag.ui.view.EditSettingsViewState

class SettingsViewModel(
    private val settings: SettingsRepository,
): ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init{
        state.postValue(
            State(
                editState = EditSettingsViewState(
                    defaultIso = "200",
                    defaultFrameCount = "24",
                    defaultShutter = "200",
                    defaultAperture = "2.8",
                )
            )

        )
    }

    fun saveSettings() {
        // TODO settings.saveSettings()

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