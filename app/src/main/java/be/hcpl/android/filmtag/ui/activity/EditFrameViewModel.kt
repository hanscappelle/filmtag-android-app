package be.hcpl.android.filmtag.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.model.Frame
import be.hcpl.android.filmtag.model.Roll

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
        // TODO implement this
        state.postValue(
            State(
                roll = filmRollRepository.getRollById(selectedRollId) ?: Roll(),
                frameId = selectedFrameId
            )
        )
    }


    data class State(
        val roll: Roll,
        val frameId: Int,
    )

    sealed class Event {

    }
}