package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll
import be.hcpl.android.filmtag.ui.view.EditFrameViewState

class EditFrameViewModel(
    private val selectedRollId: Long,
    private val selectedFrameId: Int,
    private val filmRollRepository: FilmRollRepository,
) : ViewModel() {

    val state = MutableLiveData<State>()
    val events = MutableLiveData<Event>()

    init {
        refreshData()
    }

    private fun refreshData() {
        val roll = filmRollRepository.getRollById(selectedRollId) ?: Roll()
        // TODO needs safeguard here?
        val frame = filmRollRepository.getFramesForFilm(selectedRollId)[selectedFrameId]
        state.postValue(
            State(
                roll = roll,
                frame = frame,
                editFrameState = EditFrameViewState(
                    roll,
                    frame,
                )
            )
        )
    }


    data class State(
        val roll: Roll,
        val frame: Frame,
        val editFrameState: EditFrameViewState,
    )

    sealed class Event {

    }
}